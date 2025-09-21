package com.propertymanagement.mapper.base;

import com.propertymanagement.dto.base.BaseDTO;
import com.propertymanagement.model.base.BaseEntity;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public interface BaseMapper<E extends BaseEntity, D extends BaseDTO> {
    
    D toDTO(E entity);
    
    E toEntity(D dto);
    
    default List<D> toDTOList(List<E> entities) {
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    default List<E> toEntityList(List<D> dtos) {
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
    
    default Page<D> toDTOPage(Page<E> entityPage) {
        return entityPage.map(this::toDTO);
    }
} 