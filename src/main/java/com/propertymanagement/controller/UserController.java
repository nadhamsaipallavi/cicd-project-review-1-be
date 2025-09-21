package com.propertymanagement.controller;

import com.propertymanagement.controller.base.impl.BaseControllerImpl;
import com.propertymanagement.dto.UserDTO;
import com.propertymanagement.dto.UserProfileUpdateDTO;
import com.propertymanagement.mapper.UserMapper;
import com.propertymanagement.model.User;
import com.propertymanagement.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController extends BaseControllerImpl<User, UserDTO, UserService, UserMapper> {
    
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    public UserController(UserService userService, UserMapper userMapper) {
        super(userService, userMapper);
        this.userService = userService;
    }
    
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public UserDTO getCurrentUser() {
        return mapper.toDTO(userService.getCurrentUser());
    }
    
    @PutMapping("/{id}/profile")
    @PreAuthorize("isAuthenticated()")
    public UserDTO updateProfile(
            @PathVariable Long id,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String profileImage,
            @RequestParam(required = false) String address) {
        
        logger.debug("Updating profile via PUT for user ID: {}", id);
        logger.debug("Profile data - firstName: {}, lastName: {}, phoneNumber: {}, address: {}, profileImage: {}",
                firstName, lastName, phoneNumber, address, 
                profileImage != null ? (profileImage.length() > 100 ? "Base64 image data" : profileImage) : "null");
        
        return mapper.toDTO(userService.updateUserProfile(id, firstName, lastName, phoneNumber, profileImage, address));
    }
    
    @PostMapping("/{id}/profile")
    @PreAuthorize("isAuthenticated()")
    public UserDTO updateProfileJson(
            @PathVariable Long id,
            @RequestBody UserProfileUpdateDTO profileData) {
        
        logger.debug("Updating profile via POST for user ID: {}", id);
        logger.debug("Profile data from JSON - firstName: {}, lastName: {}, phoneNumber: {}, address: {}, profileImage: {}",
                profileData.getFirstName(), profileData.getLastName(), 
                profileData.getPhoneNumber(), profileData.getAddress(),
                profileData.getProfileImage() != null ? 
                    (profileData.getProfileImage().length() > 100 ? "Base64 image data present" : profileData.getProfileImage()) 
                    : "null");
        
        return mapper.toDTO(userService.updateUserProfile(
                id, 
                profileData.getFirstName(), 
                profileData.getLastName(), 
                profileData.getPhoneNumber(),
                profileData.getProfileImage(),
                profileData.getAddress()));
    }
    
    @PutMapping("/{id}/password")
    @PreAuthorize("isAuthenticated()")
    public UserDTO changePassword(
            @PathVariable Long id,
            @RequestParam String currentPassword,
            @RequestParam String newPassword) {
        return mapper.toDTO(userService.changeUserPassword(id, currentPassword, newPassword));
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public UserDTO toggleUserStatus(@PathVariable Long id) {
        return mapper.toDTO(userService.toggleUserStatus(id));
    }
} 