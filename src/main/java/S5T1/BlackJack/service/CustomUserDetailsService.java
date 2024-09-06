package S5T1.BlackJack.service;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CustomUserDetailsService implements ReactiveUserDetailsService {

    // This method would need to actually look up the user in the database or other data source
    @Override
    public Mono<UserDetails> findByUsername(String username) {
        // Implement your user lookup logic here, e.g., find the user in a database
        // Return Mono<UserDetails> with the user or Mono.empty() if the user is not found
        return Mono.empty(); // Placeholder; implement your logic here
    }

    // Implement other methods as needed
}
