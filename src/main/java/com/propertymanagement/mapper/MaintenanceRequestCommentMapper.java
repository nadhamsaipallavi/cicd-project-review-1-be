package com.propertymanagement.mapper;

import com.propertymanagement.dto.MaintenanceRequestCommentDTO;
import com.propertymanagement.model.MaintenanceRequestComment;
import com.propertymanagement.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MaintenanceRequestCommentMapper {

    @Autowired
    private UserMapper userMapper;

    /**
     * Convert a MaintenanceRequestComment entity to MaintenanceRequestCommentDTO
     */
    public MaintenanceRequestCommentDTO toDTO(MaintenanceRequestComment comment) {
        if (comment == null) {
            return null;
        }

        String userName = null;
        if (comment.getUser() != null) {
            User user = comment.getUser();
            userName = (user.getFirstName() != null ? user.getFirstName() : "") + 
                      " " + 
                      (user.getLastName() != null ? user.getLastName() : "");
            userName = userName.trim();
        }

        return MaintenanceRequestCommentDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .userId(comment.getUser() != null ? comment.getUser().getId() : null)
                .userName(userName)
                .userRole(comment.getUser() != null ? comment.getUser().getRole().name() : null)
                .createdAt(comment.getCreatedAt())
                .build();
    }

    /**
     * Convert a list of MaintenanceRequestComment entities to a list of MaintenanceRequestCommentDTOs
     */
    public List<MaintenanceRequestCommentDTO> toDTOList(List<MaintenanceRequestComment> comments) {
        if (comments == null) {
            return null;
        }

        return comments.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert a MaintenanceRequestCommentDTO to MaintenanceRequestComment entity
     */
    public MaintenanceRequestComment toEntity(MaintenanceRequestCommentDTO commentDTO) {
        if (commentDTO == null) {
            return null;
        }

        MaintenanceRequestComment comment = new MaintenanceRequestComment();
        comment.setId(commentDTO.getId());
        comment.setContent(commentDTO.getContent());
        // Note: The user and maintenanceRequest relationships should be 
        // set by the service layer, not directly by the mapper
        
        return comment;
    }
} 