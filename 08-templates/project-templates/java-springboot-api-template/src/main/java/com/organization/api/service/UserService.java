package com.organization.api.service;

import com.organization.api.dto.UserDto;
import com.organization.api.entity.User;
import com.organization.api.exception.ResourceNotFoundException;
import com.organization.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ユーザーサービス.
 * 
 * <p>ユーザー関連のビジネスロジックを実装します。
 * Organization Standards準拠:
 * <ul>
 *   <li>コンストラクタインジェクション使用</li>
 *   <li>トランザクション管理</li>
 *   <li>単一責任原則（SRP）</li>
 *   <li>DTO/Entity変換</li>
 * </ul>
 * 
 * @author Organization Development Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 全ユーザーを取得.
     * 
     * @return ユーザーDTOのリスト
     */
    public List<UserDto> findAllUsers() {
        log.debug("全ユーザー取得開始");
        List<User> users = userRepository.findAll();
        log.info("全ユーザー取得完了: {}件", users.size());
        
        return users.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * IDでユーザーを取得.
     * 
     * @param id ユーザーID
     * @return ユーザーDTO
     * @throws ResourceNotFoundException ユーザーが見つからない場合
     */
    public UserDto findUserById(Long id) {
        log.debug("ユーザー取得開始: id={}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        log.info("ユーザー取得完了: email={}", user.getEmail());
        return convertToDto(user);
    }

    /**
     * ユーザーを作成.
     * 
     * <p>パスワードはBCryptでハッシュ化して保存されます。
     * 
     * @param userDto ユーザー情報
     * @return 作成されたユーザーDTO
     * @throws IllegalArgumentException メールアドレスが既に登録されている場合
     */
    @Transactional
    public UserDto createUser(UserDto userDto, String rawPassword) {
        log.debug("ユーザー作成開始: email={}", userDto.getEmail());

        // メールアドレス重複チェック
        if (userRepository.existsByEmail(userDto.getEmail())) {
            log.warn("ユーザー作成失敗: メールアドレスが既に登録されています email={}", 
                    userDto.getEmail());
            throw new IllegalArgumentException(
                "メールアドレス " + userDto.getEmail() + " は既に登録されています");
        }

        // BCryptでパスワードをハッシュ化（organization-standards準拠）
        String hashedPassword = passwordEncoder.encode(rawPassword);

        User user = User.builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .password(hashedPassword)
                .role(userDto.getRole() != null ? userDto.getRole() : User.Role.ROLE_USER)
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);
        log.info("ユーザー作成完了: id={}, email={}", savedUser.getId(), savedUser.getEmail());

        return convertToDto(savedUser);
    }

    /**
     * ユーザーを更新.
     * 
     * @param id ユーザーID
     * @param userDto 更新情報
     * @return 更新されたユーザーDTO
     * @throws ResourceNotFoundException ユーザーが見つからない場合
     */
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        log.debug("ユーザー更新開始: id={}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        // 更新可能なフィールドのみ更新（パスワード除く）
        user.setName(userDto.getName());
        
        if (userDto.getEnabled() != null) {
            user.setEnabled(userDto.getEnabled());
        }

        User updatedUser = userRepository.save(user);
        log.info("ユーザー更新完了: id={}, email={}", updatedUser.getId(), 
                updatedUser.getEmail());

        return convertToDto(updatedUser);
    }

    /**
     * ユーザーを削除.
     * 
     * @param id ユーザーID
     * @throws ResourceNotFoundException ユーザーが見つからない場合
     */
    @Transactional
    public void deleteUser(Long id) {
        log.debug("ユーザー削除開始: id={}", id);

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }

        userRepository.deleteById(id);
        log.info("ユーザー削除完了: id={}", id);
    }

    /**
     * Spring Security用: ユーザー名（email）でユーザーを取得.
     * 
     * @param username ユーザー名（email）
     * @return UserDetails実装オブジェクト
     * @throws UsernameNotFoundException ユーザーが見つからない場合
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("認証用ユーザー取得: username={}", username);
        
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                    "ユーザーが見つかりません: " + username));
    }

    /**
     * EntityをDTOに変換（パスワードは含めない）.
     * 
     * <p>Organization Standards準拠: EntityをAPIレスポンスとして直接返さない。
     * 
     * @param user ユーザーエンティティ
     * @return ユーザーDTO
     */
    private UserDto convertToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .enabled(user.getEnabled())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
