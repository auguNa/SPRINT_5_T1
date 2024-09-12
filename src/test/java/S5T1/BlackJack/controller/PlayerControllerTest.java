package S5T1.BlackJack.controller;

import S5T1.BlackJack.entity.Player;
import S5T1.BlackJack.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class PlayerControllerTest {

    @Mock
    private PlayerService playerService;

    @InjectMocks
    private PlayerController playerController;

    private Player player;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        player = new Player();
        player.setId("playerId123");
        player.setName("OldName");
    }

    @Test
    void changePlayerName_Success() {
        // Arrange
        String newName = "NewName";
        Player updatedPlayer = new Player();
        updatedPlayer.setId(player.getId());
        updatedPlayer.setName(newName);

        when(playerService.changePlayerName(player.getId(), newName)).thenReturn(Mono.just(updatedPlayer));

        // Act
        Mono<Player> result = playerController.changePlayerName(player.getId(), newName);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(p -> p.getId().equals(player.getId()) && p.getName().equals(newName))
                .verifyComplete();

        verify(playerService, times(1)).changePlayerName(player.getId(), newName);
    }

    @Test
    void changePlayerName_Failure() {
        // Arrange
        String newName = "NewName";
        when(playerService.changePlayerName(player.getId(), newName)).thenReturn(Mono.error(new RuntimeException("Player not found")));

        // Act
        Mono<Player> result = playerController.changePlayerName(player.getId(), newName);

        // Assert
        StepVerifier.create(result)
                .expectErrorMessage("Player not found")
                .verify();

        verify(playerService, times(1)).changePlayerName(player.getId(), newName);
    }

    @Test
    void getPlayerRanking_Success() {
        // Arrange
        Player player1 = new Player();
        player1.setId("playerId1");
        player1.setName("Player1");

        Player player2 = new Player();
        player2.setId("playerId2");
        player2.setName("Player2");

        when(playerService.getPlayerRanking()).thenReturn(Flux.just(player1, player2));

        // Act
        Flux<Player> result = playerController.getPlayerRanking();

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(p -> p.getId().equals("playerId1") && p.getName().equals("Player1"))
                .expectNextMatches(p -> p.getId().equals("playerId2") && p.getName().equals("Player2"))
                .verifyComplete();

        verify(playerService, times(1)).getPlayerRanking();
    }

    @Test
    void getPlayerRanking_Empty() {
        // Arrange
        when(playerService.getPlayerRanking()).thenReturn(Flux.empty());

        // Act
        Flux<Player> result = playerController.getPlayerRanking();

        // Assert
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();

        verify(playerService, times(1)).getPlayerRanking();
    }
}
