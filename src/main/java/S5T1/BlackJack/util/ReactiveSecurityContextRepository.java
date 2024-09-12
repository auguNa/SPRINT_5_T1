package S5T1.BlackJack.util;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class ReactiveSecurityContextRepository implements ServerSecurityContextRepository {

    private final ReactiveJwtAuthenticationConverter jwtAuthenticationConverter;

    public ReactiveSecurityContextRepository(ReactiveJwtAuthenticationConverter jwtAuthenticationConverter) {
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    }

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty(); // Stateless; no need to store context
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        return jwtAuthenticationConverter.convert(exchange)
                .map(auth -> new SecurityContextImpl(auth));
    }
}