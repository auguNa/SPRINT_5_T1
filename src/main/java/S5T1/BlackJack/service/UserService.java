package S5T1.BlackJack.service;

import S5T1.BlackJack.entity.UserEntity;
import S5T1.BlackJack.repository.PlayerRepository;
import S5T1.BlackJack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Create a new user
    public Mono<Void> createUser(UserEntity userEntity) {
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        return Mono.fromRunnable(() -> userRepository.save(userEntity))
                .then(); // Complete the Mono<Void> after saving
    }

    // Retrieve all users
    public Flux<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    // Retrieve user by username
    public Mono<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found with username: " + username)));
    }

    // Update existing user
    public Mono<Void> updateUser(Long userId, UserEntity updatedUserEntity) {
        return userRepository.findById(userId)
                .flatMap(userEntity -> {
                    userEntity.setUsername(updatedUserEntity.getUsername());
                    if (updatedUserEntity.getPassword() != null && !updatedUserEntity.getPassword().isEmpty()) {
                        userEntity.setPassword(passwordEncoder.encode(updatedUserEntity.getPassword()));
                    }
                    return Mono.fromRunnable(() -> userRepository.save(userEntity))
                            .then(); // Complete the Mono<Void> after saving
                })
                .switchIfEmpty(Mono.error(new RuntimeException("User not found with ID: " + userId)));
    }

    // Delete user by ID
    public Mono<Void> deleteUser(Long userId) {
        return userRepository.findById(userId)
                .flatMap(userEntity -> userRepository.delete(userEntity))
                .switchIfEmpty(Mono.error(new RuntimeException("User not found with ID: " + userId)));
    }
}