package S5T1.BlackJack.service;

import S5T1.BlackJack.entity.Player;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PlayerService {

    Mono<Player> savePlayer(Player player);

    Mono<Player> findByName(String name);

    Mono<Player> updatePlayer(Long id, String newName);

    Flux<Player> getPlayerRanking();

    Mono<Player> getPlayerById(String playerId);

    Flux<Player> getAllPlayers();

    Mono<Player> createOrUpdatePlayer(Player player);

    Mono<Void> deletePlayer(Long id);
}
