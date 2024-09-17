package S5T1.BlackJack.service;

import S5T1.BlackJack.entity.Card;
import S5T1.BlackJack.entity.Deck;
import S5T1.BlackJack.entity.Game;
import S5T1.BlackJack.entity.Player;
import S5T1.BlackJack.exception.CustomException;
import S5T1.BlackJack.exception.GameNotFoundException;
import S5T1.BlackJack.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameService {
    private static final Logger log = LoggerFactory.getLogger(GameService.class);

    private final GameRepository gameRepository;
    private final PlayerService playerService;



    @Autowired
    public GameService(GameRepository gameRepository, PlayerService playerService) {
        this.gameRepository = gameRepository;
        this.playerService = playerService;
         }

    // --- MongoDB Interactions: Managing Blackjack Games ---
    public Mono<Game> createNewGame(String playerName) {
        // Find or create player
        return playerService.findByName(playerName)
                .switchIfEmpty(Mono.defer(() -> {
                    // Player not found, create and save new player
                    Player newPlayer = new Player();
                    newPlayer.setName(playerName);
                    newPlayer.setScore(0);
                    return playerService.savePlayer(newPlayer);
                }))
                .flatMap(player -> {
                    // Now that the player has been saved and has an id, proceed to create the game
                    Game game = new Game();

                    // Initialize a new deck and deal two cards to player and dealer
                    Deck deck = new Deck();
                    List<Card> playerHand = new ArrayList<>();
                    List<Card> dealerHand = new ArrayList<>();

                    // Draw cards
                    playerHand.add(deck.drawCard());
                    playerHand.add(deck.drawCard());

                    dealerHand.add(deck.drawCard());
                    dealerHand.add(deck.drawCard());

                    // Set game details
                    game.setPlayerHand(playerHand);
                    game.setDealerHand(dealerHand);
                    game.setStatus("IN_PROGRESS");
                    game.setDeck(deck);
                    game.setPlayerId(String.valueOf(player.getId())); // Assuming you want to associate the game with the player

                    return gameRepository.save(game)  // Save the game in MongoDB
                            .onErrorMap(e -> new CustomException("Error while creating game", e));
                });
    }



    public Mono<Game> getGameById(String id) {
        return gameRepository.findById(id)
                .switchIfEmpty(Mono.error(new GameNotFoundException("Game not found with ID: " + id)));
    }


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
                .onErrorResume(e -> {
                    // Handle specific exceptions or return a fallback Mono
                    return Mono.error(new CustomException("An error occurred while making a move", e));
                });
    }

    public Mono<Void> deleteGame(String id) {
        return gameRepository.deleteById(id);
    }

    private Mono<Game> handleHit(Game game) {
        log.debug("Handling hit for game ID: {}", game.getId());
        Deck deck = game.getDeck();

        // Ensure deck is not null
        if (deck == null) {
            return Mono.error(new IllegalStateException("Deck not initialized"));
        }

        // Add a new card to the player's hand
        game.getPlayerHand().add(deck.drawCard());

        // Calculate the player's score and update game status
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
            if (card.getRank().equals("A")) {
                aceCount++;
            }
        }
        // Adjust for Aces if needed (Ace can be 1 or 11)
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

    public Mono<Player> getPlayerById(String playerId) {
        return playerService.getPlayerById(playerId);
    }

    public Flux<Player> getAllPlayers() {
        return playerService.getAllPlayers();
    }

    public Flux<Player> getPlayerRanking() {
        return playerService.getPlayerRanking();
    }

    public Mono<Player> createOrUpdatePlayer(Player player) {
        return playerService.createOrUpdatePlayer(player);
    }

    public Mono<Void> deletePlayer(Long playerId) {
        return playerService.deletePlayer(playerId);
    }

}