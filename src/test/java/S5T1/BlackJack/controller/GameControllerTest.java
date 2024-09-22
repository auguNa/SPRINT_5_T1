package S5T1.BlackJack.controller;

import S5T1.BlackJack.entity.Game;
import S5T1.BlackJack.entity.Player;
import S5T1.BlackJack.service.GameService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

public class GameControllerTest {

    private WebTestClient webTestClient;

    @Mock
    private GameService gameService;

    @InjectMocks
    private GameController gameController;

    private Player player;
    private Game game;

    @BeforeEach
    public void setup() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Create a Player instance
        player = new Player();
        player.setId(1L); // Set a sample ID
        player.setName("John Doe");

        // Create a Game instance
        game = new Game();
        game.setId("gameId");
        game.setPlayer(player); // Associate the player with the game

        // Initialize WebTestClient with the GameController instance
        this.webTestClient = WebTestClient.bindToController(gameController).build();
    }

    @AfterEach
    public void tearDown() {
        player = null; // Clean up Player instance
        game = null;   // Clean up Game instance
    }

    @Test
    public void testCreateNewGame() {
        when(gameService.createNewGame(player.getName())).thenReturn(Mono.just(game));

        webTestClient.post()
                .uri("/game/new")
                .bodyValue(player.getName())
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Game.class).isEqualTo(game);

        verify(gameService, times(1)).createNewGame(player.getName());
    }

    @Test
    public void testGetGameDetails() {
        when(gameService.getGameById(game.getId())).thenReturn(Mono.just(game));

        webTestClient.get()
                .uri("/game/{id}", game.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Game.class).isEqualTo(game);

        verify(gameService, times(1)).getGameById(game.getId());
    }

    @Test
    public void testMakeMove() {
        String moveType = "hit";

        when(gameService.makeMove(game.getId(), moveType)).thenReturn(Mono.just(game));

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/game/{id}/play").queryParam("moveType", moveType).build(game.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Game.class).isEqualTo(game);

        verify(gameService, times(1)).makeMove(game.getId(), moveType);
    }

    @Test
    public void testDeleteGame() {
        when(gameService.deleteGame(game.getId())).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/game/{id}/delete", game.getId())
                .exchange()
                .expectStatus().isNoContent();

        verify(gameService, times(1)).deleteGame(game.getId());
    }
}