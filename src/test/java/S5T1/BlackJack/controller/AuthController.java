package S5T1.BlackJack.controller;

import S5T1.BlackJack.dto.AuthRequest;
import S5T1.BlackJack.entity.UserEntity;
import S5T1.BlackJack.service.AuthService;
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
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_Success() {
        AuthRequest authRequest = new AuthRequest("testUser", "testPassword");

        // Mock successful registration
        when(authService.registerAdmin(anyString(), anyString()))
                .thenReturn(Mono.empty());

        Mono<ResponseEntity<String>> response = authController.registerUser(authRequest);

        ResponseEntity<String> result = response.block(); // Blocking to get the result for assertion

        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("User registered successfully", result.getBody());
    }
    @Test
    void register_Failure() {
        AuthRequest authRequest = new AuthRequest("testUser", "testPassword");

        // Mock registration failure
        when(authService.registerAdmin(anyString(), anyString()))
                .thenReturn(Mono.error(new RuntimeException("Error")));

        Mono<ResponseEntity<String>> response = authController.registerUser(authRequest);

        ResponseEntity<String> result = response.block();

        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Registration failed: Error", result.getBody());
    }
    @Test
    void login_Success() {
        AuthRequest authRequest = new AuthRequest("testUser", "testPassword");
        String mockToken = "mockJwtToken";

        // Mock successful login
        when(authService.loginUser(anyString(), anyString()))
                .thenReturn(Mono.just(mockToken));

        Mono<ResponseEntity<String>> response = authController.login(authRequest);

        ResponseEntity<String> result = response.block();

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(mockToken, result.getBody());
    }
    @Test
    void login_Failure() {
        AuthRequest authRequest = new AuthRequest("testUser", "testPassword");

        // Mock login failure
        when(authService.loginUser(anyString(), anyString()))
                .thenReturn(Mono.error(new RuntimeException("Invalid credentials")));

        Mono<ResponseEntity<String>> response = authController.login(authRequest);

        ResponseEntity<String> result = response.block();

        assertNotNull(result);
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertEquals("Login failed: Invalid credentials", result.getBody());
    }
    @Test
    void getAuthenticatedUser_Success() {
        UserEntity mockUser = new UserEntity();
        mockUser.setUsername("testUser");

        // Mock authentication and user details
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testUser");

        // Mock userService behavior
        when(userService.findByUsername(anyString()))
                .thenReturn(Mono.just(mockUser));

        Mono<ResponseEntity<UserEntity>> response = authController.getAuthenticatedUser(authentication);

        ResponseEntity<UserEntity> result = response.block();

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("testUser", result.getBody().getUsername());
    }
    @Test
    void getAuthenticatedUser_Failure() {
        // Mock authentication and user details
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testUser");

        // Mock userService failure
        when(userService.findByUsername(anyString()))
                .thenReturn(Mono.error(new RuntimeException("Error fetching user")));

        Mono<ResponseEntity<UserEntity>> response = authController.getAuthenticatedUser(authentication);

        ResponseEntity<UserEntity> result = response.block();

        assertNotNull(result);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertNull(result.getBody());
    }

}