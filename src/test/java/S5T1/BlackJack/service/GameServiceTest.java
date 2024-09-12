package S5T1.BlackJack.service;

import S5T1.BlackJack.entity.Card;
import S5T1.BlackJack.entity.Deck;
import S5T1.BlackJack.entity.Game;
import S5T1.BlackJack.entity.Player;
import S5T1.BlackJack.repository.GameRepository;
import S5T1.BlackJack.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private GameService gameService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateNewGame() {
        Game game = new Game();
        game.setPlayerName("Player1");
        game.setPlayerHand(new ArrayList<>());
        game.setDealerHand(new ArrayList<>());
        game.setStatus("IN_PROGRESS");
        game.setDeck(new Deck());

        when(gameRepository.save(any(Game.class))).thenReturn(Mono.just(game));

        Mono<Game> createdGame = gameService.createNewGame("Player1");

        StepVerifier.create(createdGame)
                .expectNextMatches(g -> g.getPlayerName().equals("Player1") &&
                        g.getStatus().equals("IN_PROGRESS"))
                .verifyComplete();
    }

    @Test
    void testGetGameById() {
        Game game = new Game();
        game.setPlayerName("Player1");

        when(gameRepository.findById(anyString())).thenReturn(Mono.just(game));

        Mono<Game> fetchedGame = gameService.getGameById("gameId");

        StepVerifier.create(fetchedGame)
                .expectNextMatches(g -> g.getPlayerName().equals("Player1"))
                .verifyComplete();
    }

    @Test
    void testMakeMove_Hit() {
        Deck deck = new Deck();
        List<Card> playerHand = new ArrayList<>();
        List<Card> dealerHand = new ArrayList<>();
        playerHand.add(new Card("HEARTS", "5"));
        playerHand.add(new Card("SPADES", "7"));
        dealerHand.add(new Card("DIAMONDS", "9"));
        dealerHand.add(new Card("CLUBS", "10"));

        Game game = new Game();
        game.setPlayerHand(playerHand);
        game.setDealerHand(dealerHand);
        game.setDeck(deck);
        game.setStatus("IN_PROGRESS");

        when(gameRepository.findById(anyString())).thenReturn(Mono.just(game));
        when(gameRepository.save(any(Game.class))).thenReturn(Mono.just(game));

        Mono<Game> updatedGame = gameService.makeMove("gameId", "hit");

        StepVerifier.create(updatedGame)
                .expectNextMatches(g -> g.getPlayerHand().size() > 2)
                .verifyComplete();
    }

    @Test
    void testMakeMove_Stand() {
        Deck deck = new Deck();
        List<Card> playerHand = new ArrayList<>();
        List<Card> dealerHand = new ArrayList<>();
        playerHand.add(new Card("HEARTS", "5"));
        playerHand.add(new Card("SPADES", "7"));
        dealerHand.add(new Card("DIAMONDS", "9"));
        dealerHand.add(new Card("CLUBS", "10"));

        Game game = new Game();
        game.setPlayerHand(playerHand);
        game.setDealerHand(dealerHand);
        game.setDeck(deck);
        game.setStatus("IN_PROGRESS");

        when(gameRepository.findById(anyString())).thenReturn(Mono.just(game));
        when(gameRepository.save(any(Game.class))).thenReturn(Mono.just(game));

        Mono<Game> updatedGame = gameService.makeMove("gameId", "stand");

        StepVerifier.create(updatedGame)
                .expectNextMatches(g -> g.getStatus().equals("PLAYER_WON") ||
                        g.getStatus().equals("DEALER_WON") ||
                        g.getStatus().equals("TIE"))
                .verifyComplete();
    }

    @Test
    void testDeleteGame() {
        when(gameRepository.deleteById(anyString())).thenReturn(Mono.empty());

        Mono<Void> result = gameService.deleteGame("gameId");

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void testGetPlayerById() {
        Player player = new Player();
        player.setId("playerId");

        when(playerRepository.findById(anyString())).thenReturn(Mono.just(player));

        Mono<Player> fetchedPlayer = gameService.getPlayerById("playerId");

        StepVerifier.create(fetchedPlayer)
                .expectNextMatches(p -> p.getId().equals("playerId"))
                .verifyComplete();
    }

    @Test
    void testGetAllPlayers() {
        Player player = new Player();
        player.setId("playerId");

        when(playerRepository.findAll()).thenReturn(Flux.just(player));

        Flux<Player> players = gameService.getAllPlayers();

        StepVerifier.create(players)
                .expectNextMatches(p -> p.getId().equals("playerId"))
                .verifyComplete();
    }

    @Test
    void testCreateOrUpdatePlayer() {
        Player player = new Player();
        player.setId("playerId");

        when(playerRepository.save(any(Player.class))).thenReturn(Mono.just(player));

        Mono<Player> savedPlayer = gameService.createOrUpdatePlayer(player);

        StepVerifier.create(savedPlayer)
                .expectNextMatches(p -> p.getId().equals("playerId"))
                .verifyComplete();
    }

    @Test
    void testDeletePlayer() {
        when(playerRepository.deleteById(anyString())).thenReturn(Mono.empty());

        Mono<Void> result = gameService.deletePlayer("playerId");

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void testGetPlayerRanking() {
        Player player = new Player();
        player.setId("playerId");
        player.setWins(10);

        when(playerRepository.findAll()).thenReturn(Flux.just(player));

        Flux<Player> rankedPlayers = gameService.getPlayerRanking();

        StepVerifier.create(rankedPlayers)
                .expectNextMatches(p -> p.getWins() == 10)
                .verifyComplete();
    }
}
