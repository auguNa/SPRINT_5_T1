package S5T1.BlackJack.service;

import S5T1.BlackJack.entity.Deck;
import S5T1.BlackJack.entity.Game;
import S5T1.BlackJack.entity.Player;
import S5T1.BlackJack.exception.CustomException;
import S5T1.BlackJack.exception.GameNotFoundException;
import S5T1.BlackJack.repository.GameRepository;
import S5T1.BlackJack.service.impl.GameServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private PlayerService playerService;

    @InjectMocks
    private GameServiceImpl gameService;

    private AutoCloseable closeable;
    private Player player;
    private Game game;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initializing player and game objects
        player = new Player();
        player.setId(1L);
        player.setName("John");

        game = new Game();
        game.setPlayerId("1");
        game.setId("1");
        game.setStatus("IN_PROGRESS");
        game.setDeck(new Deck());
        game.setPlayerHand(new ArrayList<>());
    }

    @AfterEach
    void tearDown() {
        // Reset player and game to null after each test
        player = null;
        game = null;
    }

    @DisplayName("BlackJackServiceUnitTest - Test create new player when player exists.")
    @Test
    void testCreateNewGame_PlayerExists() {
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
        when(gameRepository.findById("1")).thenReturn(Mono.just(game)); // Game exists
        when(gameRepository.deleteById("1")).thenReturn(Mono.empty()); // Successfully deleted
        // When
        Mono<Void> result = gameService.deleteGame("1");
        // Then
        StepVerifier.create(result)
                .verifyComplete(); // Verifies that the result completes successfully
        verify(gameRepository).findById("1");
        verify(gameRepository).deleteById("1");
    }

    @Test
    void testDeleteNonExistingGame() {
        // Given
        when(gameRepository.findById("1")).thenReturn(Mono.empty()); // Game does not exist
        // When
        Mono<Void> result = gameService.deleteGame("1");
        // Then
        StepVerifier.create(result)
                .expectError(GameNotFoundException.class) // Verifies that a GameNotFoundException is thrown
                .verify();
        verify(gameRepository).findById("1");
        verify(gameRepository, never()).deleteById(anyString()); // Ensure delete is never called
    }

}
