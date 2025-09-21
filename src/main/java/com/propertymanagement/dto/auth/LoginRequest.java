package com.propertymanagement.dto.auth;

import lombok.Data;

@Data
public class LoginRequest {
    
    private String email;
    
    private String password;
    
    // Explicit getters and setters to avoid Lombok issues
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
} 