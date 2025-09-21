package com.propertymanagement.controller;

import com.propertymanagement.service.CloudinaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Base64;

@RestController
@RequestMapping("/api/images")
public class ImageController {
    
    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);
    private final CloudinaryService cloudinaryService;
    
    @Value("${app.local-storage.path:./uploads}")
    private String localStoragePath;
    
    @Autowired
    public ImageController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }
    
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        logger.debug("Received image upload request");
        
        if (request == null || !request.containsKey("imageData") || request.get("imageData") == null || request.get("imageData").isEmpty()) {
            logger.warn("No image data provided in request");
            response.put("success", false);
            response.put("error", "No image data provided");
            return ResponseEntity.badRequest().body(response);
        }
        
        String imageData = request.get("imageData");
        
        try {
            // Validate that this is a valid base64 image
            if (!isValidBase64Image(imageData)) {
                logger.warn("Invalid base64 image data format");
                response.put("success", false);
                response.put("error", "Invalid image data format");
                return ResponseEntity.badRequest().body(response);
            }
            
            logger.info("Uploading image to storage service");
            String imageUrl = cloudinaryService.uploadImage(imageData, "properties");
            
            if (imageUrl != null) {
                logger.info("Image uploaded successfully: {}", imageUrl);
                response.put("success", true);
                response.put("secureUrl", imageUrl);
                return ResponseEntity.ok(response);
            } else {
                logger.error("Storage service returned null URL");
                response.put("success", false);
                response.put("error", "Failed to upload image to storage service");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            logger.error("Exception during image upload: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/upload-file")
    public ResponseEntity<Map<String, Object>> uploadImageFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        if (file.isEmpty()) {
            logger.warn("Empty file received in upload-file endpoint");
            response.put("success", false);
            response.put("error", "No file provided");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            logger.debug("Received file upload: {} ({} bytes, type: {})", 
                file.getOriginalFilename(), file.getSize(), file.getContentType());
            
            // Validate content type
            if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
                logger.warn("Invalid file type: {}", file.getContentType());
                response.put("success", false);
                response.put("error", "Only image files are allowed");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Convert MultipartFile to base64
            String base64Image = "data:" + file.getContentType() + ";base64," + 
                                Base64.getEncoder().encodeToString(file.getBytes());
            
            logger.info("Uploading file to Cloudinary service");            
            String imageUrl = cloudinaryService.uploadImage(base64Image, "properties");
            
            if (imageUrl != null) {
                logger.info("File uploaded successfully: {}", imageUrl);
                response.put("secureUrl", imageUrl);
                response.put("success", true);
                return ResponseEntity.ok(response);
            } else {
                logger.error("Cloudinary service returned null URL for file upload");
                response.put("success", false);
                response.put("error", "Failed to upload image to cloud service");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (IOException e) {
            logger.error("IO Exception during file upload: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Failed to process image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            logger.error("Unexpected exception during file upload: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteImage(@RequestParam String imageUrl) {
        Map<String, Object> response = new HashMap<>();
        
        if (imageUrl == null || imageUrl.isEmpty()) {
            logger.warn("Empty image URL in delete request");
            response.put("success", false);
            response.put("error", "Image URL is required");
            return ResponseEntity.badRequest().body(response);
        }
        
        String publicId = cloudinaryService.getPublicIdFromUrl(imageUrl);
        
        if (publicId == null) {
            logger.warn("Could not extract public ID from URL: {}", imageUrl);
            response.put("success", false);
            response.put("error", "Invalid image URL format");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            logger.info("Attempting to delete image with public ID: {}", publicId);
            boolean deleted = cloudinaryService.deleteImage(publicId);
            
            if (deleted) {
                logger.info("Image deleted successfully: {}", publicId);
                response.put("success", true);
                response.put("message", "Image deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                logger.error("Failed to delete image: {}", publicId);
                response.put("success", false);
                response.put("error", "Failed to delete image");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            logger.error("Exception during image deletion: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if Cloudinary is initialized
            boolean cloudinaryAvailable = cloudinaryService != null;
            
            response.put("status", "UP");
            response.put("cloudinaryAvailable", cloudinaryAvailable);
            response.put("timestamp", System.currentTimeMillis());
            
            logger.info("Health check completed: Cloudinary available = {}", cloudinaryAvailable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Health check failed: {}", e.getMessage(), e);
            response.put("status", "DOWN");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/cloudinary-test")
    public ResponseEntity<Map<String, Object>> testCloudinaryConnection() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if Cloudinary service is available
            boolean available = cloudinaryService.isCloudinaryAvailable();
            response.put("cloudinaryAvailable", available);
            
            if (available) {
                // Try to upload a simple test image
                String testImage = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg==";
                String imageUrl = cloudinaryService.uploadImage(testImage, "test");
                
                if (imageUrl != null) {
                    response.put("success", true);
                    response.put("testImageUrl", imageUrl);
                    response.put("message", "Test image uploaded successfully!");
                    logger.info("Cloudinary test successful, image URL: {}", imageUrl);
                } else {
                    response.put("success", false);
                    response.put("error", "Failed to upload test image to Cloudinary");
                    logger.error("Failed to upload test image to Cloudinary");
                }
            } else {
                response.put("success", false);
                response.put("error", "Cloudinary service is not available");
                logger.error("Cloudinary service is not available for testing");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error testing Cloudinary connection: " + e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Validates if the given string is a valid base64 image.
     * 
     * @param imageData The base64 image data string to validate
     * @return true if the string is a valid base64 image, false otherwise
     */
    private boolean isValidBase64Image(String imageData) {
        if (imageData == null || imageData.isEmpty()) {
            return false;
        }
        
        // Check if it has the data URL format
        if (imageData.startsWith("data:image/")) {
            // Get the base64 part after the comma
            int commaIndex = imageData.indexOf(",");
            if (commaIndex == -1) {
                return false;
            }
            
            String base64Part = imageData.substring(commaIndex + 1);
            return isValidBase64(base64Part);
        } else {
            // Check if it's a raw base64 string
            return isValidBase64(imageData);
        }
    }
    
    /**
     * Checks if a string is valid base64 encoded.
     * 
     * @param base64 The string to check
     * @return true if it's valid base64, false otherwise
     */
    private boolean isValidBase64(String base64) {
        try {
            Base64.getDecoder().decode(base64);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * Serve local images from the file system.
     * This endpoint handles requests to /api/images/local/{folder}/{filename}
     */
    @GetMapping("/local/{folder}/{filename:.+}")
    public ResponseEntity<Resource> serveLocalImage(@PathVariable String folder, @PathVariable String filename) {
        try {
            // Construct the file path
            Path filePath = Paths.get(localStoragePath, folder, filename);
            Resource resource = new UrlResource(filePath.toUri());
            
            // Check if the file exists
            if (!resource.exists() || !resource.isReadable()) {
                logger.warn("Local image not found: {}/{}", folder, filename);
                return ResponseEntity.notFound().build();
            }
            
            // Determine media type
            String contentType = "image/jpeg"; // default
            if (filename.endsWith(".png")) {
                contentType = "image/png";
            } else if (filename.endsWith(".gif")) {
                contentType = "image/gif";
            } else if (filename.endsWith(".webp")) {
                contentType = "image/webp";
            }
            
            logger.debug("Serving local image: {}/{}", folder, filename);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            logger.error("Malformed URL error serving local image: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error serving local image: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 