package com.propertymanagement.service.base.impl;

import com.propertymanagement.exception.ResourceNotFoundException;
import com.propertymanagement.model.base.BaseEntity;
import com.propertymanagement.repository.base.BaseRepository;
import com.propertymanagement.service.base.BaseService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public abstract class BaseServiceImpl<T extends BaseEntity, R extends BaseRepository<T>> implements BaseService<T> {

    protected final R repository;

    protected BaseServiceImpl(R repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public T save(T entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public T update(Long id, T entity) {
        T existingEntity = findById(id);
        BeanUtils.copyProperties(entity, existingEntity, "id", "createdAt", "createdBy");
        return repository.save(existingEntity);
    }

    @Override
    public T findById(Long id) {
        Optional<T> entity = repository.findByIdAndDeletedFalse(id);
        return entity.orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + id));
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return repository.findByDeletedFalse(pageable);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsByIdAndDeletedFalse(id)) {
            throw new ResourceNotFoundException("Resource not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void softDelete(Long id) {
        if (!repository.existsByIdAndDeletedFalse(id)) {
            throw new ResourceNotFoundException("Resource not found with id: " + id);
        }
        repository.softDelete(id);
    }

    @Override
    public boolean existsById(Long id) {
        return repository.existsByIdAndDeletedFalse(id);
    }
} 