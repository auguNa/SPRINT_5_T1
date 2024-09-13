package S5T1.BlackJack.service;

import S5T1.BlackJack.repository.UserRepository;
import S5T1.BlackJack.security.CustomUserDetails;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CustomUserDetailsService implements ReactiveUserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(CustomUserDetails::new) // CustomUserDetails should be compatible with UserDetails
                .cast(UserDetails.class) // Ensure the type is cast correctly
                .switchIfEmpty(Mono.empty());
    }
}
