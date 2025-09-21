package com.propertymanagement.repository;

import com.propertymanagement.model.Lease;
import com.propertymanagement.model.LeaseStatus;
import com.propertymanagement.model.Property;
import com.propertymanagement.model.User;
import com.propertymanagement.repository.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeaseRepository extends BaseRepository<Lease> {
    
    Page<Lease> findByProperty(Property property, Pageable pageable);
    
    Page<Lease> findByProperty_Id(Long propertyId, Pageable pageable);
    
    Page<Lease> findByTenant(User tenant, Pageable pageable);
    
    Page<Lease> findByLandlord(User landlord, Pageable pageable);
    
    Page<Lease> findByStatus(LeaseStatus status, Pageable pageable);
    
    Page<Lease> findByPropertyAndStatus(Property property, LeaseStatus status, Pageable pageable);
    
    Page<Lease> findByTenantAndStatus(User tenant, LeaseStatus status, Pageable pageable);
    
    Page<Lease> findByLandlordAndStatus(User landlord, LeaseStatus status, Pageable pageable);
    
    List<Lease> findByEndDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<Lease> findByStatusAndEndDateBefore(LeaseStatus status, LocalDate date);
    
    List<Lease> findByStatusAndStartDateAfter(LeaseStatus status, LocalDate date);
    
    Optional<Lease> findFirstByPropertyAndStatusOrderByStartDateDesc(Property property, LeaseStatus status);
    
    boolean existsByPropertyAndStatus(Property property, LeaseStatus status);
    
    long countByPropertyAndStatus(Property property, LeaseStatus status);
    
    long countByTenantAndStatus(User tenant, LeaseStatus status);
    
    long countByLandlordAndStatus(User landlord, LeaseStatus status);
    
    @Query("SELECT COUNT(DISTINCT l.tenant.id) FROM Lease l WHERE l.landlord.id = :landlordId")
    long countDistinctTenantsByLandlord(@Param("landlordId") Long landlordId);
} 