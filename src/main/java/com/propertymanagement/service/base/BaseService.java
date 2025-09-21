package com.propertymanagement.service.base;

import com.propertymanagement.model.base.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BaseService<T extends BaseEntity> {
    
    T save(T entity);
    
    T update(Long id, T entity);
    
    T findById(Long id);
    
    Page<T> findAll(Pageable pageable);
    
    void delete(Long id);
    
    void softDelete(Long id);
    
    boolean existsById(Long id);
} 