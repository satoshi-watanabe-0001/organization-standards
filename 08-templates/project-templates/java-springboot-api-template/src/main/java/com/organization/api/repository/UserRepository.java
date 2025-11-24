package com.organization.api.repository;

import com.organization.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ユーザーリポジトリ.
 * 
 * <p>ユーザーエンティティのデータアクセスを提供します。
 * Organization Standards準拠: Repositoryはデータアクセスのみを担当。
 * 
 * @author Organization Development Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * メールアドレスでユーザーを検索.
     * 
     * <p>ログイン認証時に使用されます。
     * 
     * @param email メールアドレス
     * @return ユーザー（存在しない場合はOptional.empty()）
     */
    Optional<User> findByEmail(String email);

    /**
     * メールアドレスの存在チェック.
     * 
     * <p>ユーザー登録時の重複チェックに使用されます。
     * 
     * @param email メールアドレス
     * @return 存在する場合true
     */
    boolean existsByEmail(String email);
}
