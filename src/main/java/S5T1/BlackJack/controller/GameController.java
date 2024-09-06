package S5T1.BlackJack.controller;

import S5T1.BlackJack.entity.Game;
import S5T1.BlackJack.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/game")
public class GameController {

    @Autowired
    private GameService gameService;

    @PostMapping("/new")
    public Mono<Game> createNewGame(@RequestParam String playerName) {
        return gameService.createNewGame(playerName);
    }

    @GetMapping("/{id}")
    public Mono<Game> getGameDetails(@PathVariable String id) {
        return gameService.getGameById(id);
    }

    @PostMapping("/{id}/play")
    public Mono<Game> makeMove(@PathVariable String id, @RequestParam String moveType) {
        return gameService.makeMove(id, moveType);
    }

    @DeleteMapping("/{id}/delete")
    public Mono<Void> deleteGame(@PathVariable String id) {
        return gameService.deleteGame(id);
    }
}