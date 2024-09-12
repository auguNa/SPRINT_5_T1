package S5T1.BlackJack.controller;

import S5T1.BlackJack.dto.AuthRequest;
import S5T1.BlackJack.entity.Player;
import S5T1.BlackJack.entity.UserEntity;
import S5T1.BlackJack.repository.UserRepository;
import S5T1.BlackJack.service.AuthService;
import S5T1.BlackJack.service.PlayerService;
import S5T1.BlackJack.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AuthService authService;
    private final UserService userService;
    private final PlayerService playerService;
    private final UserRepository userRepository;

    @Autowired
    public AdminController(AuthService authService, UserService userService, PlayerService playerService, UserRepository userRepository) {
        this.authService = authService;
        this.userService = userService;
        this.playerService = playerService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<String>> registerAdmin(@RequestBody AuthRequest request) {
        return authService.registerAdmin(request.getUsername(), request.getPassword())
                .then(Mono.just(ResponseEntity.ok("Admin registered successfully!")))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Admin registration failed: " + e.getMessage())));
    }

    @GetMapping("/users")
    public Flux<UserEntity> getAllUsers(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        return userService.getAllUsers()
                .doOnNext(users -> log.info("Fetched users for admin: {}", username))
                .onErrorResume(e -> Flux.empty());
    }

    @PostMapping("/users")
    public Mono<ResponseEntity<String>> createUser(@RequestBody UserEntity userEntity) {
        return userService.createUser(userEntity)
                .then(Mono.just(ResponseEntity.status(HttpStatus.CREATED).body("User created successfully!")))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("User creation failed: " + e.getMessage())));
    }

    @PutMapping("/users/{userId}")
    public Mono<ResponseEntity<String>> updateUser(@PathVariable Long userId, @RequestBody UserEntity userEntity) {
        return userService.updateUser(userId, userEntity)
                .then(Mono.just(ResponseEntity.ok("User updated successfully!")))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("User update failed: " + e.getMessage())));
    }

    @DeleteMapping("/users/{userId}")
    public Mono<ResponseEntity<String>> deleteUser(@PathVariable Long userId) {
        return userService.deleteUser(userId)
                .then(Mono.just(ResponseEntity.ok("User deleted successfully!")))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("User deletion failed: " + e.getMessage())));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/ranking")
    public Flux<Player> getAllGamesForAdmin(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        log.info("Fetching player rankings for admin: {}", username);

        return Mono.justOrEmpty(userRepository.findByUsername(username))
                .flatMapMany(userEntity -> playerService.getPlayerRanking())
                .doOnNext(player -> log.info("Fetched player: {}", player))
                .onErrorResume(e -> {
                    log.error("Failed to fetch player rankings: {}", e.getMessage());
                    return Flux.empty(); // Return an empty Flux in case of error
                });
    }
}
