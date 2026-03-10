package com.organization.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT認証フィルター.
 * 
 * <p>すべてのHTTPリクエストに対してJWTトークンを検証し、
 * 認証されたユーザー情報をSecurityContextに設定します。
 * 
 * <p>処理フロー:
 * <ol>
 *   <li>Authorizationヘッダーからトークンを抽出</li>
 *   <li>トークンからユーザー名を抽出</li>
 *   <li>UserDetailsServiceでユーザー情報を取得</li>
 *   <li>トークンの有効性を検証</li>
 *   <li>認証情報をSecurityContextに設定</li>
 * </ol>
 * 
 * @author Organization Development Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    /**
     * 各リクエストに対してJWT認証を実行.
     * 
     * <p>Authorizationヘッダーが存在しない、または不正な形式の場合は
     * フィルターチェーンを続行します（パブリックエンドポイント用）。
     * 
     * @param request HTTPリクエスト
     * @param response HTTPレスポンス
     * @param filterChain フィルターチェーン
     * @throws ServletException サーブレット例外
     * @throws IOException I/O例外
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        // Authorizationヘッダーを取得
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Authorizationヘッダーが存在しない、またはBearer形式でない場合はスキップ
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // "Bearer "プレフィックスを除去してトークンを抽出
        jwt = authHeader.substring(7);
        
        // トークンからユーザー名を抽出
        username = jwtTokenProvider.extractUsername(jwt);

        // ユーザー名が存在し、まだ認証されていない場合
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // UserDetailsServiceでユーザー情報を取得
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // トークンの有効性を検証
            if (jwtTokenProvider.isTokenValid(jwt, userDetails)) {
                // 認証トークンを作成
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,  // パスワードは含めない（既に認証済み）
                        userDetails.getAuthorities()
                    );
                
                // リクエスト詳細情報を設定
                authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );
                
                // SecurityContextに認証情報を設定
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 次のフィルターに処理を委譲
        filterChain.doFilter(request, response);
    }
}
