package com.propertymanagement.service;

import com.propertymanagement.dto.auth.RegisterRequest;
import com.propertymanagement.model.User;
import com.propertymanagement.model.UserRole;
import com.propertymanagement.service.base.BaseService;

import java.util.List;
import java.util.Optional;

public interface UserService extends BaseService<User> {
    
    User registerUser(RegisterRequest registerRequest);
    
    User getCurrentUser();
    
    User updateUser(Long id, User user);
    
    User findById(Long id);
    
    Optional<User> findByEmail(String email);
    
    List<User> findAll();
    
    List<User> findByRole(UserRole role);
    
    boolean existsByEmail(String email);
    
    void deleteUser(Long id);
    
    User updateUserProfile(Long id, String firstName, String lastName, String phoneNumber, String profileImage, String address);
    
    User changeUserPassword(Long id, String currentPassword, String newPassword);
    
    User toggleUserStatus(Long id);
    
    /**
     * Checks if a user has a specific role
     */
    boolean hasRole(User user, String role);
    
    /**
     * Counts tenants associated with a landlord's properties
     */
    long countTenantsByLandlord(User landlord);
} 