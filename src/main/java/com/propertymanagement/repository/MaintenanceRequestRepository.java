package com.propertymanagement.repository;

import com.propertymanagement.model.MaintenanceRequest;
import com.propertymanagement.model.MaintenanceRequestPriority;
import com.propertymanagement.model.MaintenanceRequestStatus;
import com.propertymanagement.model.Property;
import com.propertymanagement.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, Long> {
    
    List<MaintenanceRequest> findByProperty(Property property);
    
    List<MaintenanceRequest> findByUser(User user);
    
    Page<MaintenanceRequest> findByUser(User user, Pageable pageable);
    
    List<MaintenanceRequest> findByStatus(MaintenanceRequestStatus status);
    
    List<MaintenanceRequest> findByPriority(MaintenanceRequestPriority priority);
    
    List<MaintenanceRequest> findByPropertyAndStatus(Property property, MaintenanceRequestStatus status);
    
    List<MaintenanceRequest> findByUserAndStatus(User user, MaintenanceRequestStatus status);
    
    List<MaintenanceRequest> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT mr FROM MaintenanceRequest mr WHERE mr.property IN " +
           "(SELECT p FROM Property p WHERE p.landlord = :landlord)")
    List<MaintenanceRequest> findByLandlord(@Param("landlord") User landlord);
    
    @Query("SELECT mr FROM MaintenanceRequest mr WHERE mr.property IN " +
           "(SELECT p FROM Property p WHERE p.landlord = :landlord) AND mr.status = :status")
    List<MaintenanceRequest> findByLandlordAndStatus(
            @Param("landlord") User landlord, 
            @Param("status") MaintenanceRequestStatus status);
    
    @Query("SELECT COUNT(mr) FROM MaintenanceRequest mr WHERE mr.property IN " +
           "(SELECT p FROM Property p WHERE p.landlord = :landlord) AND mr.status = :status")
    Long countByLandlordAndStatus(
            @Param("landlord") User landlord, 
            @Param("status") MaintenanceRequestStatus status);
    
    @Query("SELECT COUNT(mr) FROM MaintenanceRequest mr WHERE mr.user = :tenant AND mr.status = :status")
    Long countByTenantAndStatus(
            @Param("tenant") User tenant, 
            @Param("status") MaintenanceRequestStatus status);

    Page<MaintenanceRequest> findByProperty(Property property, Pageable pageable);
    
    Page<MaintenanceRequest> findByPropertyIn(List<Property> properties, Pageable pageable);
    
    @Query("SELECT m FROM MaintenanceRequest m WHERE m.status <> 'COMPLETED' AND m.status <> 'CANCELLED' ORDER BY m.createdAt DESC")
    List<MaintenanceRequest> findRecentActiveRequests(Pageable pageable);
    
    @Query("SELECT m FROM MaintenanceRequest m WHERE m.property IN :properties AND m.status <> 'COMPLETED' AND m.status <> 'CANCELLED' ORDER BY m.createdAt DESC")
    List<MaintenanceRequest> findRecentActiveRequestsByProperties(@Param("properties") List<Property> properties, Pageable pageable);
    
    @Query("SELECT COUNT(m) FROM MaintenanceRequest m WHERE m.property IN :properties AND m.status <> 'COMPLETED' AND m.status <> 'CANCELLED'")
    int countPendingRequestsByProperties(@Param("properties") List<Property> properties);
    
    @Query("SELECT COUNT(m) FROM MaintenanceRequest m WHERE m.user = :tenant AND m.status <> 'COMPLETED' AND m.status <> 'CANCELLED'")
    int countPendingRequestsByTenant(@Param("tenant") User tenant);
} 