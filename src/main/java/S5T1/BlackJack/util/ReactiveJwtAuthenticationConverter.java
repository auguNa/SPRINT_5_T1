package S5T1.BlackJack.util;

import S5T1.BlackJack.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class ReactiveJwtAuthenticationConverter implements ServerAuthenticationConverter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Autowired
    public ReactiveJwtAuthenticationConverter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwt = authorizationHeader.substring(7);
            String username = jwtUtil.extractUsername(jwt);

            if (username != null) {
                return userDetailsService.findByUsername(username)
                        .flatMap(userDetails -> {
                            if (jwtUtil.validateToken(jwt, userDetails)) {
                                // Create an Authentication object and return it wrapped in a Mono
                                return Mono.just(new UsernamePasswordAuthenticationToken(
                                        userDetails, jwt, userDetails.getAuthorities()));
                            } else {
                                // Return an empty Mono if the token is invalid
                                return Mono.empty();
                            }
                        });
            }
        }

        // Return an empty Mono if no valid token is found
        return Mono.empty();
    }
}
