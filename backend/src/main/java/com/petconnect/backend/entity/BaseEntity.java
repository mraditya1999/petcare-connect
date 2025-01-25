package com.petconnect.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * A base entity class to hold common fields for creation and update timestamps.
 * Inherit this class in other entities to eliminate redundancy.
 */
@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    /**
     * Automatically sets createdAt and updatedAt before persisting.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
    }

    /**
     * Automatically updates the updatedAt field before updating a record.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}