package S5T1.BlackJack.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.stream.Collectors;


import org.springframework.security.core.Authentication;

@Component
public class JWTTokenProvider {

    private final SecretKey secretKey;

    @Autowired
    public JWTTokenProvider(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    // Resolve token from reactive request (ServerWebExchange)
    public Mono<String> resolveToken(ServerWebExchange exchange) {
        String bearerToken = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return Mono.just(bearerToken.substring(7));
        }
        return Mono.empty(); // No token or improper format
    }

    // Validate the token
    public Mono<Boolean> validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return Mono.just(true); // Token is valid
        } catch (Exception e) {
            return Mono.just(false); // Invalid token
        }
    }

    // Get authentication object from JWT token
    public Mono<Authentication> getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
        String username = claims.getSubject();
        List<String> roles = claims.get("roles", List.class);

        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // Create an Authentication object using UsernamePasswordAuthenticationToken
        return Mono.just(new UsernamePasswordAuthenticationToken(username, null, authorities));
    }
}