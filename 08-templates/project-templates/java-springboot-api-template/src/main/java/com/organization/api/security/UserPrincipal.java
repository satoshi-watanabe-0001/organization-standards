package com.organization.api.security;

import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Spring Security認証用のユーザープリンシパル.
 * 
 * <p>このクラスは、Spring Securityの{@link UserDetails}インターフェースを実装し、
 * 認証・認可に必要なユーザー情報を保持します。</p>
 * 
 * <p><strong>WHY原則:</strong> Spring Securityとの統合のため、
 * UserDetailsインターフェースを実装します。カスタムユーザー情報
 * （ID、メール、ロール）を保持し、認証後のリクエスト処理で利用します。</p>
 * 
 * @see UserDetails
 */
@Getter
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * ユーザープリンシパルを生成 (ロール指定).
     * 
     * @param id ユーザーID
     * @param email メールアドレス
     * @param password パスワード (BCryptハッシュ化済み)
     * @param roles ユーザーロール (例: "ROLE_USER", "ROLE_ADMIN")
     * @return UserPrincipal インスタンス
     */
    public static UserPrincipal create(Long id, String email, String password, 
                                       List<String> roles) {
        List<GrantedAuthority> authorities = roles.stream()
            .map(SimpleGrantedAuthority::new)
            .map(auth -> (GrantedAuthority) auth)
            .toList();

        return new UserPrincipal(id, email, password, authorities);
    }

    @Override
    public String getUsername() {
        // メールアドレスをユーザー名として使用
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
