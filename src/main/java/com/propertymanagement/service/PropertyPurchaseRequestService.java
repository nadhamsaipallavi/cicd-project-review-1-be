package com.propertymanagement.service;

import com.propertymanagement.model.PropertyPurchaseRequest;
import com.propertymanagement.model.PurchaseRequestStatus;
import com.propertymanagement.model.User;
import com.propertymanagement.service.base.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PropertyPurchaseRequestService extends BaseService<PropertyPurchaseRequest> {
    
    PropertyPurchaseRequest createPurchaseRequest(Long propertyId, User tenant);
    
    PropertyPurchaseRequest updateRequestStatus(Long requestId, PurchaseRequestStatus status, String responseNotes);
    
    PropertyPurchaseRequest initiatePayment(Long requestId);
    
    PropertyPurchaseRequest processPayment(Long requestId, String razorpayPaymentId, String razorpaySignature);
    
    PropertyPurchaseRequest cancelRequest(Long requestId);
    
    List<PropertyPurchaseRequest> getTenantRequests(User tenant);
    
    List<PropertyPurchaseRequest> getLandlordRequests(User landlord);
    
    Page<PropertyPurchaseRequest> getTenantRequests(User tenant, Pageable pageable);
    
    Page<PropertyPurchaseRequest> getLandlordRequests(User landlord, Pageable pageable);
    
    boolean isRequestPending(Long requestId);
    
    boolean isRequestApproved(Long requestId);
    
    boolean isRequestRejected(Long requestId);
    
    boolean isRequestCancelled(Long requestId);
    
    boolean isPaymentCompleted(Long requestId);
} 