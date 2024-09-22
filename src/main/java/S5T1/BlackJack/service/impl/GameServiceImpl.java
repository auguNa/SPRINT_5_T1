package S5T1.BlackJack.service.impl;

import S5T1.BlackJack.entity.Deck;
import S5T1.BlackJack.entity.Game;
import S5T1.BlackJack.entity.Player;
import S5T1.BlackJack.entity.Card;
import S5T1.BlackJack.exception.CustomException;
import S5T1.BlackJack.exception.GameNotFoundException;
import S5T1.BlackJack.repository.GameRepository;
import S5T1.BlackJack.service.GameService;
import S5T1.BlackJack.service.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameServiceImpl implements GameService {
    private static final Logger log = LoggerFactory.getLogger(GameServiceImpl.class);

    private final GameRepository gameRepository;
    private final PlayerService playerService;

    @Autowired
    public GameServiceImpl(GameRepository gameRepository, PlayerService playerService) {
        this.gameRepository = gameRepository;
        this.playerService = playerService;
    }

    @Override
    public Mono<Game> createNewGame(String playerName) {
        return playerService.findByName(playerName)
                .switchIfEmpty(Mono.defer(() -> {
                    Player newPlayer = new Player();
                    newPlayer.setName(playerName);
                    newPlayer.setScore(0);
                    return playerService.savePlayer(newPlayer);
                }))
                .flatMap(player -> {
                    Game game = new Game();
                    Deck deck = new Deck();
                    List<Card> playerHand = new ArrayList<>();
                    List<Card> dealerHand = new ArrayList<>();

                    playerHand.add(deck.drawCard());
                    playerHand.add(deck.drawCard());
                    dealerHand.add(deck.drawCard());
                    dealerHand.add(deck.drawCard());

                    game.setPlayerHand(playerHand);
                    game.setDealerHand(dealerHand);
                    game.setStatus("IN_PROGRESS");
                    game.setDeck(deck);
                    game.setPlayerId(String.valueOf(player.getId()));

                    return gameRepository.save(game)
                            .onErrorMap(e -> new CustomException("Error while creating game", e));
                });
    }

    @Override
    public Mono<Game> getGameById(String id) {
        return gameRepository.findById(id)
                .switchIfEmpty(Mono.error(new GameNotFoundException("Game not found with ID: " + id)));
    }

    @Override
    public Mono<Game> makeMove(String id, String moveType) {
        return gameRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid game ID")))
                .flatMap(game -> {
                    if (!"IN_PROGRESS".equals(game.getStatus())) {
                        return Mono.error(new IllegalStateException("Game is already over"));
                    }

                    if ("hit".equalsIgnoreCase(moveType)) {
                        return handleHit(game);
                    } else if ("stand".equalsIgnoreCase(moveType)) {
                        return handleStand(game);
                    } else {
                        return Mono.error(new IllegalArgumentException("Invalid move type"));
                    }
                })
                .onErrorResume(e -> Mono.error(new CustomException("An error occurred while making a move", e)));
    }

    @Override
    public Mono<Void> deleteGame(String id) {
        return gameRepository.findById(id)
                .switchIfEmpty(Mono.error(new GameNotFoundException("Game with ID " + id + " not found.")))
                .flatMap(existingGame -> gameRepository.deleteById(id));
    }

    private Mono<Game> handleHit(Game game) {
        log.debug("Handling hit for game ID: {}", game.getId());
        Deck deck = game.getDeck();

        if (deck == null) {
            return Mono.error(new IllegalStateException("Deck not initialized"));
        }

        game.getPlayerHand().add(deck.drawCard());

        int playerScore = calculateScore(game.getPlayerHand());
        if (playerScore > 21) {
            game.setStatus("DEALER_WON");
        } else if (playerScore == 21) {
            game.setStatus("PLAYER_WON");
        }

        return gameRepository.save(game);
    }

    private Mono<Game> handleStand(Game game) {
        Deck deck = game.getDeck();
        int dealerScore = calculateScore(game.getDealerHand());

        while (dealerScore < 17) {
            game.getDealerHand().add(deck.drawCard());
            dealerScore = calculateScore(game.getDealerHand());
        }

        int playerScore = calculateScore(game.getPlayerHand());
        String gameStatus = determineGameOutcome(playerScore, dealerScore);

        game.setStatus(gameStatus);
        return gameRepository.save(game);
    }

    private int calculateScore(List<Card> hand) {
        int total = 0;
        int aceCount = 0;

        for (Card card : hand) {
            int cardValue = card.getValue();
            total += cardValue;
            if ("A".equals(card.getRank())) {
                aceCount++;
            }
        }

        while (total > 21 && aceCount > 0) {
            total -= 10;
            aceCount--;
        }

        return total;
    }

    private String determineGameOutcome(int playerScore, int dealerScore) {
        if (playerScore > 21) {
            return "DEALER_WON";
        } else if (dealerScore > 21) {
            return "PLAYER_WON";
        } else if (playerScore > dealerScore) {
            return "PLAYER_WON";
        } else if (dealerScore > playerScore) {
            return "DEALER_WON";
        } else {
            return "TIE";
        }
    }

    @Override
    public Mono<Player> getPlayerById(String playerId) {
        return playerService.getPlayerById(playerId);
    }

    @Override
    public Flux<Player> getAllPlayers() {
        return playerService.getAllPlayers();
    }

    @Override
    public Flux<Player> getPlayerRanking() {
        return playerService.getPlayerRanking();
    }

    @Override
    public Mono<Player> createOrUpdatePlayer(Player player) {
        return playerService.createOrUpdatePlayer(player);
    }

    @Override
    public Mono<Void> deletePlayer(Long playerId) {
        return playerService.deletePlayer(playerId);
    }
}