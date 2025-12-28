package com.psychegame.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT工具类
 *
 * @author Psyche Game Team
 * @since 2025-12-28
 */
@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private long expiration;
    
    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;
    
    /**
     * 从令牌中获取用户名
     *
     * @param token JWT令牌
     * @return 用户名
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    /**
     * 从令牌中获取用户ID
     *
     * @param token JWT令牌
     * @return 用户ID
     */
    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return Long.parseLong(claims.get("userId").toString());
    }
    
    /**
     * 从令牌中获取过期时间
     *
     * @param token JWT令牌
     * @return 过期时间
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * 从令牌中获取指定的声明
     *
     * @param <T> 声明类型
     * @param token JWT令牌
     * @param claimsResolver 声明解析器
     * @return 声明值
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * 从令牌中获取所有声明
     *
     * @param token JWT令牌
     * @return 所有声明
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new RuntimeException("JWT令牌解析失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证令牌是否过期
     *
     * @param token JWT令牌
     * @return 是否过期
     */
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    /**
     * 生成访问令牌
     *
     * @param username 用户名
     * @param userId 用户ID
     * @param extraClaims 额外声明
     * @return JWT访问令牌
     */
    public String generateAccessToken(String username, Long userId, Map<String, Object> extraClaims) {
        return buildToken(extraClaims, username, userId, expiration);
    }
    
    /**
     * 生成刷新令牌
     *
     * @param username 用户名
     * @param userId 用户ID
     * @return JWT刷新令牌
     */
    public String generateRefreshToken(String username, Long userId) {
        return buildToken(Map.of(), username, userId, refreshExpiration);
    }
    
    /**
     * 构建JWT令牌
     *
     * @param extraClaims 额外声明
     * @param username 用户名
     * @param userId 用户ID
     * @param expiration 过期时间
     * @return JWT令牌
     */
    private String buildToken(
            Map<String, Object> extraClaims,
            String username,
            Long userId,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .setId(userId.toString())
                .claim("userId", userId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * 验证令牌
     *
     * @param token JWT令牌
     * @param username 用户名
     * @return 验证结果
     */
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
    
    /**
     * 获取签名密钥
     *
     * @return 签名密钥
     */
    private javax.crypto.SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
