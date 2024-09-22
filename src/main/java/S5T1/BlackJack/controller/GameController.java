package S5T1.BlackJack.controller;

import S5T1.BlackJack.entity.Game;
import S5T1.BlackJack.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/game")
@Tag(name = "Game Controller", description = "Endpoints for managing Blackjack games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new Blackjack game")
    public Mono<Game> createNewGame(@RequestBody String playerName) {
        return gameService.createNewGame(playerName);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get game details by ID")
    public Mono<Game> getGameDetails(@PathVariable String id) {
        return gameService.getGameById(id);
    }

    @PostMapping("/{id}/play")
    @Operation(summary = "Make a move in the game")
    public Mono<Game> makeMove(@PathVariable String id, @RequestParam String moveType) {
        return gameService.makeMove(id, moveType);
    }

    @DeleteMapping("/{id}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a game by ID")
    public Mono<Void> deleteGame(@PathVariable String id) {
        return gameService.deleteGame(id);
    }
}
