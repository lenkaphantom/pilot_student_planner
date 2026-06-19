package com.pilot.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${pilot.jwt.secret}")
    private String jwtSecret;

    @Value("${pilot.jwt.access-token-expiration-ms}")
    private long accessTokenExpirationMs;

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(
            java.util.Base64.getEncoder().encodeToString(jwtSecret.getBytes())
        );
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generise access token iz Authentication objekta (posle logina).
     */
    public String generateAccessToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return buildToken(userDetails.getUsername(), accessTokenExpirationMs);
    }

    /**
     * Generise access token direktno iz emaila (za refresh flow).
     */
    public String generateAccessTokenFromEmail(String email) {
        return buildToken(email, accessTokenExpirationMs);
    }

    private String buildToken(String subject, long expirationMs) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Izvlaci email (subject) iz tokena.
     */
    public String getEmailFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Validira token — vraca true ako je ispravan i nije istekao.
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (MalformedJwtException e) {
            log.warn("Neispravan JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("JWT token je istekao: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token nije podrzan: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string je prazan: {}", e.getMessage());
        }
        return false;
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
