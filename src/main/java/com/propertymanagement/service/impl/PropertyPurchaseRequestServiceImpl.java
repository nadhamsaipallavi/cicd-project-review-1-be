package com.propertymanagement.service.impl;

import com.propertymanagement.exception.ResourceNotFoundException;
import com.propertymanagement.model.*;
import com.propertymanagement.repository.PropertyPurchaseRequestRepository;
import com.propertymanagement.service.PropertyPurchaseRequestService;
import com.propertymanagement.service.PropertyService;
import com.propertymanagement.service.RazorpayService;
import com.propertymanagement.service.UserService;
import com.propertymanagement.service.base.impl.BaseServiceImpl;
import com.razorpay.Order;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PropertyPurchaseRequestServiceImpl extends BaseServiceImpl<PropertyPurchaseRequest, PropertyPurchaseRequestRepository> implements PropertyPurchaseRequestService {

    private final PropertyService propertyService;
    private final UserService userService;
    private final RazorpayService razorpayService;
    private static final Logger logger = LoggerFactory.getLogger(PropertyPurchaseRequestServiceImpl.class);

    @Autowired
    public PropertyPurchaseRequestServiceImpl(
            PropertyPurchaseRequestRepository repository,
                                            PropertyService propertyService,
            UserService userService,
            RazorpayService razorpayService) {
        super(repository);
        this.propertyService = propertyService;
        this.userService = userService;
        this.razorpayService = razorpayService;
    }

    @Override
    @Transactional
    public PropertyPurchaseRequest createPurchaseRequest(Long propertyId, User tenant) {
        Property property = propertyService.findById(propertyId);
        
        if (!property.isAvailable()) {
            throw new IllegalStateException("Property is not available for purchase");
        }
        
        // Check if property is for sale
        if (property.getListingType() == ListingType.FOR_RENT) {
            throw new IllegalStateException("Property is not available for sale, it is for rent only");
        }
        
        // Ensure the property has a sale price
        if (property.getSalePrice() == null || property.getSalePrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Property does not have a valid sale price");
        }
        
        PropertyPurchaseRequest request = new PropertyPurchaseRequest();
        request.setProperty(property);
        request.setTenant(tenant);
        request.setLandlord(property.getLandlord());
        request.setRequestDate(LocalDateTime.now());
        
        // Automatically approve the request to allow direct purchase
        request.setStatus(PurchaseRequestStatus.APPROVED);
        request.setResponseDate(LocalDateTime.now());
        request.setResponseNotes("Auto-approved for direct purchase");
        
        request.setPurchasePrice(property.getSalePrice());
        
        return repository.save(request);
    }

    @Override
    @Transactional
    public PropertyPurchaseRequest updateRequestStatus(Long requestId, PurchaseRequestStatus status, String responseNotes) {
        PropertyPurchaseRequest request = findById(requestId);
        
        if (request.getStatus() != PurchaseRequestStatus.PENDING) {
            throw new IllegalStateException("Can only update status of pending requests");
        }
        
        request.setStatus(status);
        request.setResponseDate(LocalDateTime.now());
        request.setResponseNotes(responseNotes);
        
        return repository.save(request);
    }

    @Override
    @Transactional
    public PropertyPurchaseRequest initiatePayment(Long requestId) {
        PropertyPurchaseRequest request = findById(requestId);
        
        if (request.getStatus() != PurchaseRequestStatus.APPROVED) {
            throw new IllegalStateException("Can only initiate payment for approved requests");
        }
        
        try {
            // Create a unique receipt ID based on request ID
            String receiptId = "PROP_PUR_" + requestId + "_" + System.currentTimeMillis();
            
            // Ensure we have a valid purchase price
            if (request.getPurchasePrice() == null || request.getPurchasePrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                throw new IllegalStateException("Invalid purchase price for property");
            }
            
            // Log the purchase price for debugging
            logger.info("Initiating payment for property purchase with price: {}", request.getPurchasePrice());
            
            // Prepare notes for the order
            Map<String, String> notes = new HashMap<>();
            notes.put("propertyId", request.getProperty().getId().toString());
            notes.put("propertyTitle", request.getProperty().getTitle());
            notes.put("tenantId", request.getTenant().getId().toString());
            notes.put("tenantName", request.getTenant().getFirstName() + " " + request.getTenant().getLastName());
            notes.put("purchasePrice", request.getPurchasePrice().toString());
            
            // Create Razorpay order
            Order order = razorpayService.createOrder(request.getPurchasePrice(), receiptId, notes);
            JSONObject orderJson = order.toJson();
            
            // Update the request with Razorpay order ID
            request.setRazorpayOrderId(orderJson.getString("id"));
            request.setStatus(PurchaseRequestStatus.PAYMENT_PENDING);
            request.setPaymentStatus(PaymentStatus.PENDING);
            
            logger.info("Payment initiated for purchase request {}: Razorpay Order ID: {}, Amount: {}", 
                      requestId, request.getRazorpayOrderId(), request.getPurchasePrice());
        
            return repository.save(request);
        } catch (RazorpayException e) {
            logger.error("Error creating Razorpay order: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initiate payment: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public PropertyPurchaseRequest processPayment(Long requestId, String razorpayPaymentId, String razorpaySignature) {
        PropertyPurchaseRequest request = findById(requestId);
        
        if (request.getStatus() != PurchaseRequestStatus.PAYMENT_PENDING) {
            throw new IllegalStateException("Can only process payment for payment pending requests");
        }
        
        try {
            // Verify the payment signature
            boolean isValid = razorpayService.verifyPaymentSignature(
                request.getRazorpayOrderId(), razorpayPaymentId, razorpaySignature);
            
            if (!isValid) {
                request.setStatus(PurchaseRequestStatus.PAYMENT_FAILED);
                request.setPaymentStatus(PaymentStatus.FAILED);
                repository.save(request);
                throw new IllegalStateException("Payment verification failed");
            }
            
            // Fetch payment details
            JSONObject paymentDetails = razorpayService.fetchPaymentById(razorpayPaymentId);
            
            // Generate receipt/invoice URL
            String invoiceUrl = razorpayService.generateReceipt(razorpayPaymentId);
            
            // Update the request with payment details
        request.setStatus(PurchaseRequestStatus.PAYMENT_COMPLETED);
        request.setPaymentStatus(PaymentStatus.COMPLETED);
        request.setPaymentDate(LocalDateTime.now());
        request.setRazorpayPaymentId(razorpayPaymentId);
        request.setRazorpaySignature(razorpaySignature);
            request.setInvoiceUrl(invoiceUrl);
        
        // Update property status
        Property property = request.getProperty();
        property.setAvailable(false);
        propertyService.updateProperty(property.getId(), property);
        
            logger.info("Payment processed successfully for purchase request {}: Payment ID: {}", 
                      requestId, razorpayPaymentId);
            
        return repository.save(request);
        } catch (RazorpayException e) {
            logger.error("Error processing payment: {}", e.getMessage(), e);
            
            // Update request status to failed
            request.setStatus(PurchaseRequestStatus.PAYMENT_FAILED);
            request.setPaymentStatus(PaymentStatus.FAILED);
            repository.save(request);
            
            throw new RuntimeException("Failed to process payment: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public PropertyPurchaseRequest cancelRequest(Long requestId) {
        PropertyPurchaseRequest request = findById(requestId);
        
        if (request.getStatus() == PurchaseRequestStatus.PAYMENT_COMPLETED) {
            throw new IllegalStateException("Cannot cancel a completed payment");
        }
        
        request.setStatus(PurchaseRequestStatus.CANCELLED);
        
        return repository.save(request);
    }

    @Override
    public List<PropertyPurchaseRequest> getTenantRequests(User tenant) {
        return repository.findByTenant(tenant);
    }

    @Override
    public List<PropertyPurchaseRequest> getLandlordRequests(User landlord) {
        return repository.findByLandlord(landlord);
    }

    @Override
    public Page<PropertyPurchaseRequest> getTenantRequests(User tenant, Pageable pageable) {
        return repository.findByTenant(tenant, pageable);
    }

    @Override
    public Page<PropertyPurchaseRequest> getLandlordRequests(User landlord, Pageable pageable) {
        return repository.findByLandlord(landlord, pageable);
    }

    @Override
    public boolean isRequestPending(Long requestId) {
        PropertyPurchaseRequest request = findById(requestId);
        return request.getStatus() == PurchaseRequestStatus.PENDING;
    }

    @Override
    public boolean isRequestApproved(Long requestId) {
        PropertyPurchaseRequest request = findById(requestId);
        return request.getStatus() == PurchaseRequestStatus.APPROVED;
    }

    @Override
    public boolean isRequestRejected(Long requestId) {
        PropertyPurchaseRequest request = findById(requestId);
        return request.getStatus() == PurchaseRequestStatus.REJECTED;
    }

    @Override
    public boolean isRequestCancelled(Long requestId) {
        PropertyPurchaseRequest request = findById(requestId);
        return request.getStatus() == PurchaseRequestStatus.CANCELLED;
    }

    @Override
    public boolean isPaymentCompleted(Long requestId) {
        PropertyPurchaseRequest request = findById(requestId);
        return request.getStatus() == PurchaseRequestStatus.PAYMENT_COMPLETED;
    }
} 