package com.propertymanagement.controller;

import com.propertymanagement.dto.PaymentDTO;
import com.propertymanagement.dto.PaymentMethodDTO;
import com.propertymanagement.dto.NewCardDTO;
import com.propertymanagement.mapper.PaymentMapper;
import com.propertymanagement.model.Payment;
import com.propertymanagement.model.User;
import com.propertymanagement.model.Lease;
import com.propertymanagement.model.PaymentStatus;
import com.propertymanagement.service.PaymentService;
import com.propertymanagement.service.UserService;
import com.propertymanagement.service.RazorpayService;
import com.propertymanagement.service.LeaseService;
import com.razorpay.Order;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
public class PaymentController {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserService userService;

    @Autowired
    private PaymentMapper paymentMapper;
    
    @Autowired
    private RazorpayService razorpayService;
    
    @Autowired
    private LeaseService leaseService;

    // Get all payments (admin)
    @GetMapping("/payments")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<PaymentDTO> getAllPayments(Pageable pageable) {
        return paymentService.findAll(pageable)
                .map(paymentMapper::toDTO);
    }

    // Get payments by property (landlord)
    @GetMapping("/payments/property/{propertyId}")
    @PreAuthorize("hasRole('LANDLORD')")
    public Page<PaymentDTO> getPaymentsByProperty(
            @PathVariable Long propertyId, Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        return paymentService.findByPropertyId(propertyId, currentUser, pageable)
                .map(paymentMapper::toDTO);
    }

    // Get payments for landlord
    @GetMapping("/landlord/payments")
    @PreAuthorize("hasRole('LANDLORD')")
    public Page<PaymentDTO> getLandlordPayments(Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        return paymentService.findByLandlord(currentUser, pageable)
                .map(paymentMapper::toDTO);
    }

    // Get payments for tenant
    @GetMapping("/tenant/payments")
    @PreAuthorize("hasRole('TENANT')")
    public Page<PaymentDTO> getTenantPayments(Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        return paymentService.findByTenant(currentUser, pageable)
                .map(paymentMapper::toDTO);
    }

    // Get payment by ID
    @GetMapping("/payments/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD', 'TENANT')")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        Payment payment = paymentService.findById(id);
        
        if (payment == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Verify access rights
        if (!paymentService.canAccess(payment, currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        return ResponseEntity.ok(paymentMapper.toDTO(payment));
    }

    // Get upcoming payments for tenant
    @GetMapping("/tenant/payments/upcoming")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<List<PaymentDTO>> getUpcomingPaymentsForTenant() {
        User currentUser = userService.getCurrentUser();
        List<Payment> payments = paymentService.findUpcomingPaymentsByTenant(currentUser);
        List<PaymentDTO> paymentDTOs = payments.stream()
                .map(paymentMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(paymentDTOs);
    }
    
    // Create Razorpay order for lease payment
    @PostMapping("/tenant/payments/razorpay/create-order")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<?> createRazorpayOrder(@RequestBody CreateOrderDTO orderRequest) {
        User currentUser = userService.getCurrentUser();
        
        try {
            logger.info("Creating Razorpay order for lease: {}, amount: {}", 
                      orderRequest.getLeaseId(), orderRequest.getAmount());
            
            // Validate lease exists and belongs to tenant
            Lease lease = leaseService.findById(orderRequest.getLeaseId());
            if (lease == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Lease not found"));
            }
            
            if (!lease.getTenant().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You are not authorized to make payment for this lease"));
            }
            
            // Ensure the amount is valid
            BigDecimal amount = orderRequest.getAmount();
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                // If amount is not provided or invalid, use the lease's monthly rent
                amount = lease.getMonthlyRent();
                logger.info("Using lease monthly rent as payment amount: {}", amount);
            }
            
            // Create unique receipt ID
            String receiptId = "LEASE_PMT_" + lease.getId() + "_" + System.currentTimeMillis();
            
            // Add notes for the payment
            Map<String, String> notes = new HashMap<>();
            notes.put("leaseId", lease.getId().toString());
            notes.put("propertyId", lease.getProperty().getId().toString());
            notes.put("propertyTitle", lease.getProperty().getTitle());
            notes.put("tenantId", currentUser.getId().toString());
            notes.put("tenantName", currentUser.getFirstName() + " " + currentUser.getLastName());
            notes.put("description", orderRequest.getDescription());
            notes.put("amount", amount.toString());
            
            // Create order
            Order order = razorpayService.createOrder(amount, receiptId, notes);
            
            // Return order details
            Map<String, Object> response = new HashMap<>();
            response.put("razorpayOrderId", order.get("id").toString());
            response.put("amount", amount);
            response.put("currency", "INR");
            response.put("receiptId", receiptId);
            
            return ResponseEntity.ok(response);
        } catch (RazorpayException e) {
            logger.error("Error creating Razorpay order: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to create payment order: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }
    
    // Verify Razorpay payment
    @PostMapping("/tenant/payments/razorpay/verify")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<?> verifyRazorpayPayment(@RequestBody VerifyPaymentDTO verifyRequest) {
        User currentUser = userService.getCurrentUser();
        
        try {
            logger.info("Verifying Razorpay payment for orderId: {}, paymentId: {}", 
                      verifyRequest.getRazorpayOrderId(), verifyRequest.getRazorpayPaymentId());
            
            // Validate lease exists and belongs to tenant
            Lease lease = leaseService.findById(verifyRequest.getLeaseId());
            if (lease == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Lease not found"));
            }
            
            if (!lease.getTenant().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You are not authorized to make payment for this lease"));
            }
            
            // Verify signature
            boolean isValid = razorpayService.verifyPaymentSignature(
                verifyRequest.getRazorpayOrderId(),
                verifyRequest.getRazorpayPaymentId(),
                verifyRequest.getRazorpaySignature()
            );
            
            if (!isValid) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid payment signature"));
            }
            
            // Record payment in our system
            Payment payment = new Payment();
            payment.setLease(lease);
            payment.setProperty(lease.getProperty());
            payment.setTenant(currentUser);
            payment.setAmount(lease.getMonthlyRent()); // Use lease monthly rent as amount
            payment.setDescription("Rent payment via Razorpay");
            payment.setPaymentMethod("RAZORPAY");
            payment.setTransactionId(verifyRequest.getRazorpayPaymentId());
            payment.setStatus(PaymentStatus.COMPLETED.name());
            payment.setReceiptUrl(razorpayService.generateReceipt(verifyRequest.getRazorpayPaymentId()));
            
            Payment savedPayment = paymentService.recordRazorpayPayment(payment);
            
            return ResponseEntity.ok(paymentMapper.toDTO(savedPayment));
        } catch (Exception e) {
            logger.error("Error verifying payment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to verify payment: " + e.getMessage()));
        }
    }

    // Make a payment (tenant)
    @PostMapping("/tenant/payments")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<PaymentDTO> makePayment(@RequestBody MakePaymentDTO paymentDTO) {
        User currentUser = userService.getCurrentUser();
        
        try {
            logger.info("Processing payment request: amount={}, leaseId={}, method={}", 
                      paymentDTO.getAmount(), paymentDTO.getLeaseId(), 
                      paymentDTO.getRazorpayPaymentId() != null ? "RAZORPAY" : 
                      (paymentDTO.getPaymentMethodId() != null ? "SAVED_METHOD" : "NEW_CARD"));
            
            Payment payment;
            if (paymentDTO.getRazorpayPaymentId() != null) {
                // This is a Razorpay payment
                logger.info("Processing Razorpay payment with ID: {}", paymentDTO.getRazorpayPaymentId());
                
                // Validate lease exists and belongs to tenant
                Lease lease = leaseService.findById(paymentDTO.getLeaseId());
                if (lease == null) {
                    return ResponseEntity.badRequest().body(null);
                }
                
                if (!lease.getTenant().getId().equals(currentUser.getId())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
                
                // Create payment record
                payment = new Payment();
                payment.setLease(lease);
                payment.setProperty(lease.getProperty());
                payment.setTenant(currentUser);
                payment.setAmount(paymentDTO.getAmount());
                payment.setDescription(paymentDTO.getDescription());
                payment.setPaymentMethod("RAZORPAY");
                payment.setTransactionId(paymentDTO.getRazorpayPaymentId());
                payment.setStatus(PaymentStatus.COMPLETED.name());
                payment.setPaymentDate(java.time.LocalDate.now());
                
                // Record the payment
                payment = paymentService.recordRazorpayPayment(payment);
                logger.info("Razorpay payment recorded successfully with ID: {}", payment.getId());
            } else if (paymentDTO.getPaymentMethodId() != null) {
                // Use existing payment method
                payment = paymentService.makePayment(
                        paymentDTO.getAmount(),
                        paymentDTO.getDescription(),
                        paymentDTO.getLeaseId(),
                        paymentDTO.getPaymentMethodId(),
                        currentUser);
            } else if (paymentDTO.getNewCard() != null) {
                // Use new card
                payment = paymentService.makePaymentWithNewCard(
                        paymentDTO.getAmount(),
                        paymentDTO.getDescription(),
                        paymentDTO.getLeaseId(),
                        paymentDTO.getNewCard(),
                        paymentDTO.isSaveCard(),
                        currentUser);
            } else {
                return ResponseEntity.badRequest().body(null);
            }
            
            return ResponseEntity.ok(paymentMapper.toDTO(payment));
        } catch (Exception e) {
            logger.error("Error processing payment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Record a payment (landlord)
    @PostMapping("/landlord/payments/record")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<PaymentDTO> recordPayment(@RequestBody RecordPaymentDTO paymentDTO) {
        User currentUser = userService.getCurrentUser();
        
        Payment payment = paymentService.recordPayment(
                paymentDTO.getAmount(),
                paymentDTO.getDescription(),
                paymentDTO.getLeaseId(),
                paymentDTO.getPaymentMethod(),
                paymentDTO.getPaymentDate(),
                currentUser);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentMapper.toDTO(payment));
    }

    // Get payment receipt
    @GetMapping("/payments/{paymentId}/receipt")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD', 'TENANT')")
    public ResponseEntity<Resource> getPaymentReceipt(@PathVariable Long paymentId) {
        User currentUser = userService.getCurrentUser();
        Payment payment = paymentService.findById(paymentId);
        
        if (payment == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Verify access rights
        if (!paymentService.canAccess(payment, currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Resource receipt = paymentService.generateReceipt(paymentId);
        
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"payment-receipt-" + paymentId + ".pdf\"")
                .body(receipt);
    }

    // Get payment statistics for landlord
    @GetMapping("/landlord/payments/stats")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<Map<String, Object>> getLandlordPaymentStats() {
        User currentUser = userService.getCurrentUser();
        Map<String, Object> stats = paymentService.getPaymentStatsByLandlord(currentUser);
        return ResponseEntity.ok(stats);
    }

    // Get recent payments (for dashboard)
    @GetMapping("/payments/recent")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<List<PaymentDTO>> getRecentPayments(
            @RequestParam(defaultValue = "5") int limit) {
        User currentUser = userService.getCurrentUser();
        List<Payment> payments;
        
        if (userService.hasRole(currentUser, "ADMIN")) {
            payments = paymentService.findRecentPayments(limit);
        } else {
            payments = paymentService.findRecentPaymentsByLandlord(currentUser, limit);
        }

        List<PaymentDTO> paymentDTOs = payments.stream()
                .map(paymentMapper::toDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(paymentDTOs);
    }

    // Generate payment invoice
    @PostMapping("/leases/{leaseId}/invoices")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<PaymentDTO> generatePaymentInvoice(
            @PathVariable Long leaseId, @RequestBody InvoiceDTO invoiceDTO) {
        User currentUser = userService.getCurrentUser();
        
        Payment invoice = paymentService.generateInvoice(
                leaseId,
                invoiceDTO.getAmount(),
                invoiceDTO.getDescription(),
                invoiceDTO.getDueDate(),
                currentUser);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentMapper.toDTO(invoice));
    }

    // Get payment methods for tenant
    @GetMapping("/tenant/payment-methods")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<List<PaymentMethodDTO>> getTenantPaymentMethods() {
        User currentUser = userService.getCurrentUser();
        List<PaymentMethodDTO> paymentMethods = paymentService.findPaymentMethodsByUser(currentUser);
        return ResponseEntity.ok(paymentMethods);
    }

    // Add payment method for tenant
    @PostMapping("/tenant/payment-methods")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<PaymentMethodDTO> addTenantPaymentMethod(
            @RequestBody PaymentMethodDTO paymentMethodDTO) {
        User currentUser = userService.getCurrentUser();
        
        PaymentMethodDTO savedMethod = paymentService.addPaymentMethod(paymentMethodDTO, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMethod);
    }

    // Remove payment method
    @DeleteMapping("/tenant/payment-methods/{paymentMethodId}")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<Void> removeTenantPaymentMethod(@PathVariable Long paymentMethodId) {
        User currentUser = userService.getCurrentUser();
        
        boolean result = paymentService.removePaymentMethod(paymentMethodId, currentUser);
        
        if (result) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Set default payment method
    @PutMapping("/tenant/payment-methods/{paymentMethodId}/default")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<PaymentMethodDTO> setDefaultPaymentMethod(@PathVariable Long paymentMethodId) {
        User currentUser = userService.getCurrentUser();
        
        PaymentMethodDTO updatedMethod = paymentService.setDefaultPaymentMethod(paymentMethodId, currentUser);
        
        if (updatedMethod != null) {
            return ResponseEntity.ok(updatedMethod);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // DTOs for request handling
    static class CreateOrderDTO {
        private BigDecimal amount;
        private Long leaseId;
        private String description;
        
        public BigDecimal getAmount() {
            return amount;
        }
        
        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
        
        public Long getLeaseId() {
            return leaseId;
        }
        
        public void setLeaseId(Long leaseId) {
            this.leaseId = leaseId;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
    }
    
    static class VerifyPaymentDTO {
        private String razorpayOrderId;
        private String razorpayPaymentId;
        private String razorpaySignature;
        private Long leaseId;
        
        public String getRazorpayOrderId() {
            return razorpayOrderId;
        }
        
        public void setRazorpayOrderId(String razorpayOrderId) {
            this.razorpayOrderId = razorpayOrderId;
        }
        
        public String getRazorpayPaymentId() {
            return razorpayPaymentId;
        }
        
        public void setRazorpayPaymentId(String razorpayPaymentId) {
            this.razorpayPaymentId = razorpayPaymentId;
        }
        
        public String getRazorpaySignature() {
            return razorpaySignature;
        }
        
        public void setRazorpaySignature(String razorpaySignature) {
            this.razorpaySignature = razorpaySignature;
        }
        
        public Long getLeaseId() {
            return leaseId;
        }
        
        public void setLeaseId(Long leaseId) {
            this.leaseId = leaseId;
        }
    }
    
    static class MakePaymentDTO {
        private java.math.BigDecimal amount;
        private String description;
        private Long leaseId;
        private Long paymentMethodId;
        private NewCardDTO newCard;
        private boolean saveCard = true;
        private String razorpayPaymentId;
        private String razorpayOrderId;
        private String razorpaySignature;
        
        public java.math.BigDecimal getAmount() {
            return amount;
        }
        
        public void setAmount(java.math.BigDecimal amount) {
            this.amount = amount;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public Long getLeaseId() {
            return leaseId;
        }
        
        public void setLeaseId(Long leaseId) {
            this.leaseId = leaseId;
        }
        
        public Long getPaymentMethodId() {
            return paymentMethodId;
        }
        
        public void setPaymentMethodId(Long paymentMethodId) {
            this.paymentMethodId = paymentMethodId;
        }
        
        public NewCardDTO getNewCard() {
            return newCard;
        }
        
        public void setNewCard(NewCardDTO newCard) {
            this.newCard = newCard;
        }
        
        public boolean isSaveCard() {
            return saveCard;
        }
        
        public void setSaveCard(boolean saveCard) {
            this.saveCard = saveCard;
        }
        
        public String getRazorpayPaymentId() {
            return razorpayPaymentId;
        }
        
        public void setRazorpayPaymentId(String razorpayPaymentId) {
            this.razorpayPaymentId = razorpayPaymentId;
        }
        
        public String getRazorpayOrderId() {
            return razorpayOrderId;
        }
        
        public void setRazorpayOrderId(String razorpayOrderId) {
            this.razorpayOrderId = razorpayOrderId;
        }
        
        public String getRazorpaySignature() {
            return razorpaySignature;
        }
        
        public void setRazorpaySignature(String razorpaySignature) {
            this.razorpaySignature = razorpaySignature;
        }
    }
    
    static class RecordPaymentDTO {
        private java.math.BigDecimal amount;
        private String description;
        private Long leaseId;
        private String paymentMethod;
        private java.time.LocalDate paymentDate;
        
        public java.math.BigDecimal getAmount() {
            return amount;
        }
        
        public void setAmount(java.math.BigDecimal amount) {
            this.amount = amount;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public Long getLeaseId() {
            return leaseId;
        }
        
        public void setLeaseId(Long leaseId) {
            this.leaseId = leaseId;
        }
        
        public String getPaymentMethod() {
            return paymentMethod;
        }
        
        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }
        
        public java.time.LocalDate getPaymentDate() {
            return paymentDate;
        }
        
        public void setPaymentDate(java.time.LocalDate paymentDate) {
            this.paymentDate = paymentDate;
        }
    }
    
    static class InvoiceDTO {
        private java.math.BigDecimal amount;
        private String description;
        private java.time.LocalDate dueDate;
        
        public java.math.BigDecimal getAmount() {
            return amount;
        }
        
        public void setAmount(java.math.BigDecimal amount) {
            this.amount = amount;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public java.time.LocalDate getDueDate() {
            return dueDate;
        }
        
        public void setDueDate(java.time.LocalDate dueDate) {
            this.dueDate = dueDate;
        }
    }
} 