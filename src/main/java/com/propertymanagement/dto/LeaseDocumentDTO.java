package com.propertymanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaseDocumentDTO {
    private Long id;
    private Long leaseId;
    private String fileName;
    private String documentType;
    private String url;
    private LocalDateTime uploadedAt;
    private String uploadedBy;
    
    // Explicit getters and setters to ensure availability
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getLeaseId() {
        return leaseId;
    }
    
    public void setLeaseId(Long leaseId) {
        this.leaseId = leaseId;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getDocumentType() {
        return documentType;
    }
    
    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
    
    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
    
    public String getUploadedBy() {
        return uploadedBy;
    }
    
    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }
} 