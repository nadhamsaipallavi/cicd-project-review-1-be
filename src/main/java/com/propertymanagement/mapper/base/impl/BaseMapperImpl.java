package com.propertymanagement.mapper.base.impl;

import com.propertymanagement.dto.base.BaseDTO;
import com.propertymanagement.mapper.base.BaseMapper;
import com.propertymanagement.model.base.BaseEntity;
import org.springframework.beans.BeanUtils;

public abstract class BaseMapperImpl<E extends BaseEntity, D extends BaseDTO> implements BaseMapper<E, D> {

    protected abstract D createDTO();
    
    protected abstract E createEntity();
    
    @Override
    public D toDTO(E entity) {
        if (entity == null) {
            return null;
        }
        
        D dto = createDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
    
    @Override
    public E toEntity(D dto) {
        if (dto == null) {
            return null;
        }
        
        E entity = createEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }
} 