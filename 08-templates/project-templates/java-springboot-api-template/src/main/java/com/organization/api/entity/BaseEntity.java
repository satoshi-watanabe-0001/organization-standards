package com.organization.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * エンティティ基底クラス.
 * 
 * <p>すべてのエンティティクラスが継承する基底クラスです。
 * JPA監査機能により、作成日時と更新日時を自動的に記録します。
 * 
 * <p>Organization Standards準拠:
 * <ul>
 *   <li>監査フィールドの自動管理</li>
 *   <li>タイムゾーン非依存のInstant型使用</li>
 *   <li>不変性の確保（setterは継承クラスのみ）</li>
 * </ul>
 * 
 * <p>使用例:
 * <pre>{@code
 * @Entity
 * public class User extends BaseEntity {
 *     // ユーザー固有のフィールド
 * }
 * }</pre>
 * 
 * @author Organization Development Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity {

    /**
     * 作成日時.
     * 
     * <p>エンティティが最初に永続化された日時を記録します。
     * JPA監査機能により自動的に設定されます。
     * 
     * <p>不変フィールドのため、更新不可に設定されています。
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * 更新日時.
     * 
     * <p>エンティティが最後に更新された日時を記録します。
     * JPA監査機能により自動的に更新されます。
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
