package com.propertymanagement.controller.auth;

import com.propertymanagement.dto.auth.JwtResponse;
import com.propertymanagement.dto.auth.LoginRequest;
import com.propertymanagement.dto.auth.RegisterRequest;
import com.propertymanagement.model.User;
import com.propertymanagement.model.UserRole;
import com.propertymanagement.security.JwtTokenProvider;
import com.propertymanagement.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);
        
        User user = (User) authentication.getPrincipal();
        
        JwtResponse response = new JwtResponse();
        response.setToken(jwt);
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setRole(user.getRole());
        response.setProfileImage(user.getProfileImage());
        response.setAddress(user.getAddress());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register")
    public ResponseEntity<JwtResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            logger.info("Registering new user with email: {}", registerRequest.getEmail());
            
            // Default to tenant role if not specified
            if (registerRequest.getRole() == null) {
                registerRequest.setRole(UserRole.TENANT);
            }
            
            // Register the user without authentication
            User user = userService.registerUser(registerRequest);
            logger.info("User registered successfully with ID: {}", user.getId());
            
            try {
                // Generate token directly from user details
                String jwt = jwtTokenProvider.generateTokenForUser(user);
                logger.info("JWT token generated successfully");
                
                // Build the response
                JwtResponse response = new JwtResponse();
                response.setToken(jwt);
                response.setId(user.getId());
                response.setEmail(user.getEmail());
                response.setFirstName(user.getFirstName());
                response.setLastName(user.getLastName());
                response.setRole(user.getRole());
                response.setProfileImage(user.getProfileImage());
                response.setAddress(user.getAddress());
                
                return ResponseEntity.ok(response);
            } catch (Exception tokenEx) {
                // Log the token generation error
                logger.error("Failed to generate JWT token: {}", tokenEx.getMessage(), tokenEx);
                
                // Return user data without token as fallback
                JwtResponse response = new JwtResponse();
                response.setId(user.getId());
                response.setEmail(user.getEmail());
                response.setFirstName(user.getFirstName());
                response.setLastName(user.getLastName());
                response.setRole(user.getRole());
                
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            logger.error("Registration failed: {}", e.getMessage(), e);
            throw new RuntimeException("Registration failed: " + e.getMessage(), e);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logoutUser() {
        try {
            // Clear the security context
            SecurityContextHolder.clearContext();
            logger.info("User logged out successfully");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Logged out successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error during logout: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Failed to logout: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
} 