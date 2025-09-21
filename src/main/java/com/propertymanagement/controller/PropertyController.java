package com.propertymanagement.controller;

import com.propertymanagement.dto.PropertyDTO;
import com.propertymanagement.mapper.PropertyMapper;
import com.propertymanagement.model.Property;
import com.propertymanagement.model.PropertyType;
import com.propertymanagement.model.User;
import com.propertymanagement.service.PropertyService;
import com.propertymanagement.service.UserService;
import com.propertymanagement.service.CloudinaryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {
    
    private final PropertyService propertyService;
    private final UserService userService;
    private final CloudinaryService cloudinaryService;
    private final PropertyMapper mapper;
    private static final Logger logger = LoggerFactory.getLogger(PropertyController.class);
    
    public PropertyController(PropertyService propertyService, PropertyMapper propertyMapper, 
                             UserService userService, CloudinaryService cloudinaryService) {
        this.propertyService = propertyService;
        this.mapper = propertyMapper;
        this.userService = userService;
        this.cloudinaryService = cloudinaryService;
    }
    
    @GetMapping
    public Page<PropertyDTO> findAll(Pageable pageable) {
        return propertyService.findAll(pageable).map(mapper::toDTO);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PropertyDTO> getPropertyById(@PathVariable Long id) {
        Property property = propertyService.findById(id);
        return ResponseEntity.ok(mapper.toDTO(property));
    }
    
    @PostMapping("/create")
    @PreAuthorize("hasRole('LANDLORD')")
    public PropertyDTO createProperty(@RequestBody PropertyDTO propertyDTO) {
        User currentUser = userService.getCurrentUser();
        Property property = mapper.toEntity(propertyDTO);
        return mapper.toDTO(propertyService.createProperty(property, currentUser));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<PropertyDTO> updateProperty(@PathVariable Long id, @RequestBody PropertyDTO propertyDTO) {
        if (!propertyService.isPropertyOwnedBy(id, userService.getCurrentUser())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Property property = mapper.toEntity(propertyDTO);
        property.setId(id);
        return ResponseEntity.ok(mapper.toDTO(propertyService.updateProperty(id, property)));
    }
    
    @PutMapping("/{id}/prices")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<PropertyDTO> updatePropertyPrices(
            @PathVariable Long id,
            @RequestBody Map<String, Object> priceData) {
        
        if (!propertyService.isPropertyOwnedBy(id, userService.getCurrentUser())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Property property = propertyService.findById(id);
        
        // Update monthly rent if provided
        if (priceData.containsKey("monthlyRent")) {
            Object monthlyRentObj = priceData.get("monthlyRent");
            if (monthlyRentObj == null) {
                property.setMonthlyRent(null);
            } else {
                try {
                    BigDecimal monthlyRent = new BigDecimal(monthlyRentObj.toString());
                    property.setMonthlyRent(monthlyRent);
                } catch (NumberFormatException e) {
                    logger.error("Invalid monthly rent format: {}", monthlyRentObj);
                    return ResponseEntity.badRequest().build();
                }
            }
        }
        
        // Update sale price if provided
        if (priceData.containsKey("salePrice")) {
            Object salePriceObj = priceData.get("salePrice");
            if (salePriceObj == null) {
                property.setSalePrice(null);
            } else {
                try {
                    BigDecimal salePrice = new BigDecimal(salePriceObj.toString());
                    property.setSalePrice(salePrice);
                } catch (NumberFormatException e) {
                    logger.error("Invalid sale price format: {}", salePriceObj);
                    return ResponseEntity.badRequest().build();
                }
            }
        }
        
        // Update security deposit if provided
        if (priceData.containsKey("securityDeposit")) {
            Object securityDepositObj = priceData.get("securityDeposit");
            if (securityDepositObj == null) {
                property.setSecurityDeposit(null);
            } else {
                try {
                    BigDecimal securityDeposit = new BigDecimal(securityDepositObj.toString());
                    property.setSecurityDeposit(securityDeposit);
                } catch (NumberFormatException e) {
                    logger.error("Invalid security deposit format: {}", securityDepositObj);
                    return ResponseEntity.badRequest().build();
                }
            }
        }
        
        Property updatedProperty = propertyService.updateProperty(id, property);
        return ResponseEntity.ok(mapper.toDTO(updatedProperty));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        if (!propertyService.isPropertyOwnedBy(id, userService.getCurrentUser())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        propertyService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<Map<String, Object>> uploadPropertyImages(
            @PathVariable Long id,
            @RequestParam("images") List<MultipartFile> images) {
        
        if (!propertyService.isPropertyOwnedBy(id, userService.getCurrentUser())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Property property = propertyService.findById(id);
        List<String> uploadedImageUrls = new ArrayList<>();
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Limit to max 5 images total
            int maxImages = 5;
            List<String> currentImages = property.getImages();
            if (currentImages == null) {
                currentImages = new ArrayList<>();
            }
            
            int availableSlots = maxImages - currentImages.size();
            
            if (availableSlots <= 0) {
                response.put("success", false);
                response.put("message", "Maximum number of images (5) already reached. Please delete some images first.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            int toUpload = Math.min(availableSlots, images.size());
            
            // Process each image
            for (int i = 0; i < toUpload; i++) {
                MultipartFile image = images.get(i);
                
                // Validate file type
                String contentType = image.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    response.put("success", false);
                    response.put("message", "Invalid file type. Only images are allowed.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
                
                // Validate file size (max 5MB)
                if (image.getSize() > 5 * 1024 * 1024) {
                    response.put("success", false);
                    response.put("message", "Image size should be less than 5MB.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
                
                String base64Image = "data:" + image.getContentType() + ";base64," + 
                                    java.util.Base64.getEncoder().encodeToString(image.getBytes());
                String imageUrl = cloudinaryService.uploadImage(base64Image, "properties");
                if (imageUrl != null) {
                    uploadedImageUrls.add(imageUrl);
                }
            }
            
            if (!uploadedImageUrls.isEmpty()) {
                // Add new images to existing ones
                currentImages.addAll(uploadedImageUrls);
                property.setImages(currentImages);
                propertyService.updateProperty(id, property);
            }
            
            response.put("success", true);
            response.put("imageUrls", uploadedImageUrls);
            response.put("message", "Images uploaded successfully");
            response.put("totalImages", currentImages.size());
            response.put("availableSlots", maxImages - currentImages.size());
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Failed to upload images: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/{id}/images/{imageIndex}")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<Map<String, Object>> deletePropertyImage(
            @PathVariable Long id,
            @PathVariable int imageIndex) {
        
        if (!propertyService.isPropertyOwnedBy(id, userService.getCurrentUser())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Property property = propertyService.findById(id);
        List<String> images = property.getImages();
        
        if (images == null || images.isEmpty() || imageIndex < 0 || imageIndex >= images.size()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Image not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        String imageUrl = images.get(imageIndex);
        // Delete from Cloudinary if it's a Cloudinary URL
        String publicId = cloudinaryService.getPublicIdFromUrl(imageUrl);
        if (publicId != null) {
            cloudinaryService.deleteImage(publicId);
        }
        
        // Remove from property's image list
        images.remove(imageIndex);
        property.setImages(images);
        propertyService.updateProperty(id, property);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Image deleted successfully");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/available")
    public Page<PropertyDTO> findAllAvailable(Pageable pageable) {
        return propertyService.findAllAvailable(pageable).map(mapper::toDTO);
    }
    
    @GetMapping("/landlord")
    @PreAuthorize("hasRole('LANDLORD')")
    public Page<PropertyDTO> findByLandlord(Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        return propertyService.findByLandlord(currentUser, pageable).map(mapper::toDTO);
    }
    
    @GetMapping("/search")
    public Page<PropertyDTO> searchProperties(
            @RequestParam(required = false) PropertyType propertyType,
            @RequestParam(required = false) BigDecimal maxRent,
            @RequestParam(required = false) Integer minBedrooms,
            Pageable pageable) {
        return propertyService.searchProperties(propertyType, maxRent, minBedrooms, pageable)
                .map(mapper::toDTO);
    }
    
    @GetMapping("/featured")
    public List<PropertyDTO> findFeaturedProperties() {
        return propertyService.findFeaturedProperties().stream()
                .map(mapper::toDTO)
                .toList();
    }
    
    @PutMapping("/{id}/availability")
    @PreAuthorize("hasRole('LANDLORD')")
    public PropertyDTO togglePropertyAvailability(@PathVariable Long id) {
        if (!propertyService.isPropertyOwnedBy(id, userService.getCurrentUser())) {
            throw new IllegalStateException("Property not owned by current user");
        }
        return mapper.toDTO(propertyService.togglePropertyAvailability(id));
    }
    
    @PutMapping("/{id}/featured")
    @PreAuthorize("hasRole('ADMIN')")
    public PropertyDTO togglePropertyFeatured(@PathVariable Long id) {
        return mapper.toDTO(propertyService.togglePropertyFeatured(id));
    }
    
    @GetMapping("/{id}/available")
    public boolean isPropertyAvailable(@PathVariable Long id) {
        return propertyService.isPropertyAvailable(id);
    }
    
    @GetMapping("/stats")
    @PreAuthorize("hasRole('LANDLORD')")
    public PropertyStats getPropertyStats() {
        User currentUser = userService.getCurrentUser();
        return new PropertyStats(
                propertyService.countPropertiesByLandlord(currentUser),
                propertyService.countAvailableProperties()
        );
    }
    
    @PostMapping("/{id}/update-images")
    public ResponseEntity<Map<String, Object>> updatePropertyImages(
            @PathVariable("id") Long id,
            @RequestBody Map<String, List<String>> requestBody) {
        
        try {
            // Get the list of image URLs from the request body
            List<String> imageUrls = requestBody.get("imageUrls");
            
            if (imageUrls == null || imageUrls.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", "No image URLs provided");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Find the property
            Property property = propertyService.findById(id);
            
            if (property == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", "Property not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            // Update the property's images
            property.setImages(imageUrls);
            propertyService.updateProperty(id, property);
            
            // Prepare the response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Property images updated successfully");
            response.put("imageUrls", imageUrls);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error updating property images: ", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Failed to update property images: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    private record PropertyStats(long totalProperties, long availableProperties) {}
} 