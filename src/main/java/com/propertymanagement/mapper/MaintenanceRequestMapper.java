package com.propertymanagement.mapper;

import com.propertymanagement.dto.MaintenanceRequestDTO;
import com.propertymanagement.model.MaintenanceRequest;
import com.propertymanagement.model.MaintenanceRequestPriority;
import com.propertymanagement.model.MaintenanceRequestStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MaintenanceRequestMapper {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PropertyMapper propertyMapper;

    /**
     * Convert a MaintenanceRequest entity to MaintenanceRequestDTO
     */
    public MaintenanceRequestDTO toDTO(MaintenanceRequest request) {
        if (request == null) {
            return null;
        }

        return MaintenanceRequestDTO.builder()
                .id(request.getId())
                .title(request.getTitle())
                .description(request.getDescription())
                .propertyId(request.getProperty() != null ? request.getProperty().getId() : null)
                .property(request.getProperty() != null ? propertyMapper.toDTO(request.getProperty()) : null)
                .tenant(request.getUser() != null ? userMapper.toDTO(request.getUser()) : null)
                .priority(request.getPriority() != null ? request.getPriority().toString() : null)
                .status(request.getStatus() != null ? request.getStatus().toString() : null)
                .imageUrls(request.getImages())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .resolvedAt(request.getResolvedAt())
                .resolutionNotes(request.getResolution())
                .build();
    }

    /**
     * Convert a list of MaintenanceRequest entities to a list of MaintenanceRequestDTOs
     */
    public List<MaintenanceRequestDTO> toDTOList(List<MaintenanceRequest> requests) {
        if (requests == null) {
            return null;
        }

        return requests.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert a MaintenanceRequestDTO to MaintenanceRequest entity
     */
    public MaintenanceRequest toEntity(MaintenanceRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        MaintenanceRequest request = new MaintenanceRequest();
        request.setId(requestDTO.getId());
        request.setTitle(requestDTO.getTitle());
        request.setDescription(requestDTO.getDescription());
        
        // Set priority and status based on string values
        if (requestDTO.getPriority() != null) {
            try {
                request.setPriority(MaintenanceRequestPriority.valueOf(requestDTO.getPriority()));
            } catch (IllegalArgumentException e) {
                // Set default priority if string doesn't match enum
                request.setPriority(MaintenanceRequestPriority.MEDIUM);
            }
        }
        
        if (requestDTO.getStatus() != null) {
            try {
                request.setStatus(MaintenanceRequestStatus.valueOf(requestDTO.getStatus()));
            } catch (IllegalArgumentException e) {
                // Set default status if string doesn't match enum
                request.setStatus(MaintenanceRequestStatus.PENDING);
            }
        }
        
        request.setImages(requestDTO.getImageUrls());
        request.setResolvedAt(requestDTO.getResolvedAt());
        request.setResolution(requestDTO.getResolutionNotes());
        
        // Note: The property and user relationships should be 
        // set by the service layer, not directly by the mapper
        
        return request;
    }

    /**
     * Update an existing MaintenanceRequest entity from DTO
     */
    public void updateEntityFromDTO(MaintenanceRequestDTO dto, MaintenanceRequest entity) {
        if (dto == null || entity == null) {
            return;
        }
        
        // Only update fields that should be updateable
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        
        // Set priority and status based on string values
        if (dto.getPriority() != null) {
            try {
                entity.setPriority(MaintenanceRequestPriority.valueOf(dto.getPriority()));
            } catch (IllegalArgumentException e) {
                // Keep existing priority if string doesn't match enum
            }
        }
        
        if (dto.getStatus() != null) {
            try {
                entity.setStatus(MaintenanceRequestStatus.valueOf(dto.getStatus()));
            } catch (IllegalArgumentException e) {
                // Keep existing status if string doesn't match enum
            }
        }
        
        entity.setResolution(dto.getResolutionNotes());
        
        // The following fields should generally not be updated directly:
        // - ID
        // - property relationship
        // - user relationship
        // - createdAt
        // - images (managed separately)
    }
} 