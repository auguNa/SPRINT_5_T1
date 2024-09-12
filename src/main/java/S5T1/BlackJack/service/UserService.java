package S5T1.BlackJack.service;

import S5T1.BlackJack.entity.UserEntity;
import S5T1.BlackJack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashSet;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Retrieve user settings by username
    public Mono<UserEntity> getUserSettings(String username) {
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found with username: " + username)));
    }

    // Retrieve all users
    public Flux<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    // Create a new user
    public Mono<Void> createUser(UserEntity userEntity) {
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        return Mono.fromRunnable(() -> userRepository.save(userEntity))
                .then();
    }

    // Update existing user
    public Mono<Void> updateUser(Long userId, UserEntity updatedUserEntity) {
        return userRepository.findById(userId)
                .flatMap(userEntity -> {
                    userEntity.setUsername(updatedUserEntity.getUsername());
                    if (updatedUserEntity.getPassword() != null && !updatedUserEntity.getPassword().isEmpty()) {
                        userEntity.setPassword(passwordEncoder.encode(updatedUserEntity.getPassword()));
                    }
                    userEntity.setRoles(new HashSet<>(updatedUserEntity.getRoles())); // Create a new mutable set
                    return userRepository.save(userEntity).then(); // Save user and then complete
                })
                .switchIfEmpty(Mono.error(new RuntimeException("User not found with ID: " + userId)));
    }

    // Delete user by ID
    public Mono<Void> deleteUser(Long userId) {
        return userRepository.findById(userId)
                .flatMap(userEntity -> {
                    // Create a new user entity with cleared roles
                    UserEntity updatedUser = new UserEntity();
                    updatedUser.setId(userEntity.getId());
                    updatedUser.setUsername(userEntity.getUsername());
                    updatedUser.setPassword(userEntity.getPassword());
                    updatedUser.setRoles(Collections.emptySet()); // Clear roles with a new set

                    return userRepository.save(updatedUser)
                            .then(userRepository.delete(updatedUser)); // Save and then delete
                })
                .switchIfEmpty(Mono.error(new RuntimeException("User not found with ID: " + userId)));
    }

    // Retrieve user by username
    public Mono<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found with username: " + username)));
    }
}
