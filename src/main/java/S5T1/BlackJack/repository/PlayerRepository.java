package S5T1.BlackJack.repository;

import S5T1.BlackJack.entity.Player;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

@Repository
public interface PlayerRepository extends ReactiveMongoRepository<Player, String> {
    Mono<Player> findByName(String name);
}