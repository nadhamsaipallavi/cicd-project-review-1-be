package com.propertymanagement.repository;

import com.propertymanagement.model.Lease;
import com.propertymanagement.model.Payment;
import com.propertymanagement.model.Property;
import com.propertymanagement.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    // Basic finder methods
    List<Payment> findByLease(Lease lease);
    Page<Payment> findByLease(Lease lease, Pageable pageable);
    
    List<Payment> findByTenant(User tenant);
    Page<Payment> findByTenant(User tenant, Pageable pageable);
    
    List<Payment> findByProperty(Property property);
    Page<Payment> findByProperty(Property property, Pageable pageable);
    
    Optional<Payment> findByTransactionId(String transactionId);
    
    List<Payment> findByStatus(String status);
    
    // Simple query methods
    @Query("SELECT p FROM Payment p ORDER BY p.createdAt DESC")
    List<Payment> findRecentPayments(Pageable pageable);
    
    @Query("SELECT p FROM Payment p WHERE p.tenant = :tenant AND p.status = 'PENDING' ORDER BY p.paymentDate")
    List<Payment> findUpcomingPaymentsByTenant(@Param("tenant") User tenant);
    
    // Methods for property ID
    Page<Payment> findByPropertyId(Long propertyId, Pageable pageable);
    
    // Methods for multiple property IDs
    @Query("SELECT p FROM Payment p WHERE p.property.id IN :propertyIds")
    Page<Payment> findByPropertyIdIn(@Param("propertyIds") List<Long> propertyIds, Pageable pageable);
    
    @Query("SELECT p FROM Payment p WHERE p.property.id IN :propertyIds ORDER BY p.createdAt DESC")
    List<Payment> findRecentPaymentsByPropertyIds(@Param("propertyIds") List<Long> propertyIds, Pageable pageable);
    
    // Counting methods
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.property.id IN :propertyIds")
    long countByPropertyIdIn(@Param("propertyIds") List<Long> propertyIds);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.property.id IN :propertyIds AND p.status = :status")
    long countByPropertyIdInAndStatus(@Param("propertyIds") List<Long> propertyIds, @Param("status") String status);
    
    // Sum methods
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.property.id IN :propertyIds AND p.status = :status")
    BigDecimal sumAmountByPropertyIdInAndStatus(@Param("propertyIds") List<Long> propertyIds, @Param("status") String status);
} 