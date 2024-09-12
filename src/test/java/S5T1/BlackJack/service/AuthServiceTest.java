package S5T1.BlackJack.service;

import S5T1.BlackJack.entity.Role;
import S5T1.BlackJack.entity.UserEntity;
import S5T1.BlackJack.repository.RoleRepository;
import S5T1.BlackJack.repository.UserRepository;
import S5T1.BlackJack.service.AuthService;
import S5T1.BlackJack.service.CustomUserDetailsService;
import S5T1.BlackJack.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ReactiveAuthenticationManager authenticationManager;

    @Mock
    private CustomUserDetailsService userDetailsService;  // Mock the userDetailsService

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRegisterAdmin_Success() {
        // Mocking the interactions
        when(userRepository.findByUsername(anyString())).thenReturn(Mono.empty());  // No user found
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Mono.just(new Role("ROLE_ADMIN")));  // Admin role found
        when(userRepository.save(any(UserEntity.class))).thenReturn(Mono.just(new UserEntity()));  // Mock saving user
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");  // Mock password encoding

        // Calling the method under test
        Mono<Object> result = authService.registerAdmin("admin", "password");

        // Verifying the result
        StepVerifier.create(result)
                .expectNextMatches(user -> user instanceof UserEntity)
                .verifyComplete();
    }

    @Test
    public void testRegisterAdmin_UserAlreadyExists() {
        // Mock user repository to return an existing user (simulating that the user already exists)
        when(userRepository.findByUsername(anyString())).thenReturn(Mono.just(new UserEntity()));

        // Mock role repository to return an admin role
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Mono.just(new Role("ROLE_ADMIN")));  // Fix: return a Mono, not null

        // Call the method under test
        Mono<Object> result = authService.registerAdmin("admin", "password");

        // Verify that an error is returned because the user already exists
        StepVerifier.create(result)
                .expectErrorMessage("Username already exists")
                .verify();
    }


    @Test
    public void testRegisterAdmin_RoleNotFound() {
        // Mocking the case where the role is not found
        when(userRepository.findByUsername(anyString())).thenReturn(Mono.empty());
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Mono.empty());

        // Calling the method under test
        Mono<Object> result = authService.registerAdmin("admin", "password");

        // Verifying the error
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void testLoginUser_Success() {
        // Create mock UserDetails
        UserDetails userDetails = User.withUsername("admin")
                .password("encodedPassword")
                .roles("ADMIN")
                .build();

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("admin");
        userEntity.setPassword("encodedPassword");
        userEntity.setRoles(Collections.singleton("ROLE_ADMIN"));

        // Mocking the interactions
        when(userDetailsService.findByUsername(anyString())).thenReturn(Mono.just(userDetails));  // Mocking UserDetailsService
        when(userRepository.findByUsername(anyString())).thenReturn(Mono.just(userEntity));  // Mocking UserEntity from the repository
        when(jwtUtil.generateToken(userDetails, List.of("ROLE_ADMIN"))).thenReturn("jwtToken");  // Mock JWT generation
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(Mono.just(new UsernamePasswordAuthenticationToken(userDetails, "password", userDetails.getAuthorities())));

        // Calling the method under test
        Mono<String> result = authService.loginUser("admin", "password");

        // Verifying the result
        StepVerifier.create(result)
                .expectNext("jwtToken")
                .verifyComplete();
    }
}
