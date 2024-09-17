package S5T1.BlackJack.repository;

import S5T1.BlackJack.entity.Game;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface GameRepository extends ReactiveMongoRepository<Game, String> {
    Mono<Game> findById(String id);
}