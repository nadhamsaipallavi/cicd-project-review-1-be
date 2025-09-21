package com.propertymanagement.util;

import com.propertymanagement.model.User;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {
    
    public boolean isAdmin(User user) {
        return user != null && "ADMIN".equals(user.getRole());
    }
    
    public boolean isLandlord(User user) {
        return user != null && "LANDLORD".equals(user.getRole());
    }
    
    public boolean isTenant(User user) {
        return user != null && "TENANT".equals(user.getRole());
    }
    
    public boolean canUserAccessProperty(User user, Long propertyId) {
        if (isAdmin(user)) {
            return true;
        }
        
        // For simplicity, we'll assume landlords and tenants can access properties
        // based on more specific business rules that would be implemented here
        return false;
    }
} 