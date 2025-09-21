package com.propertymanagement.mapper;

import com.propertymanagement.dto.PropertyDTO;
import com.propertymanagement.mapper.base.impl.BaseMapperImpl;
import com.propertymanagement.model.Property;
import org.springframework.stereotype.Component;

@Component
public class PropertyMapper extends BaseMapperImpl<Property, PropertyDTO> {
    
    @Override
    protected PropertyDTO createDTO() {
        return new PropertyDTO();
    }
    
    @Override
    protected Property createEntity() {
        return new Property();
    }
    
    @Override
    public PropertyDTO toDTO(Property entity) {
        PropertyDTO dto = super.toDTO(entity);
        if (dto != null && entity.getLandlord() != null) {
            dto.setLandlordId(entity.getLandlord().getId());
            dto.setLandlordName(entity.getLandlord().getFirstName() + " " + entity.getLandlord().getLastName());
        }
        return dto;
    }
    
    @Override
    public Property toEntity(PropertyDTO dto) {
        Property entity = super.toEntity(dto);
        if (entity != null) {
            // Additional mapping logic can be added here if needed
        }
        return entity;
    }
} 