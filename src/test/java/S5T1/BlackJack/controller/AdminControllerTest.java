package S5T1.BlackJack.controller;

import S5T1.BlackJack.dto.AuthRequest;
import S5T1.BlackJack.entity.Player;
import S5T1.BlackJack.entity.UserEntity;
import S5T1.BlackJack.repository.UserRepository;
import S5T1.BlackJack.service.AuthService;
import S5T1.BlackJack.service.PlayerService;
import S5T1.BlackJack.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AdminControllerTest {
    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @Mock
    private PlayerService playerService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerAdmin_Success() {
        AuthRequest request = new AuthRequest("admin", "password");
        when(authService.registerAdmin(anyString(), anyString())).thenReturn(Mono.empty());

        Mono<ResponseEntity<String>> response = adminController.registerAdmin(request);

        StepVerifier.create(response)
                .expectNext(ResponseEntity.ok("Admin registered successfully!"))
                .verifyComplete();

        verify(authService).registerAdmin(request.getUsername(), request.getPassword());
    }

    @Test
    void registerAdmin_Failure() {
        AuthRequest request = new AuthRequest("admin", "password");
        when(authService.registerAdmin(anyString(), anyString())).thenReturn(Mono.error(new RuntimeException("Error")));

        Mono<ResponseEntity<String>> response = adminController.registerAdmin(request);

        StepVerifier.create(response)
                .expectNext(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Admin registration failed: Error"))
                .verifyComplete();
    }

    @Test
    void getAllUsers_Success() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("admin");
        when(userService.getAllUsers()).thenReturn(Flux.just(new UserEntity(), new UserEntity()));

        Flux<UserEntity> users = adminController.getAllUsers(authentication);

        StepVerifier.create(users)
                .expectNextCount(2)
                .verifyComplete();

        verify(userService).getAllUsers();
    }

    @Test
    void createUser_Success() {
        UserEntity user = new UserEntity();
        when(userService.createUser(any(UserEntity.class))).thenReturn(Mono.empty());

        Mono<ResponseEntity<String>> response = adminController.createUser(user);

        StepVerifier.create(response)
                .expectNext(ResponseEntity.status(HttpStatus.CREATED).body("User created successfully!"))
                .verifyComplete();

        verify(userService).createUser(user);
    }

    @Test
    void createUser_Failure() {
        UserEntity user = new UserEntity();
        when(userService.createUser(any(UserEntity.class))).thenReturn(Mono.error(new RuntimeException("Error")));

        Mono<ResponseEntity<String>> response = adminController.createUser(user);

        StepVerifier.create(response)
                .expectNext(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User creation failed: Error"))
                .verifyComplete();
    }

    @Test
    void updateUser_Success() {
        UserEntity updatedUser = new UserEntity();
        when(userService.updateUser(anyLong(), any(UserEntity.class))).thenReturn(Mono.empty());

        Mono<ResponseEntity<String>> response = adminController.updateUser(1L, updatedUser);

        StepVerifier.create(response)
                .expectNext(ResponseEntity.ok("User updated successfully!"))
                .verifyComplete();

        verify(userService).updateUser(1L, updatedUser);
    }

    @Test
    void updateUser_Failure() {
        UserEntity updatedUser = new UserEntity();
        when(userService.updateUser(anyLong(), any(UserEntity.class))).thenReturn(Mono.error(new RuntimeException("Error")));

        Mono<ResponseEntity<String>> response = adminController.updateUser(1L, updatedUser);

        StepVerifier.create(response)
                .expectNext(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User update failed: Error"))
                .verifyComplete();
    }

    @Test
    void deleteUser_Success() {
        when(userService.deleteUser(anyLong())).thenReturn(Mono.empty());

        Mono<ResponseEntity<String>> response = adminController.deleteUser(1L);

        StepVerifier.create(response)
                .expectNext(ResponseEntity.ok("User deleted successfully!"))
                .verifyComplete();

        verify(userService).deleteUser(1L);
    }

    @Test
    void deleteUser_Failure() {
        when(userService.deleteUser(anyLong())).thenReturn(Mono.error(new RuntimeException("Error")));

        Mono<ResponseEntity<String>> response = adminController.deleteUser(1L);

        StepVerifier.create(response)
                .expectNext(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User deletion failed: Error"))
                .verifyComplete();
    }

    @Test
    void getAllGamesForAdmin_Success() {
        Player player1 = new Player();
        Player player2 = new Player();
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("admin");
        when(userRepository.findByUsername(anyString())).thenReturn(Mono.just(new UserEntity()));
        when(playerService.getPlayerRanking()).thenReturn(Flux.just(player1, player2));

        Flux<Player> response = adminController.getAllGamesForAdmin(authentication);

        StepVerifier.create(response)
                .expectNext(player1, player2)
                .verifyComplete();
    }

    @Test
    void getAllGamesForAdmin_Failure() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("admin");
        when(userRepository.findByUsername(anyString())).thenReturn(Mono.error(new RuntimeException("Error")));

        Flux<Player> response = adminController.getAllGamesForAdmin(authentication);

        StepVerifier.create(response)
                .expectComplete() // Since we're returning an empty Flux in case of error
                .verify();
    }
}

