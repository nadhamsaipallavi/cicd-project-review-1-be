package com.propertymanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "audit_logs")
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String entityType;
    
    @Column(nullable = false)
    private Long entityId;
    
    @Column(nullable = false)
    private String action;
    
    @Column(length = 5000)
    private String details;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    private Long userId;
    
    private String userEmail;
    
    @Column(nullable = false)
    private String ipAddress;
    
    private String userAgent;
    
    @Column(nullable = false)
    private boolean success;
    
    private String errorMessage;
    
    @Column(nullable = false)
    private String logLevel;
    
    private String additionalData;
} 