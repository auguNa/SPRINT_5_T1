package S5T1.BlackJack.service;

import S5T1.BlackJack.entity.UserEntity;
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
    private final JwtUtil jwtUtil;  // Updated import

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
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
                    return userRepository.save(newUser);
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
