package S5T1.BlackJack.service;

import S5T1.BlackJack.entity.Player;
import S5T1.BlackJack.entity.UserEntity;
import S5T1.BlackJack.repository.PlayerRepository;
import S5T1.BlackJack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import S5T1.BlackJack.security.JwtUtil;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PlayerService playerService;


    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, PlayerService playerService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.playerService = playerService;
    }

    // Register a new user
    public Mono<UserEntity> registerUser(String username, String password) {
        return userRepository.findByUsername(username)
                .flatMap(existingUser -> Mono.error(new RuntimeException("User already exists")))
                .switchIfEmpty(Mono.defer(() -> {
                    // Encode password and save new user
                    String encodedPassword = passwordEncoder.encode(password);
                    UserEntity newUser = new UserEntity();
                    newUser.setUsername(username);
                    newUser.setPassword(encodedPassword);
                    return userRepository.save(newUser)
                            .flatMap(user -> {
                                // Create a player profile in MongoDB
                                Player player = new Player();
                                player.setUserId(user.getId());
                                player.setName(username);
                                player.setWins(0); // Initialize with 0 wins
                                player.setLosses(0);
                                return playerService.createPlayerWithCustomId(player)
                                        .thenReturn(user); // Return the saved user
                            });
                }))
                .cast(UserEntity.class); // Ensure proper casting to UserEntity
    }

    // Login a user and generate JWT token
    public Mono<String> loginUser(String username, String password) {
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
                .flatMap(user -> {
                    // Check password
                    if (passwordEncoder.matches(password, user.getPassword())) {
                        // Generate JWT token
                        return Mono.just(jwtUtil.generateToken(user.getUsername()));
                    } else {
                        return Mono.error(new RuntimeException("Invalid credentials"));
                    }
                });
    }
}
