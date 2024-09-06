package S5T1.BlackJack.repository;

import S5T1.BlackJack.entity.Role;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface RoleRepository extends R2dbcRepository<Role, Long> {
    Mono<Role> findByName(String name);
    Mono<Boolean> existsByName(String name);
}
