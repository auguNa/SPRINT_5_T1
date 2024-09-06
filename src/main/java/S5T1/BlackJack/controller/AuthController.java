package S5T1.BlackJack.controller;


import S5T1.BlackJack.dto.AuthRequest;
import S5T1.BlackJack.entity.UserEntity;
import S5T1.BlackJack.service.AuthService;
import S5T1.BlackJack.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public Mono<ResponseEntity<String>> register(@RequestBody AuthRequest authRequest) {
        return authService.registerAdmin(authRequest.getUsername(), authRequest.getPassword())
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
                .map(token -> ResponseEntity.ok(token))
                .onErrorResume(e -> {
                    log.error("Error during login: ", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body("Login failed: " + e.getMessage()));
                });
    }

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public Mono<ResponseEntity<UserEntity>> getAuthenticatedUser(Authentication authentication) {
        return Mono.just(authentication)
                .map(auth -> (UserDetails) auth.getPrincipal())
                .flatMap(userDetails -> userService.findByUsername(userDetails.getUsername())
                        .map(userEntity -> ResponseEntity.ok(userEntity))
                        .onErrorResume(e -> {
                            log.error("Error fetching authenticated user details: ", e);
                            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
                        })
                );
    }

}