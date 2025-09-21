package com.propertymanagement.service;

import com.propertymanagement.dto.MaintenanceRequestCommentDTO;
import com.propertymanagement.dto.MaintenanceRequestDTO;
import com.propertymanagement.model.MaintenanceRequest;
import com.propertymanagement.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MaintenanceRequestService {
    
    // Find methods
    Page<MaintenanceRequest> findAll(Pageable pageable);
    
    MaintenanceRequest findById(Long id);
    
    Page<MaintenanceRequest> findByPropertyId(Long propertyId, User currentUser, Pageable pageable);
    
    Page<MaintenanceRequest> findByLandlord(User landlord, Pageable pageable);
    
    Page<MaintenanceRequest> findByTenant(User tenant, Pageable pageable);
    
    List<MaintenanceRequest> findRecentRequests(int limit);
    
    List<MaintenanceRequest> findRecentRequestsByLandlord(User landlord, int limit);
    
    // Access control
    boolean canAccess(MaintenanceRequest request, User user);
    
    boolean canModify(MaintenanceRequest request, User user);
    
    // Maintenance request operations
    MaintenanceRequest createRequest(MaintenanceRequestDTO requestDTO, List<MultipartFile> images, User tenant);
    
    MaintenanceRequest updateRequest(Long id, MaintenanceRequestDTO requestDTO, List<MultipartFile> images, User user);
    
    MaintenanceRequest updateStatus(Long id, String status, User user);
    
    MaintenanceRequest resolveRequest(Long id, String resolutionNotes, User user);
    
    // Comments
    MaintenanceRequestCommentDTO addComment(Long requestId, String content, User user);
    
    List<MaintenanceRequestCommentDTO> getComments(Long requestId, User user);
    
    // Statistics
    int countPendingRequestsByLandlord(User landlord);
    
    int countPendingRequestsByTenant(User tenant);
    
    // For dashboard
    long countActiveRequestsByLandlord(User landlord);
    
    // DTO conversion
    MaintenanceRequestDTO convertToDTO(MaintenanceRequest request);
    
    Page<MaintenanceRequestDTO> convertToDTO(Page<MaintenanceRequest> requests);
    
    List<MaintenanceRequestDTO> convertToDTO(List<MaintenanceRequest> requests);
} 