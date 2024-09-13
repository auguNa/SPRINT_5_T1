package S5T1.BlackJack.controller;

import S5T1.BlackJack.dto.AuthRequest;
import S5T1.BlackJack.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<String>> register(@RequestBody AuthRequest authRequest) {
        return authService.registerUser(authRequest.getUsername(), authRequest.getPassword())
                .then(Mono.just(ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully")))
                .onErrorResume(e -> {
                    log.error("Error during registration: ", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Registration failed: " + e.getMessage()));
                });
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<String>> login(@RequestBody AuthRequest authRequest) {
        return authService.loginUser(authRequest.getUsername(), authRequest.getPassword())
                .map(token -> ResponseEntity.ok().body("Bearer " + token))
                .onErrorResume(e -> {
                    log.error("Error during login: ", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body("Login failed: " + e.getMessage()));
                });
    }
}
