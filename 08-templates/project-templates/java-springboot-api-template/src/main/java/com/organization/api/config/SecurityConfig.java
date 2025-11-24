package com.organization.api.config;

import com.organization.api.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security設定クラス.
 * 
 * <p>Organization Standards準拠のセキュリティ設定を提供します。
 * 参照: /organization-standards/07-security-compliance/authentication-authorization.md
 * 
 * <p>主な機能:
 * <ul>
 *   <li>JWT認証の実装（アクセストークン15分、リフレッシュトークン7日）</li>
 *   <li>BCryptパスワードエンコーディング</li>
 *   <li>CORS設定</li>
 *   <li>CSRF保護（APIモードは無効化）</li>
 *   <li>ステートレスセッション管理</li>
 * </ul>
 * 
 * @author Organization Development Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // メソッドレベルのセキュリティアノテーション有効化 (@PreAuthorize等)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    /**
     * セキュリティフィルターチェーン設定.
     * 
     * <p>HTTPセキュリティ設定を定義します。
     * JWT認証を使用するため、ステートレスなセッション管理を採用しています。
     * 
     * @param http HttpSecurity設定オブジェクト
     * @return 設定されたSecurityFilterChain
     * @throws Exception 設定エラー時
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF保護を無効化（JWT認証ではCSRFトークン不要）
            .csrf(AbstractHttpConfigurer::disable)
            
            // CORS設定を適用
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // エンドポイント認証設定
            .authorizeHttpRequests(auth -> auth
                // パブリックエンドポイント（認証不要）
                .requestMatchers(
                    "/api/v1/auth/**",      // 認証API
                    "/api/v1/public/**",    // 公開API
                    "/actuator/health",     // ヘルスチェック
                    "/actuator/info",       // アプリケーション情報
                    "/h2-console/**",       // H2コンソール（開発環境のみ）
                    "/error"                // エラーページ
                ).permitAll()
                
                // 管理者専用エンドポイント
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                
                // その他すべてのエンドポイントは認証必須
                .anyRequest().authenticated()
            )
            
            // セッション管理: ステートレス（JWTトークンベース認証）
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // 認証プロバイダー設定
            .authenticationProvider(authenticationProvider())
            
            // JWT認証フィルターを追加（UsernamePasswordAuthenticationFilterの前に配置）
            .addFilterBefore(
                jwtAuthenticationFilter, 
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    /**
     * CORS設定ソース.
     * 
     * <p>本番環境では、許可するオリジンを厳格に制限してください。
     * 現在の設定は開発環境向けです。
     * 
     * @return CORS設定ソース
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 本番環境では環境変数から取得し、特定のドメインのみ許可すること
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",  // 開発環境: Reactフロントエンド
            "http://localhost:4200"   // 開発環境: Angularフロントエンド
        ));
        
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        
        configuration.setAllowedHeaders(List.of("*"));
        
        // 認証情報（Cookie、Authorization Header）の送信を許可
        configuration.setAllowCredentials(true);
        
        // プリフライトリクエストのキャッシュ時間: 1時間
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }

    /**
     * 認証プロバイダー設定.
     * 
     * <p>UserDetailsServiceとPasswordEncoderを使用した
     * DAO認証プロバイダーを提供します。
     * 
     * @return 設定された認証プロバイダー
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * 認証マネージャー設定.
     * 
     * <p>Spring Securityの認証マネージャーを提供します。
     * ログイン処理で使用されます。
     * 
     * @param config 認証設定
     * @return 認証マネージャー
     * @throws Exception 設定エラー時
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * パスワードエンコーダー設定.
     * 
     * <p>BCryptアルゴリズムを使用したパスワードハッシュ化を提供します。
     * Organization Standards準拠: BCrypt強度10（デフォルト）
     * 
     * @return BCryptパスワードエンコーダー
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt強度: 10（デフォルト、2^10 = 1024回のハッシュ化）
        return new BCryptPasswordEncoder();
    }
}
