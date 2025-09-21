package com.propertymanagement.controller;

import com.propertymanagement.dto.MaintenanceRequestCommentDTO;
import com.propertymanagement.dto.MaintenanceRequestDTO;
import com.propertymanagement.mapper.MaintenanceRequestCommentMapper;
import com.propertymanagement.mapper.MaintenanceRequestMapper;
import com.propertymanagement.model.MaintenanceRequest;
import com.propertymanagement.model.User;
import com.propertymanagement.service.MaintenanceRequestService;
import com.propertymanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class MaintenanceRequestController {

    @Autowired
    private MaintenanceRequestService maintenanceRequestService;

    @Autowired
    private UserService userService;

    @Autowired
    private MaintenanceRequestMapper maintenanceRequestMapper;
    
    @Autowired
    private MaintenanceRequestCommentMapper commentMapper;

    // Get all maintenance requests (admin)
    @GetMapping("/maintenance-requests")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<MaintenanceRequestDTO> getAllMaintenanceRequests(Pageable pageable) {
        return maintenanceRequestService.findAll(pageable)
                .map(maintenanceRequestMapper::toDTO);
    }

    // Get maintenance requests by property (landlord)
    @GetMapping("/maintenance-requests/property/{propertyId}")
    @PreAuthorize("hasRole('LANDLORD')")
    public Page<MaintenanceRequestDTO> getMaintenanceRequestsByProperty(
            @PathVariable Long propertyId, Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        return maintenanceRequestService.findByPropertyId(propertyId, currentUser, pageable)
                .map(maintenanceRequestMapper::toDTO);
    }

    // Get maintenance requests for landlord
    @GetMapping("/landlord/maintenance-requests")
    @PreAuthorize("hasRole('LANDLORD')")
    public Page<MaintenanceRequestDTO> getLandlordMaintenanceRequests(Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        return maintenanceRequestService.findByLandlord(currentUser, pageable)
                .map(maintenanceRequestMapper::toDTO);
    }

    // Get maintenance requests for tenant
    @GetMapping("/tenant/maintenance-requests")
    @PreAuthorize("hasRole('TENANT')")
    public Page<MaintenanceRequestDTO> getTenantMaintenanceRequests(Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        return maintenanceRequestService.findByTenant(currentUser, pageable)
                .map(maintenanceRequestMapper::toDTO);
    }

    // Get maintenance request by ID
    @GetMapping("/maintenance-requests/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD', 'TENANT')")
    public ResponseEntity<MaintenanceRequestDTO> getMaintenanceRequestById(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        MaintenanceRequest request = maintenanceRequestService.findById(id);
        
        if (request == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Verify access rights
        if (!maintenanceRequestService.canAccess(request, currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        return ResponseEntity.ok(maintenanceRequestMapper.toDTO(request));
    }

    // Create a maintenance request (tenant)
    @PostMapping("/tenant/maintenance-requests")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<MaintenanceRequestDTO> createMaintenanceRequest(
            @RequestPart("request") MaintenanceRequestDTO requestDTO,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        User currentUser = userService.getCurrentUser();
        
        MaintenanceRequest request = maintenanceRequestService.createRequest(requestDTO, images, currentUser);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(maintenanceRequestMapper.toDTO(request));
    }

    // Update a maintenance request
    @PutMapping("/maintenance-requests/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD', 'TENANT')")
    public ResponseEntity<MaintenanceRequestDTO> updateMaintenanceRequest(
            @PathVariable Long id,
            @RequestPart("request") MaintenanceRequestDTO requestDTO,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        User currentUser = userService.getCurrentUser();
        
        MaintenanceRequest request = maintenanceRequestService.updateRequest(id, requestDTO, images, currentUser);
        
        return ResponseEntity.ok(maintenanceRequestMapper.toDTO(request));
    }

    // Update maintenance request status
    @PutMapping("/landlord/maintenance-requests/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<MaintenanceRequestDTO> updateMaintenanceRequestStatus(
            @PathVariable Long id, @RequestBody StatusUpdateDTO statusUpdate) {
        User currentUser = userService.getCurrentUser();
        
        MaintenanceRequest request = maintenanceRequestService.updateStatus(
                id, statusUpdate.getStatus(), currentUser);
        
        return ResponseEntity.ok(maintenanceRequestMapper.toDTO(request));
    }

    // Resolve a maintenance request
    @PatchMapping("/maintenance-requests/{id}/resolve")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<MaintenanceRequestDTO> resolveMaintenanceRequest(
            @PathVariable Long id, @RequestBody ResolutionDTO resolution) {
        User currentUser = userService.getCurrentUser();
        
        MaintenanceRequest request = maintenanceRequestService.resolveRequest(
                id, resolution.getResolutionNotes(), currentUser);
        
        return ResponseEntity.ok(maintenanceRequestMapper.toDTO(request));
    }

    // Add a comment to a maintenance request
    @PostMapping("/maintenance-requests/{requestId}/comments")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD', 'TENANT')")
    public ResponseEntity<MaintenanceRequestCommentDTO> addComment(
            @PathVariable Long requestId, @RequestBody CommentDTO commentDTO) {
        User currentUser = userService.getCurrentUser();
        
        MaintenanceRequestCommentDTO comment = maintenanceRequestService.addComment(
                requestId, commentDTO.getContent(), currentUser);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    // Get comments for a maintenance request
    @GetMapping("/maintenance-requests/{requestId}/comments")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD', 'TENANT')")
    public ResponseEntity<List<MaintenanceRequestCommentDTO>> getComments(@PathVariable Long requestId) {
        User currentUser = userService.getCurrentUser();
        
        List<MaintenanceRequestCommentDTO> comments = maintenanceRequestService.getComments(requestId, currentUser);
        
        return ResponseEntity.ok(comments);
    }

    // Get recent maintenance requests (for dashboard)
    @GetMapping("/maintenance-requests/recent")
    @PreAuthorize("hasAnyRole('ADMIN', 'LANDLORD')")
    public ResponseEntity<List<MaintenanceRequestDTO>> getRecentMaintenanceRequests(
            @RequestParam(defaultValue = "5") int limit) {
        User currentUser = userService.getCurrentUser();
        List<MaintenanceRequest> requests;
        
        if (userService.hasRole(currentUser, "ADMIN")) {
            requests = maintenanceRequestService.findRecentRequests(limit);
        } else {
            requests = maintenanceRequestService.findRecentRequestsByLandlord(currentUser, limit);
        }
        
        List<MaintenanceRequestDTO> requestDTOs = requests.stream()
                .map(maintenanceRequestMapper::toDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(requestDTOs);
    }
    
    // Nested DTOs for request handling
    static class StatusUpdateDTO {
        private String status;
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
    }
    
    static class ResolutionDTO {
        private String resolutionNotes;
        
        public String getResolutionNotes() {
            return resolutionNotes;
        }
        
        public void setResolutionNotes(String resolutionNotes) {
            this.resolutionNotes = resolutionNotes;
        }
    }
    
    static class CommentDTO {
        private String content;
        
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
    }
} 