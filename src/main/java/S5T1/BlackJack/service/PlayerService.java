package S5T1.BlackJack.service;

import S5T1.BlackJack.entity.Player;
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

    public Mono<Player> savePlayer(Player player) {
        return playerRepository.save(player);
    }


    public Mono<Player> findByName(String name) {
        return playerRepository.findByName(name);
    }

    public Mono<Player> updatePlayer(Long id, String newName) {
        return playerRepository.findById(id)
                .flatMap(player -> {
                    player.setName(newName);
                    return playerRepository.save(player);
                });
    }

    public Flux<Player> getPlayerRanking() {
        return playerRepository.findAll()
                .sort((p1, p2) -> Integer.compare(p2.getWins(), p1.getWins()));
    }

    public Mono<Player> getPlayerById(String playerId) {
        return playerRepository.findById(Long.valueOf(playerId));
    }

    public Flux<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    public Mono<Player> createOrUpdatePlayer(Player player) {
        return playerRepository.save(player);
    }

    public Mono<Void> deletePlayer(Long id) {
        return playerRepository.deleteById(id).then();
    }
}
