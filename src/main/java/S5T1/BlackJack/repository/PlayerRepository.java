package S5T1.BlackJack.repository;

import S5T1.BlackJack.entity.Player;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

@Repository
public interface PlayerRepository extends ReactiveCrudRepository<Player, Long> {
    Mono<Player> findByName(String name);

    Mono<Player> findById(Long userId);

    //Mono<Player> deleteById(Long userId);
}