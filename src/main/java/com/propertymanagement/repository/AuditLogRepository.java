package com.propertymanagement.repository;

import com.propertymanagement.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    List<AuditLog> findByEntityTypeAndEntityId(String entityType, Long entityId);
    
    Page<AuditLog> findByEntityTypeAndEntityId(String entityType, Long entityId, Pageable pageable);
    
    List<AuditLog> findByUserId(Long userId);
    
    Page<AuditLog> findByUserId(Long userId, Pageable pageable);
    
    List<AuditLog> findByAction(String action);
    
    List<AuditLog> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<AuditLog> findBySuccess(boolean success);
    
    List<AuditLog> findByLogLevel(String logLevel);
    
    Page<AuditLog> findByLogLevel(String logLevel, Pageable pageable);
    
    List<AuditLog> findByUserIdAndAction(Long userId, String action);
    
    List<AuditLog> findByEntityTypeAndAction(String entityType, String action);
    
    List<AuditLog> findByIpAddress(String ipAddress);
} 