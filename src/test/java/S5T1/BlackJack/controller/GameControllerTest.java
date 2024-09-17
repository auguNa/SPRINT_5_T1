//package S5T1.BlackJack.controller;
//
//import S5T1.BlackJack.entity.Game;
//import S5T1.BlackJack.service.GameService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.web.reactive.server.WebTestClient;
//import reactor.core.publisher.Mono;
//
//import static org.mockito.Mockito.*;
//
//@WebFluxTest(GameController.class)
//public class GameControllerTest {
//
//    @Autowired
//    private WebTestClient webTestClient;
//
//    @MockBean
//    private GameService gameService;
//
//    @Test
//    public void testCreateNewGame() {
//        String playerName = "John Doe";
//        Game game = new Game();
//        game.setId("gameId");
//
//        when(gameService.createNewGame(playerName)).thenReturn(Mono.just(game));
//
//        webTestClient.post()
//                .uri("/game/new")
//                .bodyValue(playerName)
//                .exchange()
//                .expectStatus().isCreated()
//                .expectBody(Game.class).isEqualTo(game);
//
//        verify(gameService, times(1)).createNewGame(playerName);
//    }
//
//    @Test
//    public void testGetGameDetails() {
//        String gameId = "gameId";
//        Game game = new Game();
//        game.setId(gameId);
//
//        when(gameService.getGameById(gameId)).thenReturn(Mono.just(game));
//
//        webTestClient.get()
//                .uri("/game/{id}", gameId)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(Game.class).isEqualTo(game);
//
//        verify(gameService, times(1)).getGameById(gameId);
//    }
//
//    @Test
//    public void testMakeMove() {
//        String gameId = "gameId";
//        String moveType = "hit";
//        Game game = new Game();
//
//        when(gameService.makeMove(gameId, moveType)).thenReturn(Mono.just(game));
//
//        webTestClient.post()
//                .uri(uriBuilder -> uriBuilder.path("/game/{id}/play").queryParam("moveType", moveType).build(gameId))
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(Game.class).isEqualTo(game);
//
//        verify(gameService, times(1)).makeMove(gameId, moveType);
//    }
//
//    @Test
//    public void testDeleteGame() {
//        String gameId = "gameId";
//
//        when(gameService.deleteGame(gameId)).thenReturn(Mono.empty());
//
//        webTestClient.delete()
//                .uri("/game/{id}/delete", gameId)
//                .exchange()
//                .expectStatus().isNoContent();
//
//        verify(gameService, times(1)).deleteGame(gameId);
//    }
//}
