package com.petconnect.backend.services;

import com.petconnect.backend.exceptions.ResourceNotFoundException;
import com.petconnect.backend.utils.ValidationUtils;
import org.slf4j.Logger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Base service class providing common CRUD operations.
 * Services can extend this class to inherit standard functionality.
 *
 * @param <T>  Entity type
 * @param <ID> ID type
 */
public abstract class BaseService<T, ID> {

    protected final Logger logger;
    protected final JpaRepository<T, ID> repository;
    protected final String entityName;

    protected BaseService(Logger logger, JpaRepository<T, ID> repository, String entityName) {
        this.logger = logger;
        this.repository = repository;
        this.entityName = entityName;
    }

    /**
     * Find entity by ID or throw ResourceNotFoundException.
     */
    public T findByIdOrThrow(ID id) {
        ValidationUtils.requireNotNull(id, "ID");
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(entityName + " not found with id: " + id));
    }

    /**
     * Find entity by ID.
     */
    public Optional<T> findById(ID id) {
        ValidationUtils.requireNotNull(id, "ID");
        return repository.findById(id);
    }

    /**
     * Save entity.
     */
    @Transactional
    public T save(T entity) {
        ValidationUtils.requireNotNull(entity, entityName);
        logger.debug("Saving {}", entityName);
        return repository.save(entity);
    }

    /**
     * Save all entities.
     */
    @Transactional
    public List<T> saveAll(Iterable<T> entities) {
        ValidationUtils.requireNotNull(entities, "Entities");
        logger.debug("Saving multiple {}", entityName);
        return repository.saveAll(entities);
    }

    /**
     * Delete entity by ID.
     */
    @Transactional
    public void deleteById(ID id) {
        ValidationUtils.requireNotNull(id, "ID");
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(entityName + " not found with id: " + id);
        }
        logger.debug("Deleting {} with id: {}", entityName, id);
        repository.deleteById(id);
    }

    /**
     * Check if entity exists by ID.
     */
    public boolean existsById(ID id) {
        ValidationUtils.requireNotNull(id, "ID");
        return repository.existsById(id);
    }

    /**
     * Get all entities.
     */
    public List<T> findAll() {
        logger.debug("Finding all {}", entityName);
        return repository.findAll();
    }

    /**
     * Count all entities.
     */
    public long count() {
        return repository.count();
    }
}