package com.propertymanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

// Removing @Configuration to disable this class
// @Configuration
// Renamed to LegacySecurityConfig
public class MinimalSecurityConfig {

    // Removed duplicate passwordEncoder bean
    
    // Removed AuthenticationManager bean to avoid conflicts
    // Already defined in AppSecurityConfig
} 