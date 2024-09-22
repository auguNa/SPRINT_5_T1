package S5T1.BlackJack.service;

import S5T1.BlackJack.entity.Player;
import S5T1.BlackJack.exception.PlayerNotFoundException;
import S5T1.BlackJack.repository.PlayerRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public Mono<Player> savePlayer(Player player) {
        return playerRepository.save(player);
    }

    @Override
    public Mono<Player> findByName(String name) {
        return playerRepository.findByName(name);
    }

    @Override
    public Mono<Player> updatePlayer(Long id, String newName) {
        return playerRepository.findById(id)
                .flatMap(player -> {
                    player.setName(newName);
                    return playerRepository.save(player);
                })
                .switchIfEmpty(Mono.error(new PlayerNotFoundException("Player not found with id: " + id)));
    }

    @Override
    public Flux<Player> getPlayerRanking() {
        return playerRepository.findAll()
                .sort((p1, p2) -> Integer.compare(p2.getWins(), p1.getWins()));
    }

    @Override
    public Mono<Player> getPlayerById(String playerId) {
        return playerRepository.findById(Long.valueOf(playerId))
                .switchIfEmpty(Mono.error(new PlayerNotFoundException("Player not found with id: " + playerId)));
    }

    @Override
    public Flux<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    @Override
    public Mono<Player> createOrUpdatePlayer(Player player) {
        return playerRepository.save(player);
    }

    @Override
    public Mono<Void> deletePlayer(Long id) {
        return playerRepository.deleteById(id);
    }
}
