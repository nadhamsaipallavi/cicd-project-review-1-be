package com.propertymanagement.mapper;

import com.propertymanagement.dto.LeaseDTO;
import com.propertymanagement.mapper.base.impl.BaseMapperImpl;
import com.propertymanagement.model.Lease;
import org.springframework.stereotype.Component;

@Component
public class LeaseMapper extends BaseMapperImpl<Lease, LeaseDTO> {
    
    @Override
    protected LeaseDTO createDTO() {
        return new LeaseDTO();
    }
    
    @Override
    protected Lease createEntity() {
        return new Lease();
    }
    
    @Override
    public LeaseDTO toDTO(Lease entity) {
        LeaseDTO dto = super.toDTO(entity);
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
    public Lease toEntity(LeaseDTO dto) {
        Lease entity = super.toEntity(dto);
        if (entity != null) {
            // Additional mapping logic can be added here if needed
        }
        return entity;
    }
} 