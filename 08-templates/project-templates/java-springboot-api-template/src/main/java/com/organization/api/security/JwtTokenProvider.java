package com.organization.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWTトークンプロバイダー.
 * 
 * <p>JWT（JSON Web Token）の生成と検証を行います。
 * Organization Standards準拠のJWT実装を提供します。
 * 
 * <p>主な機能:
 * <ul>
 *   <li>アクセストークン生成（有効期限15分）</li>
 *   <li>リフレッシュトークン生成（有効期限7日）</li>
 *   <li>トークン検証と解析</li>
 *   <li>ユーザー情報の抽出</li>
 * </ul>
 * 
 * @author Organization Development Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    /**
     * トークンからユーザー名（サブジェクト）を抽出.
     * 
     * @param token JWTトークン
     * @return ユーザー名
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * トークンから特定のクレームを抽出.
     * 
     * @param <T> クレームの型
     * @param token JWTトークン
     * @param claimsResolver クレーム解析関数
     * @return 抽出されたクレーム
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * アクセストークンを生成.
     * 
     * <p>有効期限: 15分（organization-standards準拠）
     * 
     * @param userDetails ユーザー詳細情報
     * @return 生成されたアクセストークン
     */
    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, accessTokenExpiration);
    }

    /**
     * リフレッシュトークンを生成.
     * 
     * <p>有効期限: 7日（organization-standards準拠）
     * 
     * @param userDetails ユーザー詳細情報
     * @return 生成されたリフレッシュトークン
     */
    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, refreshTokenExpiration);
    }

    /**
     * カスタムクレーム付きトークンを生成.
     * 
     * @param extraClaims 追加のクレーム情報
     * @param userDetails ユーザー詳細情報
     * @param expiration 有効期限（ミリ秒）
     * @return 生成されたトークン
     */
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration) {
        
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * トークンの有効性を検証.
     * 
     * <p>以下の条件を満たす場合、トークンは有効と判定されます:
     * <ul>
     *   <li>トークンに含まれるユーザー名がUserDetailsと一致</li>
     *   <li>トークンが有効期限内</li>
     * </ul>
     * 
     * @param token JWTトークン
     * @param userDetails ユーザー詳細情報
     * @return トークンが有効な場合true
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * トークンの有効期限切れをチェック.
     * 
     * @param token JWTトークン
     * @return 有効期限切れの場合true
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * トークンから有効期限を抽出.
     * 
     * @param token JWTトークン
     * @return 有効期限日時
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * トークンからすべてのクレームを抽出.
     * 
     * @param token JWTトークン
     * @return すべてのクレーム
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 署名キーを取得.
     * 
     * <p>秘密鍵をBase64デコードしてHMAC-SHA256署名キーを生成します。
     * 本番環境では、最低256ビットの秘密鍵を環境変数から取得してください。
     * 
     * @return 署名キー
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
