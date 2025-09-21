package com.propertymanagement.service.impl;

import com.propertymanagement.dto.auth.RegisterRequest;
import com.propertymanagement.exception.ResourceNotFoundException;
import com.propertymanagement.model.User;
import com.propertymanagement.model.UserRole;
import com.propertymanagement.repository.UserRepository;
import com.propertymanagement.repository.LeaseRepository;
import com.propertymanagement.service.CloudinaryService;
import com.propertymanagement.service.UserService;
import com.propertymanagement.service.base.impl.BaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl extends BaseServiceImpl<User, UserRepository> implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;
    private final LeaseRepository leaseRepository;

    @Autowired
    public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder, CloudinaryService cloudinaryService, LeaseRepository leaseRepository) {
        super(repository);
        this.passwordEncoder = passwordEncoder;
        this.cloudinaryService = cloudinaryService;
        this.leaseRepository = leaseRepository;
    }

    @Override
    @Transactional
    public User registerUser(RegisterRequest registerRequest) {
        if (existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setRole(registerRequest.getRole() != null ? registerRequest.getRole() : UserRole.TENANT);
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setActive(true);
        
        // Let the AuditorAware handle these values automatically
        // Don't set them manually to avoid conflicts
        
        return repository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(Long id, User user) {
        User existingUser = findById(id);
        
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setPhoneNumber(user.getPhoneNumber());
        existingUser.setProfileImage(user.getProfileImage());
        
        return repository.save(existingUser);
    }

    @Override
    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    @Override
    public List<User> findByRole(UserRole role) {
        return repository.findByRole(role);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public User updateUserProfile(Long id, String firstName, String lastName, String phoneNumber, String profileImage, String address) {
        User user = findById(id);
        logger.info("Updating profile for user ID: {}, email: {}", id, user.getEmail());
        
        if (firstName != null) {
            logger.debug("Updating firstName from '{}' to '{}'", user.getFirstName(), firstName);
            user.setFirstName(firstName);
        }
        if (lastName != null) {
            logger.debug("Updating lastName from '{}' to '{}'", user.getLastName(), lastName);
            user.setLastName(lastName);
        }
        if (phoneNumber != null) {
            logger.debug("Updating phoneNumber from '{}' to '{}'", user.getPhoneNumber(), phoneNumber);
            user.setPhoneNumber(phoneNumber);
        }
        if (address != null) {
            logger.debug("Updating address from '{}' to '{}'", user.getAddress(), address);
            user.setAddress(address);
        }
        
        // Handle profile image
        if (profileImage != null) {
            // If it's a base64 image, upload it to Cloudinary
            if (profileImage.startsWith("data:image")) {
                logger.info("Received base64 profile image data for user ID: {}, length: {}", id, profileImage.length());
                logger.debug("Base64 image prefix: {}", profileImage.substring(0, Math.min(30, profileImage.length())) + "...");
                
                String imageUrl = cloudinaryService.uploadImage(profileImage, "users");
                if (imageUrl != null) {
                    logger.info("Successfully uploaded profile image to Cloudinary: {}", imageUrl);
                    
                    // If the user already had a profile image, delete the old one
                    if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
                        logger.debug("Deleting old profile image: {}", user.getProfileImage());
                        String publicId = cloudinaryService.getPublicIdFromUrl(user.getProfileImage());
                        if (publicId != null) {
                            boolean deleted = cloudinaryService.deleteImage(publicId);
                            logger.debug("Old image deletion result: {}", deleted ? "success" : "failed");
                        } else {
                            logger.warn("Could not extract public ID from old image URL: {}", user.getProfileImage());
                        }
                    }
                    
                    user.setProfileImage(imageUrl);
                    logger.info("Profile image updated for user ID: {}", id);
                } else {
                    logger.error("Failed to upload profile image to Cloudinary for user ID: {}", id);
                }
            } else if (profileImage.isEmpty()) {
                // Remove existing profile image if empty string is provided
                logger.info("Removing profile image for user ID: {}", id);
                
                if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
                    logger.debug("Deleting existing profile image: {}", user.getProfileImage());
                    String publicId = cloudinaryService.getPublicIdFromUrl(user.getProfileImage());
                    if (publicId != null) {
                        boolean deleted = cloudinaryService.deleteImage(publicId);
                        logger.debug("Image deletion result: {}", deleted ? "success" : "failed");
                    } else {
                        logger.warn("Could not extract public ID from image URL: {}", user.getProfileImage());
                    }
                }
                
                user.setProfileImage(null);
                logger.info("Profile image removed for user ID: {}", id);
            } else {
                // If it's a URL or other string, just set it
                logger.info("Setting profile image URL directly: {}", profileImage);
                user.setProfileImage(profileImage);
            }
        } else {
            logger.debug("No profile image update requested (profileImage parameter is null)");
        }
        
        User savedUser = repository.save(user);
        logger.info("User profile updated successfully for ID: {}", id);
        return savedUser;
    }

    @Override
    @Transactional
    public User changeUserPassword(Long id, String currentPassword, String newPassword) {
        User user = findById(id);
        
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        return repository.save(user);
    }

    @Override
    @Transactional
    public User toggleUserStatus(Long id) {
        User user = findById(id);
        user.setActive(!user.isActive());
        return repository.save(user);
    }

    @Override
    public boolean hasRole(User user, String role) {
        if (user == null || role == null) {
            return false;
        }
        return user.getRole() != null && user.getRole().name().equals(role);
    }

    @Override
    public long countTenantsByLandlord(User landlord) {
        if (landlord == null) {
            return 0;
        }
        // Count unique tenants from leases associated with landlord's properties
        return leaseRepository.countDistinctTenantsByLandlord(landlord.getId());
    }
} 