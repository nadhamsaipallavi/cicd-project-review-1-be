package com.propertymanagement.mapper;

import com.propertymanagement.dto.PropertyPurchaseRequestDTO;
import com.propertymanagement.mapper.base.impl.BaseMapperImpl;
import com.propertymanagement.model.PropertyPurchaseRequest;
import org.springframework.stereotype.Component;

@Component
public class PropertyPurchaseRequestMapper extends BaseMapperImpl<PropertyPurchaseRequest, PropertyPurchaseRequestDTO> {
    
    @Override
    protected PropertyPurchaseRequestDTO createDTO() {
        return new PropertyPurchaseRequestDTO();
    }
    
    @Override
    protected PropertyPurchaseRequest createEntity() {
        return new PropertyPurchaseRequest();
    }
    
    @Override
    public PropertyPurchaseRequestDTO toDTO(PropertyPurchaseRequest entity) {
        PropertyPurchaseRequestDTO dto = super.toDTO(entity);
        if (dto != null) {
            if (entity.getProperty() != null) {
                dto.setPropertyId(entity.getProperty().getId());
                dto.setPropertyTitle(entity.getProperty().getTitle());
            }
            if (entity.getTenant() != null) {
                dto.setTenantId(entity.getTenant().getId());
                dto.setTenantName(entity.getTenant().getFirstName() + " " + entity.getTenant().getLastName());
            }
            if (entity.getLandlord() != null) {
                dto.setLandlordId(entity.getLandlord().getId());
                dto.setLandlordName(entity.getLandlord().getFirstName() + " " + entity.getLandlord().getLastName());
            }
        }
        return dto;
    }
    
    @Override
    public PropertyPurchaseRequest toEntity(PropertyPurchaseRequestDTO dto) {
        PropertyPurchaseRequest entity = super.toEntity(dto);
        if (entity != null) {
            // Additional mapping logic can be added here if needed
        }
        return entity;
    }
} 