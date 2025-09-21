package com.propertymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long id;
    private BigDecimal amount;
    private String description;
    private Long leaseId;
    private LeaseDTO lease;
    private UserDTO tenant;
    private Long propertyId;
    private PropertyDTO property;
    private String transactionId;
    private LocalDateTime paymentDate;
    private String paymentMethod;
    private String status;
    private String receiptUrl;
    private LocalDateTime createdAt;
    
    // Explicit getters and setters to ensure availability
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Long getLeaseId() {
        return leaseId;
    }
    
    public void setLeaseId(Long leaseId) {
        this.leaseId = leaseId;
    }
    
    public LeaseDTO getLease() {
        return lease;
    }
    
    public void setLease(LeaseDTO lease) {
        this.lease = lease;
    }
    
    public UserDTO getTenant() {
        return tenant;
    }
    
    public void setTenant(UserDTO tenant) {
        this.tenant = tenant;
    }
    
    public Long getPropertyId() {
        return propertyId;
    }
    
    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }
    
    public PropertyDTO getProperty() {
        return property;
    }
    
    public void setProperty(PropertyDTO property) {
        this.property = property;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }
    
    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getReceiptUrl() {
        return receiptUrl;
    }
    
    public void setReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 