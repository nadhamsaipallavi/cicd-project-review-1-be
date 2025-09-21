package com.propertymanagement.dto;

import com.propertymanagement.dto.base.BaseDTO;
import com.propertymanagement.model.UserRole;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserDTO extends BaseDTO {
    
    private String email;
    
    private String firstName;
    
    private String lastName;
    
    private String phoneNumber;
    
    private String profileImage;
    
    private String address;
    
    private UserRole role;
    
    private boolean active;
} 