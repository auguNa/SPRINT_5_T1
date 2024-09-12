package S5T1.BlackJack.controller;

import S5T1.BlackJack.entity.Game;
import S5T1.BlackJack.service.GameService;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {
    @Mock
    private GameService gameService;

    @InjectMocks
    private GameController gameController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createNewGame() {
        String playerName = "John";
        Game mockGame = new Game();
        when(gameService.createNewGame(playerName)).thenReturn(Mono.just(mockGame));

        Mono<Game> result = gameController.createNewGame(playerName);

        assertNotNull(result);
        result.subscribe(game -> assertEquals(mockGame, game));
        verify(gameService, times(1)).createNewGame(playerName);
    }
    @Test
    void getGameDetails() {
        String gameId = "1";
        Game mockGame = new Game();
        when(gameService.getGameById(gameId)).thenReturn(Mono.just(mockGame));

        Mono<Game> result = gameController.getGameDetails(gameId);

        assertNotNull(result);
        result.subscribe(game -> assertEquals(mockGame, game));
        verify(gameService, times(1)).getGameById(gameId);
    }

    @Test
    void makeMove() {
        String gameId = "1";
        String moveType = "hit";
        Game mockGame = new Game();
        when(gameService.makeMove(gameId, moveType)).thenReturn(Mono.just(mockGame));

        Mono<Game> result = gameController.makeMove(gameId, moveType);

        assertNotNull(result);
        result.subscribe(game -> assertEquals(mockGame, game));
        verify(gameService, times(1)).makeMove(gameId, moveType);
    }
    @Test
    void deleteGame() {
        String gameId = "1";
        when(gameService.deleteGame(gameId)).thenReturn(Mono.empty());

        Mono<Void> result = gameController.deleteGame(gameId);

        assertNotNull(result);
        verify(gameService, times(1)).deleteGame(gameId);
    }
}

