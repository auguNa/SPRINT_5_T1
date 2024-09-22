package S5T1.BlackJack.service;

import S5T1.BlackJack.entity.Game;
import S5T1.BlackJack.entity.Player;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GameService {

    // --- MongoDB Interactions: Managing Blackjack Games ---
    Mono<Game> createNewGame(String playerName);

    Mono<Game> getGameById(String id);

    Mono<Game> makeMove(String id, String moveType);

    Mono<Void> deleteGame(String id);

    // --- Player Operations ---
    Mono<Player> getPlayerById(String playerId);

    Flux<Player> getAllPlayers();

    Flux<Player> getPlayerRanking();

    Mono<Player> createOrUpdatePlayer(Player player);

    Mono<Void> deletePlayer(Long playerId);
}