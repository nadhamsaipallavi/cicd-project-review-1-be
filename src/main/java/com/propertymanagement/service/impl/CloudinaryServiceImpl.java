package com.propertymanagement.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.propertymanagement.service.CloudinaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {
    
    private static final Logger logger = LoggerFactory.getLogger(CloudinaryServiceImpl.class);
    private static final Pattern PUBLIC_ID_PATTERN = Pattern.compile("/v\\d+/([^/]+/[^.]+)");
    
    @Value("${cloudinary.cloud-name}")
    private String cloudName;
    
    @Value("${cloudinary.api-key}")
    private String apiKey;
    
    @Value("${cloudinary.api-secret}")
    private String apiSecret;
    
    @Value("${cloudinary.timeout:60000}")
    private int timeout;
    
    // Local file storage configuration
    @Value("${app.local-storage.enabled:true}")
    private boolean localStorageEnabled;
    
    @Value("${app.local-storage.path:./uploads}")
    private String localStoragePath;
    
    @Value("${app.server.base-url:http://localhost:8080}")
    private String serverBaseUrl;
    
    private Cloudinary cloudinary;
    
    @PostConstruct
    public void init() {
        try {
            // Debug: log out config values (securely)
            logger.info("Initializing Cloudinary with cloud name: {}, API key length: {}, API secret: {}",
                    cloudName,
                    apiKey != null ? apiKey.length() : "null",
                    apiSecret != null ? "[HIDDEN]" : "null");
            
            if (StringUtils.isEmpty(cloudName) || StringUtils.isEmpty(apiKey) || StringUtils.isEmpty(apiSecret)) {
                logger.error("Cloudinary configuration is incomplete. Check application.properties for cloudinary.cloud-name, cloudinary.api-key, and cloudinary.api-secret");
                return;
            }
            
            Map<String, Object> config = new HashMap<>();
            config.put("cloud_name", cloudName);
            config.put("api_key", apiKey);
            config.put("api_secret", apiSecret);
            config.put("secure", true);
            
            // Add connection and read timeout settings
            Map<String, Integer> timeouts = new HashMap<>();
            timeouts.put("connect_timeout", timeout);
            timeouts.put("read_timeout", timeout);
            config.put("connection_timeout", timeout);
            config.put("timeout", timeout);
            
            cloudinary = new Cloudinary(config);
            logger.info("Cloudinary initialized successfully");
            
            // Test connection
            try {
                Map result = cloudinary.api().ping(ObjectUtils.emptyMap());
                logger.info("Cloudinary connection test successful: {}", result);
            } catch (Exception e) {
                logger.error("Cloudinary connection test failed: {}", e.getMessage());
                // We don't want to throw an exception here, just log it
            }
        } catch (Exception e) {
            logger.error("Failed to initialize Cloudinary: {}", e.getMessage(), e);
        }
    }
    
    @Override
    public String uploadImage(String base64Image, String folder) {
        if (cloudinary == null) {
            logger.error("Cloudinary not initialized properly. Check your configuration.");
            return null;
        }
        
        if (base64Image == null || base64Image.isEmpty()) {
            logger.warn("Base64 image data is null or empty");
            return null;
        }
        
        try {
            logger.debug("Starting image upload to Cloudinary");
            
            // Track values for debugging
            logger.debug("Cloud name: {}, API key length: {}, API secret: {}", 
                cloudName, 
                apiKey != null ? apiKey.length() : "null", 
                apiSecret != null ? (apiSecret.length() > 4 ? apiSecret.substring(0, 4) + "..." : apiSecret) : "null");
            
            // Process the base64 string
            String imageData = base64Image;
            if (base64Image.contains(",")) {
                logger.debug("Base64 image contains data URL prefix, removing it");
                imageData = base64Image.substring(base64Image.indexOf(",") + 1);
            }
            
            // Log a small sample of the image data for debugging
            String dataSample = imageData.length() > 20 ? imageData.substring(0, 20) + "..." : imageData;
            logger.debug("Image data sample: {}, Length: {}", dataSample, imageData.length());
            
            Map<String, Object> options = ObjectUtils.asMap(
                "folder", (folder != null && !folder.isEmpty()) ? folder : "uploads",
                "resource_type", "auto",
                "unique_filename", true
            );
            
            logger.debug("Uploading to Cloudinary folder: {}", options.get("folder"));
            
            // First attempt - direct base64 string
            try {
                logger.debug("Attempting first upload method (direct base64)");
                Map uploadResult = cloudinary.uploader().upload(imageData, options);
                String secureUrl = (String) uploadResult.get("secure_url");
                logger.info("Image uploaded to Cloudinary successfully: {}", secureUrl);
                return secureUrl;
            } catch (Exception e) {
                logger.warn("First upload attempt failed: {} - {}", e.getClass().getName(), e.getMessage());
                
                // Second attempt - try with data URI format
                try {
                    logger.debug("Attempting second upload method (data URI)");
                    String dataUri = "data:image/jpeg;base64," + imageData;
                    Map uploadResult = cloudinary.uploader().upload(dataUri, options);
                    String secureUrl = (String) uploadResult.get("secure_url");
                    logger.info("Image uploaded to Cloudinary using data URI: {}", secureUrl);
                    return secureUrl;
                } catch (Exception e2) {
                    logger.warn("Second upload attempt failed: {} - {}", e2.getClass().getName(), e2.getMessage());
                    
                    // Third attempt - try with byte array
                    try {
                        logger.debug("Attempting third upload method (byte array)");
                        byte[] imageBytes = Base64.getDecoder().decode(imageData);
                        logger.debug("Decoded base64 to byte array, length: {}", imageBytes.length);
                        Map uploadResult = cloudinary.uploader().upload(imageBytes, options);
                        String secureUrl = (String) uploadResult.get("secure_url");
                        logger.info("Image uploaded to Cloudinary using byte array: {}", secureUrl);
                        return secureUrl;
                    } catch (Exception e3) {
                        logger.error("All upload attempts failed. Final error: {} - {}", e3.getClass().getName(), e3.getMessage());
                        // Try to identify if it's credentials issue
                        if (e3.getMessage() != null && 
                           (e3.getMessage().contains("authentication") || 
                            e3.getMessage().contains("Invalid") || 
                            e3.getMessage().contains("signature"))) {
                            logger.error("Possible authentication issue with Cloudinary. Please check your credentials.");
                        }
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error in uploadImage method: {} - {}", e.getClass().getName(), e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    public boolean deleteImage(String publicId) {
        if (cloudinary == null) {
            logger.error("Cloudinary not initialized properly. Check your configuration.");
            return false;
        }
        
        if (publicId == null || publicId.isEmpty()) {
            logger.warn("Public ID is null or empty");
            return false;
        }
        
        try {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            String status = (String) result.get("result");
            logger.info("Image deletion result: {}", status);
            return "ok".equals(status);
        } catch (IOException e) {
            logger.error("Error deleting image from Cloudinary", e);
            return false;
        }
    }
    
    @Override
    public String getPublicIdFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        
        Matcher matcher = PUBLIC_ID_PATTERN.matcher(url);
        if (matcher.find()) {
            String publicId = matcher.group(1);
            logger.debug("Extracted public ID from URL: {}", publicId);
            return publicId;
        }
        
        logger.warn("Could not extract public ID from URL: {}", url);
        return null;
    }
    
    @Override
    public boolean isCloudinaryAvailable() {
        if (cloudinary == null) {
            logger.error("Cloudinary not initialized");
            return false;
        }
        
        try {
            // Ping Cloudinary service to verify credentials are working
            Map result = cloudinary.api().ping(ObjectUtils.emptyMap());
            logger.info("Cloudinary ping result: {}", result);
            
            // If status is "ok", Cloudinary is available
            return "ok".equals(result.get("status"));
        } catch (Exception e) {
            logger.error("Failed to check Cloudinary availability: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        if (cloudinary == null) {
            logger.error("Cloudinary not initialized");
            return null;
        }
        
        if (file == null || file.isEmpty()) {
            logger.error("Cannot upload empty file");
            return null;
        }
        
        try {
            logger.info("Starting file upload to Cloudinary, file size: {} bytes", file.getSize());
            
            // Prepare upload parameters
            Map<String, Object> params = new HashMap<>();
            if (folder != null && !folder.isEmpty()) {
                params.put("folder", folder);
            }
            params.put("resource_type", "auto"); // Auto-detect resource type
            
            // Upload the file directly
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
            
            // Get the secure URL from the response
            String secureUrl = (String) uploadResult.get("secure_url");
            logger.info("File successfully uploaded to Cloudinary. URL: {}", secureUrl);
            
            return secureUrl;
        } catch (IOException e) {
            logger.error("IO error when uploading file to Cloudinary: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error uploading file to Cloudinary: {}", e.getMessage(), e);
        }
        
        return null;
    }
} 