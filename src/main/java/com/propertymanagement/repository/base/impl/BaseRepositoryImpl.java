package com.propertymanagement.repository.base.impl;

import com.propertymanagement.model.base.BaseEntity;
import com.propertymanagement.repository.base.BaseRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import java.util.Optional;

public class BaseRepositoryImpl<T extends BaseEntity>
        extends SimpleJpaRepository<T, Long> implements BaseRepository<T> {

    @PersistenceContext
    private EntityManager entityManager;

    public BaseRepositoryImpl(JpaEntityInformation<T, Long> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public Page<T> findByDeletedFalse(Pageable pageable) {
        return findAll((root, query, cb) -> cb.equal(root.get("deleted"), false), pageable);
    }

    @Override
    public boolean existsByIdAndDeletedFalse(Long id) {
        Query query = entityManager.createQuery(
                "SELECT COUNT(e) FROM " + getDomainClass().getSimpleName() + " e WHERE e.id = :id AND e.deleted = false");
        query.setParameter("id", id);
        return ((Long) query.getSingleResult()) > 0;
    }

    @Override
    public Optional<T> findByIdAndDeletedFalse(Long id) {
        Query query = entityManager.createQuery(
                "SELECT e FROM " + getDomainClass().getSimpleName() + " e WHERE e.id = :id AND e.deleted = false");
        query.setParameter("id", id);
        return Optional.ofNullable((T) query.getSingleResult());
    }

    @Override
    public void softDelete(Long id) {
        Query query = entityManager.createQuery(
                "UPDATE " + getDomainClass().getSimpleName() + " e SET e.deleted = true WHERE e.id = :id");
        query.setParameter("id", id);
        query.executeUpdate();
    }
} 