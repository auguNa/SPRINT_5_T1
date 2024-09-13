package S5T1.BlackJack.service;

import S5T1.BlackJack.entity.Player;
import S5T1.BlackJack.exception.GlobalExceptionHandler;
import S5T1.BlackJack.exception.PlayerNotFoundException;
import S5T1.BlackJack.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }
    public Mono<Player> createPlayerWithCustomId(Player player) {
        return playerRepository.findAll()
                .filter(p -> p.getId().startsWith("player"))
                .map(p -> Integer.parseInt(p.getId().substring(6)))  // Extract the numeric part of the ID
                .defaultIfEmpty(0)  // If no players exist, start from 0
                .collectList()
                .flatMap(existingIds -> {
                    int newIdNumber = existingIds.isEmpty() ? 1 : Collections.max(existingIds) + 1;
                    String newPlayerId = "player" + newIdNumber;
                    player.setId(newPlayerId);  // Set the custom ID
                    return playerRepository.save(player);
                });
    }


//    public Mono<Player> createPlayer(String name) {
//        Player player = new Player(name, 0, 0);
//        return playerRepository.save(player);
//    }
    public Mono<Player> findPlayerByName(String name) {
        return playerRepository.findByName(name);
    }
    public Mono<Player> changePlayerName(String playerId, String newName) {
        return playerRepository.findById(playerId)
                .switchIfEmpty(Mono.error(new PlayerNotFoundException("Player not found with ID: " + playerId)))
                .flatMap(player -> {
                    player.setName(newName);
                    return playerRepository.save(player);
                });
    }

    public Flux<Player> getPlayerRanking() {
        return playerRepository.findAll()
                .sort((p1, p2) -> Integer.compare(p2.getWins(), p1.getWins()));
    }
}
