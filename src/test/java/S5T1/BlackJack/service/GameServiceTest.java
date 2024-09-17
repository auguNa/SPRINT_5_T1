package S5T1.BlackJack.service;

import S5T1.BlackJack.entity.Card;
import S5T1.BlackJack.entity.Deck;
import S5T1.BlackJack.entity.Game;
import S5T1.BlackJack.entity.Player;
import S5T1.BlackJack.exception.CustomException;
import S5T1.BlackJack.exception.GameNotFoundException;
import S5T1.BlackJack.repository.GameRepository;
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
import static org.mockito.Mockito.*;

class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private PlayerService playerService;

    @InjectMocks
    private GameService gameService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateNewGame_PlayerExists() {
        // Given
        Player player = new Player();
        player.setId(1L);
        player.setName("John");

        Game game = new Game();
        game.setPlayerId("1");

        when(playerService.findByName(anyString())).thenReturn(Mono.just(player));
        when(gameRepository.save(any(Game.class))).thenReturn(Mono.just(game));

        // When
        Mono<Game> result = gameService.createNewGame("John");

        // Then
        StepVerifier.create(result)
                .expectNextMatches(createdGame -> createdGame.getPlayerId().equals("1"))
                .verifyComplete();

        verify(playerService).findByName("John");
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    void testCreateNewGame_PlayerDoesNotExist() {
        // Given
        Player player = new Player();
        player.setId(1L);
        player.setName("John");

        Game game = new Game();
        game.setPlayerId("1");

        when(playerService.findByName(anyString())).thenReturn(Mono.empty());
        when(playerService.savePlayer(any(Player.class))).thenReturn(Mono.just(player));
        when(gameRepository.save(any(Game.class))).thenReturn(Mono.just(game));

        // When
        Mono<Game> result = gameService.createNewGame("John");

        // Then
        StepVerifier.create(result)
                .expectNextMatches(createdGame -> createdGame.getPlayerId().equals("1"))
                .verifyComplete();

        verify(playerService).findByName("John");
        verify(playerService).savePlayer(any(Player.class));
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    void testGetGameById_GameExists() {
        // Given
        Game game = new Game();
        game.setId("1");

        when(gameRepository.findById(anyString())).thenReturn(Mono.just(game));

        // When
        Mono<Game> result = gameService.getGameById("1");

        // Then
        StepVerifier.create(result)
                .expectNextMatches(fetchedGame -> fetchedGame.getId().equals("1"))
                .verifyComplete();

        verify(gameRepository).findById("1");
    }

    @Test
    void testGetGameById_GameNotFound() {
        // Given
        when(gameRepository.findById(anyString())).thenReturn(Mono.empty());

        // When
        Mono<Game> result = gameService.getGameById("1");

        // Then
        StepVerifier.create(result)
                .expectError(GameNotFoundException.class)
                .verify();

        verify(gameRepository).findById("1");
    }

    @Test
    void testMakeMove_HitMove() {
        // Given
        Game game = new Game();
        game.setId("1");
        game.setStatus("IN_PROGRESS");
        game.setDeck(new Deck());
        game.setPlayerHand(new ArrayList<>());

        when(gameRepository.findById(anyString())).thenReturn(Mono.just(game));
        when(gameRepository.save(any(Game.class))).thenReturn(Mono.just(game));

        // When
        Mono<Game> result = gameService.makeMove("1", "hit");

        // Then
        StepVerifier.create(result)
                .expectNextMatches(updatedGame -> updatedGame.getPlayerHand().size() == 1)
                .verifyComplete();

        verify(gameRepository).findById("1");
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    void testMakeMove_InvalidMoveType() {
        // Given
        Game game = new Game();
        game.setId("1");
        game.setStatus("IN_PROGRESS");

        when(gameRepository.findById(anyString())).thenReturn(Mono.just(game));

        // When
        Mono<Game> result = gameService.makeMove("1", "invalid_move");

        // Then
        StepVerifier.create(result)
                .expectError(CustomException.class)
                .verify();

        verify(gameRepository).findById("1");
    }


    @Test
    void testDeleteGame() {
        // Given
        when(gameRepository.deleteById(anyString())).thenReturn(Mono.empty());

        // When
        Mono<Void> result = gameService.deleteGame("1");

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(gameRepository).deleteById("1");
    }
}
