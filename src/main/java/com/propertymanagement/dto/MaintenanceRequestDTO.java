package com.propertymanagement.dto;

import java.time.LocalDateTime;
import java.util.List;

public class MaintenanceRequestDTO {
    
    private Long id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private Long propertyId;
    private String propertyName;
    private Long tenantId;
    private String tenantName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private String resolutionNotes;
    private List<String> imageUrls;
    private PropertyDTO property;
    private UserDTO tenant;
    
    // Default constructor
    public MaintenanceRequestDTO() {}
    
    // Private constructor for builder
    private MaintenanceRequestDTO(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.description = builder.description;
        this.status = builder.status;
        this.priority = builder.priority;
        this.propertyId = builder.propertyId;
        this.propertyName = builder.propertyName;
        this.tenantId = builder.tenantId;
        this.tenantName = builder.tenantName;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.resolvedAt = builder.resolvedAt;
        this.resolutionNotes = builder.resolutionNotes;
        this.imageUrls = builder.imageUrls;
        this.property = builder.property;
        this.tenant = builder.tenant;
    }
    
    // Static builder method
    public static Builder builder() {
        return new Builder();
    }
    
    // Builder class
    public static class Builder {
        private Long id;
        private String title;
        private String description;
        private String status;
        private String priority;
        private Long propertyId;
        private String propertyName;
        private Long tenantId;
        private String tenantName;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime resolvedAt;
        private String resolutionNotes;
        private List<String> imageUrls;
        private PropertyDTO property;
        private UserDTO tenant;
        
        public Builder id(Long id) {
            this.id = id;
            return this;
        }
        
        public Builder title(String title) {
            this.title = title;
            return this;
        }
        
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        
        public Builder status(String status) {
            this.status = status;
            return this;
        }
        
        public Builder priority(String priority) {
            this.priority = priority;
            return this;
        }
        
        public Builder propertyId(Long propertyId) {
            this.propertyId = propertyId;
            return this;
        }
        
        public Builder propertyName(String propertyName) {
            this.propertyName = propertyName;
            return this;
        }
        
        public Builder tenantId(Long tenantId) {
            this.tenantId = tenantId;
            return this;
        }
        
        public Builder tenantName(String tenantName) {
            this.tenantName = tenantName;
            return this;
        }
        
        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }
        
        public Builder resolvedAt(LocalDateTime resolvedAt) {
            this.resolvedAt = resolvedAt;
            return this;
        }
        
        public Builder resolutionNotes(String resolutionNotes) {
            this.resolutionNotes = resolutionNotes;
            return this;
        }
        
        public Builder imageUrls(List<String> imageUrls) {
            this.imageUrls = imageUrls;
            return this;
        }
        
        public Builder property(PropertyDTO property) {
            this.property = property;
            return this;
        }
        
        public Builder tenant(UserDTO tenant) {
            this.tenant = tenant;
            return this;
        }
        
        public MaintenanceRequestDTO build() {
            return new MaintenanceRequestDTO(this);
        }
    }
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }
    
    public Long getPropertyId() {
        return propertyId;
    }
    
    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }
    
    public String getPropertyName() {
        return propertyName;
    }
    
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
    
    public Long getTenantId() {
        return tenantId;
    }
    
    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
    
    public String getTenantName() {
        return tenantName;
    }
    
    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }
    
    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
    
    public String getResolutionNotes() {
        return resolutionNotes;
    }
    
    public void setResolutionNotes(String resolutionNotes) {
        this.resolutionNotes = resolutionNotes;
    }
    
    public List<String> getImageUrls() {
        return imageUrls;
    }
    
    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
    
    public PropertyDTO getProperty() {
        return property;
    }
    
    public void setProperty(PropertyDTO property) {
        this.property = property;
    }
    
    public UserDTO getTenant() {
        return tenant;
    }
    
    public void setTenant(UserDTO tenant) {
        this.tenant = tenant;
    }
} 