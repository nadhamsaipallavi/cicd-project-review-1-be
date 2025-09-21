package com.propertymanagement.mapper;

import com.propertymanagement.dto.UserDTO;
import com.propertymanagement.mapper.base.impl.BaseMapperImpl;
import com.propertymanagement.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper extends BaseMapperImpl<User, UserDTO> {
    
    @Override
    protected UserDTO createDTO() {
        return new UserDTO();
    }
    
    @Override
    protected User createEntity() {
        return new User();
    }
    
    @Override
    public UserDTO toDTO(User entity) {
        UserDTO dto = super.toDTO(entity);
        if (dto != null) {
            // Add any additional mapping logic here if needed
        }
        return dto;
    }
    
    @Override
    public User toEntity(UserDTO dto) {
        User entity = super.toEntity(dto);
        if (entity != null) {
            // Add any additional mapping logic here if needed
        }
        return entity;
    }
} 