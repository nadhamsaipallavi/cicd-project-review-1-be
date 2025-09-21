package com.propertymanagement.service;

import com.propertymanagement.model.Lease;
import com.propertymanagement.model.LeaseStatus;
import com.propertymanagement.model.Property;
import com.propertymanagement.model.User;
import com.propertymanagement.service.base.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface LeaseService extends BaseService<Lease> {
    
    Lease createLease(Lease lease, Property property, User tenant, User landlord);
    
    Lease updateLease(Long id, Lease lease);
    
    void deleteLease(Long id);
    
    Page<Lease> findByProperty(Property property, Pageable pageable);
    
    Page<Lease> findByPropertyId(Long propertyId, Pageable pageable);
    
    Page<Lease> findByTenant(User tenant, Pageable pageable);
    
    Page<Lease> findByLandlord(User landlord, Pageable pageable);
    
    Page<Lease> findByStatus(LeaseStatus status, Pageable pageable);
    
    Page<Lease> findByPropertyAndStatus(Property property, LeaseStatus status, Pageable pageable);
    
    Page<Lease> findByTenantAndStatus(User tenant, LeaseStatus status, Pageable pageable);
    
    Page<Lease> findByLandlordAndStatus(User landlord, LeaseStatus status, Pageable pageable);
    
    List<Lease> findExpiringLeases(LocalDate startDate, LocalDate endDate);
    
    List<Lease> findExpiredLeases(LocalDate date);
    
    List<Lease> findUpcomingLeases(LocalDate date);
    
    Lease getCurrentLease(Property property);
    
    boolean hasActiveLease(Property property);
    
    long countActiveLeasesByProperty(Property property);
    
    long countActiveLeasesByTenant(User tenant);
    
    long countActiveLeasesByLandlord(User landlord);
    
    Lease updateLeaseStatus(Long id, LeaseStatus status);
    
    Lease renewLease(Long id, LocalDate newEndDate);
    
    Lease terminateLease(Long id, LocalDate terminationDate);
    
    boolean canTerminateLease(Long id);
    
    boolean canRenewLease(Long id);
} 