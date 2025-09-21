package com.propertymanagement.service.impl;

import com.propertymanagement.dto.NewCardDTO;
import com.propertymanagement.dto.PaymentMethodDTO;
import com.propertymanagement.model.Payment;
import com.propertymanagement.model.User;
import com.propertymanagement.model.Lease;
import com.propertymanagement.model.Property;
import com.propertymanagement.model.PaymentStatus;
import com.propertymanagement.repository.LeaseRepository;
import com.propertymanagement.repository.PaymentRepository;
import com.propertymanagement.repository.PropertyRepository;
import com.propertymanagement.service.PaymentService;
import com.propertymanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository repository;

    @Autowired
    private LeaseRepository leaseRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserService userService;

    @Override
    public Page<Payment> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Payment findById(Long id) {
        Optional<Payment> payment = repository.findById(id);
        return payment.orElse(null);
    }

    @Override
    public Page<Payment> findByPropertyId(Long propertyId, User currentUser, Pageable pageable) {
        // Verify property exists
        Optional<Property> propertyOpt = propertyRepository.findById(propertyId);
        if (propertyOpt.isEmpty()) {
            return new PageImpl<>(new ArrayList<>());
        }
        
        Property property = propertyOpt.get();
        
        // Check if user is authorized - should be the landlord of the property or an admin
        if (!property.getLandlord().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().name().equals("ADMIN")) {
            return new PageImpl<>(new ArrayList<>());
        }
        
        // Get all payments related to this property
        return repository.findByPropertyId(propertyId, pageable);
    }

    @Override
    public Page<Payment> findByLandlord(User landlord, Pageable pageable) {
        // Find all properties owned by this landlord
        List<Property> properties = propertyRepository.findByLandlord(landlord);
        
        if (properties.isEmpty()) {
            return new PageImpl<>(new ArrayList<>());
        }
        
        // Get all property IDs
        List<Long> propertyIds = properties.stream()
                .map(Property::getId)
                .collect(Collectors.toList());
        
        // Get all payments for these properties
        return repository.findByPropertyIdIn(propertyIds, pageable);
    }

    @Override
    public Page<Payment> findByTenant(User tenant, Pageable pageable) {
        return repository.findByTenant(tenant, pageable);
    }

    @Override
    public List<Payment> findUpcomingPaymentsByTenant(User tenant) {
        return repository.findUpcomingPaymentsByTenant(tenant);
    }

    @Override
    public List<Payment> findRecentPayments(int limit) {
        return repository.findRecentPayments(org.springframework.data.domain.PageRequest.of(0, limit));
    }

    @Override
    public List<Payment> findRecentPaymentsByLandlord(User landlord, int limit) {
        // Find all properties owned by this landlord
        List<Property> properties = propertyRepository.findByLandlord(landlord);
        
        if (properties.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Get all property IDs
        List<Long> propertyIds = properties.stream()
                .map(Property::getId)
                .collect(Collectors.toList());
        
        // Get recent payments for these properties
        return repository.findRecentPaymentsByPropertyIds(propertyIds, 
                org.springframework.data.domain.PageRequest.of(0, limit));
    }

    @Override
    public boolean canAccess(Payment payment, User user) {
        // Simple access control logic - admin can access all payments
        if (user.getRole().name().equals("ADMIN")) {
            return true;
        }
        
        // Tenants can only access their own payments
        if (user.getRole().name().equals("TENANT")) {
            return payment.getTenant() != null && payment.getTenant().getId().equals(user.getId());
        }
        
        // Landlords can access payments for their properties
        if (user.getRole().name().equals("LANDLORD")) {
            if (payment.getProperty() != null && payment.getProperty().getLandlord() != null) {
                return payment.getProperty().getLandlord().getId().equals(user.getId());
            }
        }
        
        return false;
    }

    @Override
    public Payment makePayment(BigDecimal amount, String description, Long leaseId, 
                               Long paymentMethodId, User tenant) {
        // Find the lease
        Optional<Lease> leaseOpt = leaseRepository.findById(leaseId);
        if (leaseOpt.isEmpty()) {
            throw new IllegalArgumentException("Lease not found with ID: " + leaseId);
        }
        
        Lease lease = leaseOpt.get();
        
        // Validate tenant is authorized to make payment for this lease
        if (!lease.getTenant().getId().equals(tenant.getId())) {
            throw new IllegalArgumentException("You are not authorized to make payment for this lease");
        }
        
        // Create payment record
        Payment payment = new Payment();
        payment.setAmount(amount);
        payment.setDescription(description);
        payment.setLease(lease);
        payment.setTenant(tenant);
        payment.setProperty(lease.getProperty());
        payment.setPaymentMethod("ONLINE");
        payment.setStatus(PaymentStatus.COMPLETED.name());
        payment.setPaymentDate(LocalDate.now());
        
        // Save payment record
        return repository.save(payment);
    }

    @Override
    public Payment makePaymentWithNewCard(BigDecimal amount, String description, Long leaseId, 
                                         NewCardDTO newCard, boolean saveCard, User tenant) {
        // Find the lease
        Optional<Lease> leaseOpt = leaseRepository.findById(leaseId);
        if (leaseOpt.isEmpty()) {
            throw new IllegalArgumentException("Lease not found with ID: " + leaseId);
        }
        
        Lease lease = leaseOpt.get();
        
        // Validate tenant is authorized to make payment for this lease
        if (!lease.getTenant().getId().equals(tenant.getId())) {
            throw new IllegalArgumentException("You are not authorized to make payment for this lease");
        }
        
        // Create payment record
        Payment payment = new Payment();
        payment.setAmount(amount);
        payment.setDescription(description);
        payment.setLease(lease);
        payment.setTenant(tenant);
        payment.setProperty(lease.getProperty());
        payment.setPaymentMethod("CARD");
        payment.setStatus(PaymentStatus.COMPLETED.name());
        payment.setPaymentDate(LocalDate.now());
        
        // Set masked card number for receipt
        String maskedCardNumber = "xxxx-xxxx-xxxx-" + newCard.getCardNumber().substring(Math.max(0, newCard.getCardNumber().length() - 4));
        payment.setTransactionId(maskedCardNumber); // Store masked card for reference
        
        // Save payment method if required - in a real app, you'd save this to a secure payment provider
        // Not implementing here for security reasons
        
        // Save payment record
        return repository.save(payment);
    }

    @Override
    public Payment recordPayment(BigDecimal amount, String description, Long leaseId, 
                                String paymentMethod, LocalDate paymentDate, User user) {
        // Find the lease
        Optional<Lease> leaseOpt = leaseRepository.findById(leaseId);
        if (leaseOpt.isEmpty()) {
            throw new IllegalArgumentException("Lease not found with ID: " + leaseId);
        }
        
        Lease lease = leaseOpt.get();
        
        // Validate landlord is authorized to record payment for this lease
        if (!lease.getProperty().getLandlord().getId().equals(user.getId()) && 
            !user.getRole().name().equals("ADMIN")) {
            throw new IllegalArgumentException("You are not authorized to record payment for this lease");
        }
        
        // Create payment record
        Payment payment = new Payment();
        payment.setAmount(amount);
        payment.setDescription(description);
        payment.setLease(lease);
        payment.setTenant(lease.getTenant());
        payment.setProperty(lease.getProperty());
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus(PaymentStatus.COMPLETED.name());
        payment.setPaymentDate(paymentDate != null ? paymentDate : LocalDate.now());
        
        // Save payment record
        return repository.save(payment);
    }

    @Override
    public Payment generateInvoice(Long leaseId, BigDecimal amount, String description, 
                                 LocalDate dueDate, User user) {
        // Find the lease
        Optional<Lease> leaseOpt = leaseRepository.findById(leaseId);
        if (leaseOpt.isEmpty()) {
            throw new IllegalArgumentException("Lease not found with ID: " + leaseId);
        }
        
        Lease lease = leaseOpt.get();
        
        // Validate landlord is authorized to generate invoice for this lease
        if (!lease.getProperty().getLandlord().getId().equals(user.getId()) && 
            !user.getRole().name().equals("ADMIN")) {
            throw new IllegalArgumentException("You are not authorized to generate invoice for this lease");
        }
        
        // Create invoice record
        Payment invoice = new Payment();
        invoice.setAmount(amount);
        invoice.setDescription(description);
        invoice.setLease(lease);
        invoice.setTenant(lease.getTenant());
        invoice.setProperty(lease.getProperty());
        invoice.setStatus(PaymentStatus.PENDING.name());
        
        // Set due date if provided, otherwise use lease payment due date
        if (dueDate != null) {
            invoice.setPaymentDate(dueDate);
        } else if (lease.getPaymentDueDay() != null) {
            // Calculate next payment due date based on lease payment due day
            LocalDate now = LocalDate.now();
            LocalDate dueDateTime = LocalDate.of(now.getYear(), now.getMonth(), lease.getPaymentDueDay());
            // If due date has passed, set to next month
            if (dueDateTime.isBefore(now)) {
                dueDateTime = dueDateTime.plusMonths(1);
            }
            invoice.setPaymentDate(dueDateTime);
        } else {
            // Default to 1st of next month
            LocalDate now = LocalDate.now();
            invoice.setPaymentDate(LocalDate.of(now.getYear(), now.getMonth().plus(1), 1));
        }
        
        // Save invoice record
        return repository.save(invoice);
    }

    @Override
    public Payment recordRazorpayPayment(Payment payment) {
        // Validate payment has required fields
        if (payment.getLease() == null || payment.getTenant() == null || payment.getProperty() == null) {
            throw new IllegalArgumentException("Payment must have lease, tenant, and property information");
        }
        
        // Set payment date if not set
        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDate.now());
        }
        
        // Validate tenant is authorized to make payment for this lease
        if (!payment.getLease().getTenant().getId().equals(payment.getTenant().getId())) {
            throw new IllegalArgumentException("Tenant is not authorized to make payment for this lease");
        }
        
        // Ensure status is set
        if (payment.getStatus() == null) {
            payment.setStatus(PaymentStatus.COMPLETED.name());
        }
        
        // Log payment details
        System.out.println("Recording Razorpay payment: " + payment.getTransactionId() + 
                           " for lease: " + payment.getLease().getId() + 
                           " amount: " + payment.getAmount());
        
        // Save and return payment
        return repository.save(payment);
    }

    @Override
    public Resource generateReceipt(Long paymentId) {
        // Implement actual PDF generation logic here
        // For now, return an empty resource
        return new ByteArrayResource(new byte[0]);
    }

    @Override
    public Map<String, Object> getPaymentStatsByLandlord(User landlord) {
        Map<String, Object> stats = new HashMap<>();
        
        // Find all properties owned by this landlord
        List<Property> properties = propertyRepository.findByLandlord(landlord);
        
        if (properties.isEmpty()) {
            stats.put("totalPayments", 0);
            stats.put("pendingPayments", 0);
            stats.put("completedPayments", 0);
            stats.put("totalAmountCollected", 0);
            return stats;
        }
        
        // Get all property IDs
        List<Long> propertyIds = properties.stream()
                .map(Property::getId)
                .collect(Collectors.toList());
        
        // Calculate payment statistics
        long totalPayments = repository.countByPropertyIdIn(propertyIds);
        long pendingPayments = repository.countByPropertyIdInAndStatus(propertyIds, PaymentStatus.PENDING.name());
        long completedPayments = repository.countByPropertyIdInAndStatus(propertyIds, PaymentStatus.COMPLETED.name());
        BigDecimal totalAmountCollected = repository.sumAmountByPropertyIdInAndStatus(propertyIds, PaymentStatus.COMPLETED.name());
        
        stats.put("totalPayments", totalPayments);
        stats.put("pendingPayments", pendingPayments);
        stats.put("completedPayments", completedPayments);
        stats.put("totalAmountCollected", totalAmountCollected != null ? totalAmountCollected : BigDecimal.ZERO);
        
        return stats;
    }

    @Override
    public long countPendingPaymentsByLandlord(User landlord) {
        // Find all properties owned by this landlord
        List<Property> properties = propertyRepository.findByLandlord(landlord);
        
        if (properties.isEmpty()) {
            return 0;
        }
        
        // Get all property IDs
        List<Long> propertyIds = properties.stream()
                .map(Property::getId)
                .collect(Collectors.toList());
        
        // Count pending payments
        return repository.countByPropertyIdInAndStatus(propertyIds, PaymentStatus.PENDING.name());
    }

    @Override
    public List<PaymentMethodDTO> findPaymentMethodsByUser(User user) {
        // In a real app, this would connect to a payment processor to get saved payment methods
        // For now, return an empty list
        return new ArrayList<>();
    }

    @Override
    public PaymentMethodDTO addPaymentMethod(PaymentMethodDTO paymentMethodDTO, User user) {
        // In a real app, this would save a payment method with a payment processor
        // For now, return null
        return null;
    }

    @Override
    public boolean removePaymentMethod(Long paymentMethodId, User user) {
        // In a real app, this would remove a payment method from a payment processor
        // For now, return false
        return false;
    }

    @Override
    public PaymentMethodDTO setDefaultPaymentMethod(Long paymentMethodId, User user) {
        // In a real app, this would set a default payment method with a payment processor
        // For now, return null
        return null;
    }
} 