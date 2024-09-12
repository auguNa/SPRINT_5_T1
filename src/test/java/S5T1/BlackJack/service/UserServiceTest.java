package S5T1.BlackJack.service;

import S5T1.BlackJack.entity.UserEntity;
import S5T1.BlackJack.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new UserEntity();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("testpassword");
        testUser.setRoles(Collections.singleton("ROLE_USER"));
    }

    @Test
    void testGetUserSettings_UserExists() {
        when(userRepository.findByUsername(anyString())).thenReturn(Mono.just(testUser));

        Mono<UserEntity> result = userService.getUserSettings("testuser");

        StepVerifier.create(result)
                .expectNextMatches(user -> user.getUsername().equals("testuser"))
                .verifyComplete();
    }

    @Test
    void testGetUserSettings_UserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Mono.empty());

        Mono<UserEntity> result = userService.getUserSettings("unknownuser");

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("User not found with username: unknownuser"))
                .verify();
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(Flux.just(testUser));

        Flux<UserEntity> result = userService.getAllUsers();

        StepVerifier.create(result)
                .expectNext(testUser)
                .verifyComplete();
    }

    @Test
    void testCreateUser() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedpassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(Mono.just(testUser)); // Fixed here

        Mono<Void> result = userService.createUser(testUser);

        StepVerifier.create(result)
                .verifyComplete();

        verify(passwordEncoder, times(1)).encode("testpassword");
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }


    @Test
    void testUpdateUser_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Mono.empty());

        Mono<Void> result = userService.updateUser(1L, testUser);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("User not found with ID: 1"))
                .verify();

        verify(userRepository, times(0)).save(any(UserEntity.class));
    }


    @Test
    void testDeleteUser_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Mono.empty());

        Mono<Void> result = userService.deleteUser(1L);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("User not found with ID: 1"))
                .verify();

        verify(userRepository, times(0)).delete(any(UserEntity.class));
    }

    @Test
    void testFindByUsername_UserExists() {
        when(userRepository.findByUsername(anyString())).thenReturn(Mono.just(testUser));

        Mono<UserEntity> result = userService.findByUsername("testuser");

        StepVerifier.create(result)
                .expectNextMatches(user -> user.getUsername().equals("testuser"))
                .verifyComplete();
    }

    @Test
    void testFindByUsername_UserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Mono.empty());

        Mono<UserEntity> result = userService.findByUsername("unknownuser");

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("User not found with username: unknownuser"))
                .verify();
    }
}
