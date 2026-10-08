package com.jiism.jiism.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

public class JwtUtil {

    // Clé secrète temporaire pour le développement
    private static final String SECRET =
            "MaCleSecreteSuperSecuriseePourJwt1234567890!";

    // Durée de validité du JWT : 1 jour
    private static final long EXPIRATION_TIME = 86400000;

    /**
     * Génère la clé utilisée pour signer et vérifier les JWT.
     */
    private static Key getSigningKey() {
        return Keys.hmacShaKeyFor(
                SECRET.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Génère un JWT contenant le username et les rôles.
     */
    public static String generateToken(
            String username,
            List<String> roles
    ) {

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + EXPIRATION_TIME
                        )
                )
                .signWith(
                        getSigningKey(),
                        SignatureAlgorithm.HS256
                )
                .compact();
    }

    /**
     * Extrait le username du JWT.
     */
    public static String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Extrait les rôles du JWT.
     */
    public static List<String> extractRoles(String token) {
        Claims claims = getClaims(token);

        return claims.get("roles", List.class);
    }

    /**
     * Vérifie que le JWT est valide
     * et qu'il appartient bien au username fourni.
     */
    public static boolean validateToken(
            String token,
            String username
    ) {

        try {

            String tokenUsername = extractUsername(token);

            return tokenUsername.equals(username)
                    && !isTokenExpired(token);

        } catch (Exception e) {

            return false;
        }
    }

    /**
     * Vérifie si le JWT est expiré.
     */
    private static boolean isTokenExpired(String token) {

        return getClaims(token)
                .getExpiration()
                .before(new Date());
    }

    /**
     * Parse et vérifie la signature du JWT.
     */
    private static Claims getClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}