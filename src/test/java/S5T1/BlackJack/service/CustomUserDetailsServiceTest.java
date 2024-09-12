package S5T1.BlackJack.service;

import S5T1.BlackJack.entity.UserEntity;
import S5T1.BlackJack.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up a test user
        testUser = new UserEntity();
        testUser.setUsername("testuser");
        testUser.setPassword("testpassword");
    }

    @Test
    void testFindByUsername_UserExists() {
        // Mock the behavior of userRepository to return a Mono of the test user
        when(userRepository.findByUsername(anyString())).thenReturn(Mono.just(testUser));

        // Call the service method
        Mono<UserDetails> userDetailsMono = customUserDetailsService.findByUsername("testuser");

        // Use StepVerifier to verify the Mono behavior
        StepVerifier.create(userDetailsMono)
                .expectNextMatches(userDetails -> {
                    // Check the properties of the returned UserDetails
                    return userDetails.getUsername().equals(testUser.getUsername()) &&
                            userDetails.getPassword().equals(testUser.getPassword()) &&
                            userDetails.getAuthorities().isEmpty();  // Empty list for roles
                })
                .verifyComplete();
    }

    @Test
    void testFindByUsername_UserDoesNotExist() {
        // Mock the behavior of userRepository to return an empty Mono
        when(userRepository.findByUsername(anyString())).thenReturn(Mono.empty());

        // Call the service method
        Mono<UserDetails> userDetailsMono = customUserDetailsService.findByUsername("nonexistentuser");

        // Use StepVerifier to verify the Mono behavior
        StepVerifier.create(userDetailsMono)
                .expectError(UsernameNotFoundException.class)
                .verify();
    }
}
