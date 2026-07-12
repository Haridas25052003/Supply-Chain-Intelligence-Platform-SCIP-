package accel4.demo.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JwtUtil - JWT Token Generation, Validation, and Extraction
 * 
 * Handles:
 * - Access token generation and validation
 * - Refresh token generation and validation
 * - Claims extraction
 * - Token expiration handling
 * 
 * @author SCIP Team
 * @version 1.0
 */
@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret:mySecretKeyForSCIPPlatformWhichIsAtLeast32CharactersLongEnoughForHS256Algorithm}")
    private String secret;

    @Value("${jwt.access-token-expiration:3600000}")  // 1 hour in ms
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration:604800000}") // 7 days in ms
    private Long refreshTokenExpiration;

    @Value("${jwt.issuer:SCIP-Backend}")
    private String issuer;

    /**
     * Generate Access Token
     * 
     * @param email User email (subject)
     * @return JWT access token
     */
    public String generateAccessToken(String email) {
        return buildToken(email, accessTokenExpiration, "access");
    }

    /**
     * Generate Refresh Token
     * 
     * @param email User email (subject)
     * @return JWT refresh token
     */
    public String generateRefreshToken(String email) {
        return buildToken(email, refreshTokenExpiration, "refresh");
    }

    /**
     * Build JWT Token with claims
     * 
     * @param email User email
     * @param expiration Token expiration time in ms
     * @param tokenType Token type (access/refresh)
     * @return JWT token
     */
    private String buildToken(String email, Long expiration, String tokenType) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("tokenType", tokenType);

        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .issuer(issuer)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Validate JWT Token
     * 
     * @param token JWT token to validate
     * @return true if token is valid, false otherwise
     */
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
        }

        return false;
    }

    /**
     * Validate Access Token specifically
     * 
     * @param token JWT token to validate
     * @return true if token is valid access token
     */
    public boolean isAccessTokenValid(String token) {
        if (!isTokenValid(token)) {
            return false;
        }
        
        Claims claims = getAllClaims(token);
        String tokenType = (String) claims.get("tokenType");
        return "access".equals(tokenType);
    }

    /**
     * Validate Refresh Token specifically
     * 
     * @param token JWT token to validate
     * @return true if token is valid refresh token
     */
    public boolean isRefreshTokenValid(String token) {
        if (!isTokenValid(token)) {
            return false;
        }
        
        Claims claims = getAllClaims(token);
        String tokenType = (String) claims.get("tokenType");
        return "refresh".equals(tokenType);
    }

    /**
     * Extract email (subject) from token
     * 
     * @param token JWT token
     * @return Email from token subject
     */
    public String extractEmail(String token) {
        return getAllClaims(token).getSubject();
    }

    /**
     * Extract all claims from token
     * 
     * @param token JWT token
     * @return Claims object containing all token data
     */
    public Claims extractAllClaims(String token) {
        return getAllClaims(token);
    }

    /**
     * Check if token is expired
     * 
     * @param token JWT token
     * @return true if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getAllClaims(token).getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException ex) {
            return true;
        }
    }

    /**
     * Get time remaining for token expiration
     * 
     * @param token JWT token
     * @return Time remaining in milliseconds
     */
    public long getTokenExpirationTime(String token) {
        try {
            Date expiration = getAllClaims(token).getExpiration();
            return expiration.getTime() - System.currentTimeMillis();
        } catch (Exception ex) {
            log.error("Error getting token expiration time: {}", ex.getMessage());
            return 0;
        }
    }

    /**
     * Get all claims from token (internal method)
     * 
     * @param token JWT token
     * @return Claims object
     */
    private Claims getAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Generate signing key from secret
     * 
     * @return SecretKey for signing JWT
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Extract Bearer token from Authorization header
     * 
     * @param authHeader Authorization header value
     * @return Token without "Bearer " prefix, or null if invalid
     */
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * Debug method to print token info (USE ONLY IN DEV)
     * 
     * @param token JWT token
     */
    public void debugToken(String token) {
        try {
            Claims claims = getAllClaims(token);
            log.debug("========== JWT Token Debug ==========");
            log.debug("Subject (Email): {}", claims.getSubject());
            log.debug("Issued At: {}", claims.getIssuedAt());
            log.debug("Expiration: {}", claims.getExpiration());
            log.debug("Issuer: {}", claims.getIssuer());
            log.debug("Token Type: {}", claims.get("tokenType"));
            log.debug("All Claims: {}", claims);
            log.debug("====================================");
        } catch (Exception ex) {
            log.error("Error parsing token: {}", ex.getMessage());
        }
    }
}