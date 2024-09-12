package S5T1.BlackJack.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

@Service

public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private final SecretKey secretKey;
    @Autowired
    public JwtUtil(SecretKey secretKey) {
        this.secretKey = secretKey;
    }
        // Method to extract username (subject) from the token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Method to extract expiration date from the token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Generic method to extract claims
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Method to parse and extract all claims from the token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey) // Use injected secret key
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Method to check if the token has expired
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Method to generate JWT token with roles as claims
    public String generateToken(UserDetails userDetails, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles); // Add roles to the token claims
        return createToken(claims, userDetails.getUsername());
    }

    // Helper method to create the JWT token
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                .signWith(secretKey, SignatureAlgorithm.HS256) // Use HS256 with the secret key
                .compact();
    }

    // Method to validate the token
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            logger.info("Extracted username: {}", username); // Debugging log
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            logger.error("Malformed JWT token: {}", e.getMessage(), e); // Log the error
        } catch (io.jsonwebtoken.SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage(), e); // Log the error
        } catch (Exception e) {
            logger.error("Token validation error: {}", e.getMessage(), e); // Log any other errors
        }
        return false;
    }

}
