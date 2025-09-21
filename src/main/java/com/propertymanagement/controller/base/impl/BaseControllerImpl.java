package com.propertymanagement.controller.base.impl;

import com.propertymanagement.controller.base.BaseController;
import com.propertymanagement.dto.base.BaseDTO;
import com.propertymanagement.mapper.base.BaseMapper;
import com.propertymanagement.model.base.BaseEntity;
import com.propertymanagement.service.base.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public abstract class BaseControllerImpl<E extends BaseEntity, D extends BaseDTO, S extends BaseService<E>, M extends BaseMapper<E, D>>
        extends BaseController<E, S> {

    protected final M mapper;

    protected BaseControllerImpl(S service, M mapper) {
        super(service);
        this.mapper = mapper;
    }

    @Override
    @GetMapping
    public ResponseEntity<Page<E>> findAll(Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<E> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Override
    @PostMapping
    public ResponseEntity<E> create(@RequestBody E entity) {
        return ResponseEntity.ok(service.save(entity));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<E> update(@PathVariable Long id, @RequestBody E entity) {
        return ResponseEntity.ok(service.update(id, entity));
    }

    @GetMapping("/dto")
    public ResponseEntity<Page<D>> findAllDTO(Pageable pageable) {
        return ResponseEntity.ok(mapper.toDTOPage(service.findAll(pageable)));
    }

    @GetMapping("/{id}/dto")
    public ResponseEntity<D> findByIdDTO(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toDTO(service.findById(id)));
    }

    @PostMapping("/dto")
    public ResponseEntity<D> createDTO(@RequestBody D dto) {
        E entity = mapper.toEntity(dto);
        return ResponseEntity.ok(mapper.toDTO(service.save(entity)));
    }

    @PutMapping("/{id}/dto")
    public ResponseEntity<D> updateDTO(@PathVariable Long id, @RequestBody D dto) {
        E entity = mapper.toEntity(dto);
        return ResponseEntity.ok(mapper.toDTO(service.update(id, entity)));
    }
} 