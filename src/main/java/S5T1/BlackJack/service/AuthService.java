package S5T1.BlackJack.service;

import S5T1.BlackJack.entity.UserEntity;
import S5T1.BlackJack.repository.RoleRepository;
import S5T1.BlackJack.repository.UserRepository;
import S5T1.BlackJack.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private ReactiveAuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtUtil jwtUtil; // Inject JwtUtil here

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Register a new Admin
    public Mono<Object> registerAdmin(String username, String password) {
        return userRepository.findByUsername(username)
                .flatMap(existingUser -> Mono.error(new RuntimeException("Username already exists")))
                .switchIfEmpty(
                        roleRepository.findByName("ROLE_ADMIN")
                                .switchIfEmpty(Mono.error(new RuntimeException("Admin role not found")))
                                .flatMap(adminRole -> {
                                    UserEntity admin = new UserEntity();
                                    admin.setUsername(username);
                                    admin.setPassword(passwordEncoder.encode(password));
                                    admin.setRoles(Collections.singleton(adminRole.getName())); // Ensure roles are a Set<String>
                                    return userRepository.save(admin); // Save and return Mono<UserEntity>
                                })
                );
    }


    public Mono<String> loginUser(String username, String password) {
        return Mono.defer(() -> authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        )).flatMap(authentication -> {
            return userDetailsService.findByUsername(username)
                    .flatMap(userDetails -> userRepository.findByUsername(username)
                            .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found with username: " + username)))
                            .map(userEntity -> {
                                // Fetch user roles and add them to the JWT token
                                List<String> roles = (List<String>) userEntity.getRoles(); // Directly get roles as a list of strings
                                return jwtUtil.generateToken(userDetails, roles); // Use jwtUtil to generate token
                            })
                    );
        });
    }
}
