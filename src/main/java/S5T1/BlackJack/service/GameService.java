package S5T1.BlackJack.service;

import S5T1.BlackJack.entity.Card;
import S5T1.BlackJack.entity.Deck;
import S5T1.BlackJack.entity.Game;
import S5T1.BlackJack.entity.Player;
import S5T1.BlackJack.repository.GameRepository;
import S5T1.BlackJack.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameService {

    private final GameRepository gameRepository;   // MongoDB repository for games
    private final PlayerRepository playerRepository;  // MySQL repository for players

    @Autowired
    public GameService(GameRepository gameRepository, PlayerRepository playerRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    // --- MongoDB Interactions: Managing Blackjack Games ---

    public Mono<Game> createNewGame(String playerName) {
        Game game = new Game();
        game.setPlayerName(playerName);

        // Initialize a new deck and deal two cards to player and dealer
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

        return gameRepository.save(game);
    }

    public Mono<Game> getGameById(String id) {
        return gameRepository.findById(id);
    }

    public Mono<Game> makeMove(String id, String moveType) {
        return gameRepository.findById(id)
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
                });
    }

    public Mono<Void> deleteGame(String id) {
        return gameRepository.deleteById(id);
    }

    private Mono<Game> handleHit(Game game) {
        Deck deck = game.getDeck();
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

    // --- MySQL Interactions: Managing Players ---

    public Mono<Player> getPlayerById(String playerId) {
        return playerRepository.findById(playerId);
    }

    public Flux<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    public Mono<Player> createOrUpdatePlayer(Player player) {
        return playerRepository.save(player);
    }

    public Mono<Void> deletePlayer(String playerId) {
        return playerRepository.deleteById(playerId);
    }

    public Flux<Player> getPlayerRanking() {
        return playerRepository.findAll()
                .sort((p1, p2) -> Integer.compare(p2.getWins(), p1.getWins()));  // Sort by wins in descending order
    }
}