package com.organization.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * ユーザーエンティティ.
 * 
 * <p>ユーザー情報を管理するエンティティクラスです。
 * Spring SecurityのUserDetailsインターフェースを実装し、認証・認可機能を提供します。
 * 
 * <p>Organization Standards準拠:
 * <ul>
 *   <li>BaseEntityを継承（監査フィールド自動管理）</li>
 *   <li>Lombokによるボイラープレートコード削減</li>
 *   <li>JPA管理下のエンティティ</li>
 * </ul>
 * 
 * @author Organization Development Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String name;

    /**
     * パスワード（BCryptでハッシュ化されたもの）.
     * 
     * <p>平文パスワードは保存しないこと。
     * 必ずBCryptPasswordEncoderでエンコードしてから保存してください。
     */
    @Column(nullable = false)
    private String password;

    /**
     * ユーザーロール.
     * 
     * <p>ROLE_USER: 一般ユーザー
     * <p>ROLE_ADMIN: 管理者
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    /**
     * ユーザーロール列挙型.
     */
    public enum Role {
        ROLE_USER,
        ROLE_ADMIN
    }

    // ===== UserDetails実装メソッド =====

    /**
     * ユーザーの権限を返却.
     * 
     * @return 権限のコレクション
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    /**
     * ユーザー名（ログインID）を返却.
     * 
     * <p>このシステムではemailをユーザー名として使用します。
     * 
     * @return ユーザー名（email）
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * アカウントの有効期限チェック.
     * 
     * <p>このシステムでは常にtrueを返却（有効期限管理なし）。
     * 
     * @return 常にtrue
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * アカウントのロック状態チェック.
     * 
     * <p>このシステムでは常にtrueを返却（ロック機能なし）。
     * 
     * @return 常にtrue
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 認証情報の有効期限チェック.
     * 
     * <p>このシステムでは常にtrueを返却（有効期限管理なし）。
     * 
     * @return 常にtrue
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * アカウントの有効/無効状態チェック.
     * 
     * @return enabledフィールドの値
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
