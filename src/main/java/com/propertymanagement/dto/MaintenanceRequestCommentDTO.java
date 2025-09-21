package com.propertymanagement.dto;

import java.time.LocalDateTime;

public class MaintenanceRequestCommentDTO {
    
    private Long id;
    private Long requestId;
    private Long userId;
    private String userName;
    private String userRole;
    private String content;
    private LocalDateTime createdAt;
    
    // Default constructor
    public MaintenanceRequestCommentDTO() {}
    
    // Private constructor for builder
    private MaintenanceRequestCommentDTO(Builder builder) {
        this.id = builder.id;
        this.requestId = builder.requestId;
        this.userId = builder.userId;
        this.userName = builder.userName;
        this.userRole = builder.userRole;
        this.content = builder.content;
        this.createdAt = builder.createdAt;
    }
    
    // Static builder method
    public static Builder builder() {
        return new Builder();
    }
    
    // Builder class
    public static class Builder {
        private Long id;
        private Long requestId;
        private Long userId;
        private String userName;
        private String userRole;
        private String content;
        private LocalDateTime createdAt;
        
        public Builder id(Long id) {
            this.id = id;
            return this;
        }
        
        public Builder requestId(Long requestId) {
            this.requestId = requestId;
            return this;
        }
        
        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }
        
        public Builder userName(String userName) {
            this.userName = userName;
            return this;
        }
        
        public Builder userRole(String userRole) {
            this.userRole = userRole;
            return this;
        }
        
        public Builder content(String content) {
            this.content = content;
            return this;
        }
        
        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public MaintenanceRequestCommentDTO build() {
            return new MaintenanceRequestCommentDTO(this);
        }
    }
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getRequestId() {
        return requestId;
    }
    
    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getUserRole() {
        return userRole;
    }
    
    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 