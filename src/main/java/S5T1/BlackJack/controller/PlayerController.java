package S5T1.BlackJack.controller;

import S5T1.BlackJack.entity.Player;
import S5T1.BlackJack.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/player")
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PutMapping("/{playerId}")
    public Mono<Player> changePlayerName(@PathVariable String playerId, @RequestParam String newName) {
        return playerService.changePlayerName(playerId, newName);
    }

    @GetMapping("/ranking")
    public Flux<Player> getPlayerRanking() {
        return playerService.getPlayerRanking();
    }
}
