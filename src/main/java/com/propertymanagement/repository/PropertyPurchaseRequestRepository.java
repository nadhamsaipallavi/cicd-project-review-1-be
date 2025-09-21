package com.propertymanagement.repository;

import com.propertymanagement.model.PropertyPurchaseRequest;
import com.propertymanagement.model.PurchaseRequestStatus;
import com.propertymanagement.model.User;
import com.propertymanagement.repository.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyPurchaseRequestRepository extends BaseRepository<PropertyPurchaseRequest> {
    
    List<PropertyPurchaseRequest> findByTenant(User tenant);
    
    List<PropertyPurchaseRequest> findByLandlord(User landlord);
    
    Page<PropertyPurchaseRequest> findByTenant(User tenant, Pageable pageable);
    
    Page<PropertyPurchaseRequest> findByLandlord(User landlord, Pageable pageable);
    
    List<PropertyPurchaseRequest> findByStatus(PurchaseRequestStatus status);
    
    Page<PropertyPurchaseRequest> findByStatus(PurchaseRequestStatus status, Pageable pageable);
    
    List<PropertyPurchaseRequest> findByTenantAndStatus(User tenant, PurchaseRequestStatus status);
    
    List<PropertyPurchaseRequest> findByLandlordAndStatus(User landlord, PurchaseRequestStatus status);
} 