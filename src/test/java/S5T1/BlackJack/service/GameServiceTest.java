package S5T1.BlackJack.service;

import S5T1.BlackJack.entity.Game;
import S5T1.BlackJack.repository.GameRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

//public class GameServiceTest {
//
//    private GameRepository gameRepository = Mockito.mock(GameRepository.class);
//    private GameService gameService = new GameService(gameRepository);
//
//    @Test
//    public void createNewGameTest() {
//        when(gameRepository.save(any(Game.class))).thenReturn(Mono.just(new Game()));
//
//        Mono<Game> gameMono = gameService.createNewGame("Player1");
//
//        StepVerifier.create(gameMono)
//                .expectNextMatches(game -> game.getPlayerName().equals("Player1"))
//                .verifyComplete();
//    }
//}