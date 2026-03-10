package com.organization.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Base Entity class with audit fields.
 * 
 * <p>All entities should extend this class to inherit common audit fields:
 * <ul>
 *   <li>createdAt - Timestamp when entity was created</li>
 *   <li>updatedAt - Timestamp when entity was last updated</li>
 * </ul>
 * 
 * <p>Follows organization standards:
 * <ul>
 *   <li>/01-coding-standards/java/03-class-design-architecture.md</li>
 *   <li>Single Responsibility Principle</li>
 *   <li>DRY (Don't Repeat Yourself)</li>
 * </ul>
 * 
 * @author Organization Engineering Team
 * @version 1.0.0
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity {

    /**
     * Timestamp when the entity was created.
     * Automatically set by JPA Auditing.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the entity was last updated.
     * Automatically updated by JPA Auditing.
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
