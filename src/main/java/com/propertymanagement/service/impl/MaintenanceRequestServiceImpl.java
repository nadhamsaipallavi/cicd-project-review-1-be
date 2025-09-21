package com.propertymanagement.service.impl;

import com.propertymanagement.dto.MaintenanceRequestCommentDTO;
import com.propertymanagement.dto.MaintenanceRequestDTO;
import com.propertymanagement.model.MaintenanceRequest;
import com.propertymanagement.model.MaintenanceRequestPriority;
import com.propertymanagement.model.MaintenanceRequestStatus;
import com.propertymanagement.model.Property;
import com.propertymanagement.model.User;
import com.propertymanagement.repository.MaintenanceRequestRepository;
import com.propertymanagement.repository.PropertyRepository;
import com.propertymanagement.service.FileStorageService;
import com.propertymanagement.service.MaintenanceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

@Service
public class MaintenanceRequestServiceImpl implements MaintenanceRequestService {

    @Autowired
    private MaintenanceRequestRepository maintenanceRequestRepository;
    
    @Autowired
    private PropertyRepository propertyRepository;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @Override
    public Page<MaintenanceRequest> findAll(Pageable pageable) {
        return maintenanceRequestRepository.findAll(pageable);
    }
    
    @Override
    public MaintenanceRequest findById(Long id) {
        return maintenanceRequestRepository.findById(id).orElse(null);
    }
    
    @Override
    public Page<MaintenanceRequest> findByPropertyId(Long propertyId, User currentUser, Pageable pageable) {
        // Simplified implementation
        return Page.empty();
    }
    
    @Override
    public Page<MaintenanceRequest> findByLandlord(User landlord, Pageable pageable) {
        try {
            // Get all properties owned by the landlord
            List<Property> landlordProperties = propertyRepository.findByLandlord(landlord);
            
            if (landlordProperties.isEmpty()) {
                return Page.empty(pageable);
            }
            
            // Find maintenance requests for these properties
            return maintenanceRequestRepository.findByPropertyIn(landlordProperties, pageable);
        } catch (Exception e) {
            e.printStackTrace();
            return Page.empty(pageable);
        }
    }
    
    @Override
    public Page<MaintenanceRequest> findByTenant(User tenant, Pageable pageable) {
        try {
            // Find maintenance requests created by this tenant
            return maintenanceRequestRepository.findByUser(tenant, pageable);
        } catch (Exception e) {
            e.printStackTrace();
            return Page.empty(pageable);
        }
    }
    
    @Override
    public List<MaintenanceRequest> findRecentRequests(int limit) {
        try {
            return maintenanceRequestRepository.findRecentActiveRequests(
                org.springframework.data.domain.PageRequest.of(0, limit)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    @Override
    public List<MaintenanceRequest> findRecentRequestsByLandlord(User landlord, int limit) {
        try {
            // Get all properties owned by the landlord
            List<Property> landlordProperties = propertyRepository.findByLandlord(landlord);
            
            if (landlordProperties.isEmpty()) {
                return Collections.emptyList();
            }
            
            // Find recent maintenance requests for these properties
            return maintenanceRequestRepository.findRecentActiveRequestsByProperties(
                landlordProperties,
                org.springframework.data.domain.PageRequest.of(0, limit)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    @Override
    public boolean canAccess(MaintenanceRequest request, User user) {
        // Simplified implementation
        return true;
    }
    
    @Override
    public boolean canModify(MaintenanceRequest request, User user) {
        // Simplified implementation
        return true;
    }
    
    @Override
    public MaintenanceRequest createRequest(MaintenanceRequestDTO requestDTO, List<org.springframework.web.multipart.MultipartFile> images, User tenant) {
        try {
            // Create a new maintenance request
            MaintenanceRequest request = new MaintenanceRequest();
            
            // Set basic properties
            request.setTitle(requestDTO.getTitle());
            request.setDescription(requestDTO.getDescription());
            request.setUser(tenant);
            
            // Find and set the property
            Property property = null;
            if (requestDTO.getPropertyId() != null) {
                property = propertyRepository.findById(requestDTO.getPropertyId()).orElse(null);
                if (property == null) {
                    throw new IllegalArgumentException("Property not found with ID: " + requestDTO.getPropertyId());
                }
            } else {
                throw new IllegalArgumentException("Property ID is required");
            }
            request.setProperty(property);
            
            // Set priority
            try {
                MaintenanceRequestPriority priority = MaintenanceRequestPriority.valueOf(requestDTO.getPriority());
                request.setPriority(priority);
            } catch (IllegalArgumentException e) {
                // Default to MEDIUM if invalid priority
                request.setPriority(MaintenanceRequestPriority.MEDIUM);
            }
            
            // Set status to PENDING
            request.setStatus(MaintenanceRequestStatus.PENDING);
            
            // Save the request to get an ID
            MaintenanceRequest savedRequest = maintenanceRequestRepository.save(request);
            
            // Handle image uploads if any
            if (images != null && !images.isEmpty()) {
                List<String> imageUrls = new ArrayList<>();
                
                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        // Generate a unique filename
                        String filename = "maintenance_" + savedRequest.getId() + "_" + 
                                          System.currentTimeMillis() + "_" + 
                                          image.getOriginalFilename().replaceAll("\\s+", "_");
                        
                        // Save the file and get the URL
                        String subDirectory = "maintenance";
                        String imageUrl = fileStorageService.storeFile(image, subDirectory);
                        imageUrls.add(imageUrl);
                    }
                }
                
                // Update the request with image URLs
                savedRequest.setImages(imageUrls);
                savedRequest = maintenanceRequestRepository.save(savedRequest);
            }
            
            return savedRequest;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create maintenance request: " + e.getMessage(), e);
        }
    }
    
    @Override
    public MaintenanceRequest updateRequest(Long id, MaintenanceRequestDTO requestDTO, List<org.springframework.web.multipart.MultipartFile> images, User user) {
        // Simplified implementation
        return null;
    }
    
    @Override
    public MaintenanceRequest updateStatus(Long id, String status, User user) {
        try {
            // Find the maintenance request
            MaintenanceRequest request = maintenanceRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maintenance request not found with ID: " + id));
            
            // Check if the user has permission to update the status
            if (!canModify(request, user)) {
                throw new IllegalArgumentException("You don't have permission to update this maintenance request");
            }
            
            // Update the status
            try {
                MaintenanceRequestStatus newStatus = MaintenanceRequestStatus.valueOf(status);
                request.setStatus(newStatus);
                
                // If the status is COMPLETED, set the resolved date
                if (newStatus == MaintenanceRequestStatus.COMPLETED) {
                    request.setResolvedAt(java.time.LocalDateTime.now());
                }
                
                // Save and return the updated request
                return maintenanceRequestRepository.save(request);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status: " + status);
            }
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                throw e;
            }
            throw new RuntimeException("Failed to update maintenance request status: " + e.getMessage(), e);
        }
    }
    
    @Override
    public MaintenanceRequest resolveRequest(Long id, String resolutionNotes, User user) {
        // Simplified implementation
        return null;
    }
    
    @Override
    public MaintenanceRequestCommentDTO addComment(Long requestId, String content, User user) {
        // Simplified implementation
        return null;
    }
    
    @Override
    public List<MaintenanceRequestCommentDTO> getComments(Long requestId, User user) {
        // Simplified implementation
        return Collections.emptyList();
    }
    
    @Override
    public int countPendingRequestsByLandlord(User landlord) {
        // Simplified implementation
        return 0;
    }
    
    @Override
    public int countPendingRequestsByTenant(User tenant) {
        // Simplified implementation
        return 0;
    }
    
    @Override
    public long countActiveRequestsByLandlord(User landlord) {
        // Simplified implementation - return a placeholder value
        // In a real implementation, you would query for maintenance requests with status "ACTIVE" or "PENDING"
        // that are associated with properties owned by this landlord
        return 0;
    }
    
    @Override
    public MaintenanceRequestDTO convertToDTO(MaintenanceRequest request) {
        if (request == null) {
            return null;
        }
        
        MaintenanceRequestDTO dto = new MaintenanceRequestDTO();
        dto.setId(request.getId());
        dto.setTitle(request.getTitle());
        dto.setDescription(request.getDescription());
        dto.setStatus(request.getStatus().name());
        dto.setPriority(request.getPriority().name());
        dto.setCreatedAt(request.getCreatedAt());
        dto.setUpdatedAt(request.getUpdatedAt());
        dto.setResolvedAt(request.getResolvedAt());
        dto.setResolutionNotes(request.getResolution());
        dto.setImageUrls(request.getImages());
        
        // Set property information if available
        if (request.getProperty() != null) {
            dto.setPropertyId(request.getProperty().getId());
            dto.setPropertyName(request.getProperty().getTitle());
        }
        
        // Set user/tenant information if available
        if (request.getUser() != null) {
            dto.setTenantId(request.getUser().getId());
            dto.setTenantName(request.getUser().getFirstName() + " " + request.getUser().getLastName());
        }
        
        return dto;
    }
    
    @Override
    public Page<MaintenanceRequestDTO> convertToDTO(Page<MaintenanceRequest> requests) {
        if (requests == null) {
            return Page.empty();
        }
        
        return requests.map(this::convertToDTO);
    }
    
    @Override
    public List<MaintenanceRequestDTO> convertToDTO(List<MaintenanceRequest> requests) {
        if (requests == null) {
            return Collections.emptyList();
        }
        
        return requests.stream()
                .map(this::convertToDTO)
                .collect(java.util.stream.Collectors.toList());
    }
} 