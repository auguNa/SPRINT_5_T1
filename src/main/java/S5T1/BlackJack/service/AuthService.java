package S5T1.BlackJack.service;

import S5T1.BlackJack.entity.UserEntity;
import S5T1.BlackJack.repository.RoleRepository;
import S5T1.BlackJack.repository.UserRepository;
import S5T1.BlackJack.util.JwtUtil;
import S5T1.BlackJack.util.CustomReactiveAuthenticationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private CustomReactiveAuthenticationManager customReactiveAuthenticationManager; // Use custom auth manager

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Mono<Object> registerAdmin(String username, String password) {
        logger.info("Attempting to register admin with username: {}", username);
        return userRepository.findByUsername(username)
                .flatMap(existingUser -> {
                    logger.warn("Username already exists: {}", username);
                    return Mono.error(new RuntimeException("Username already exists"));
                })
                .switchIfEmpty(
                        roleRepository.findByName("ROLE_ADMIN")
                                .switchIfEmpty(Mono.error(new RuntimeException("Admin role not found")))
                                .flatMap(adminRole -> {
                                    UserEntity admin = new UserEntity();
                                    admin.setUsername(username);
                                    admin.setPassword(passwordEncoder.encode(password));
                                    Set<String> roles = Collections.singleton(adminRole.getName());
                                    admin.setRoles(roles);
                                    logger.info("Saving new admin user: {}", username);
                                    return userRepository.save(admin)
                                            .then(Mono.just("Admin registered successfully!"));
                                })
                );
    }

    // Register Normal User
    public Mono<Object> registerUser(String username, String password) {
        logger.info("Attempting to register user with username: {}", username);
        return userRepository.findByUsername(username)
                .flatMap(existingUser -> {
                    logger.warn("Username already exists: {}", username);
                    return Mono.error(new RuntimeException("Username already exists"));
                })
                .switchIfEmpty(
                        roleRepository.findByName("ROLE_USER")
                                .switchIfEmpty(Mono.error(new RuntimeException("User role not found")))
                                .flatMap(userRole -> {
                                    UserEntity user = new UserEntity();
                                    user.setUsername(username);
                                    user.setPassword(passwordEncoder.encode(password));
                                    Set<String> roles = Collections.singleton(userRole.getName());
                                    user.setRoles(roles);
                                    logger.info("Saving new user: {}", username);
                                    return userRepository.save(user)
                                            .then(Mono.just("User registered successfully!"));
                                })
                );
    }

    // Login User
    public Mono<String> loginUser(String username, String password) {
        return customReactiveAuthenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(username, password))
                .flatMap(authentication -> {
                    // Set the security context if needed in your reactive application
                    // ReactiveSecurityContextHolder.getContext().map(ctx -> ctx.setAuthentication(authentication));

                    // Find user by username
                    return userRepository.findByUsername(username)
                            .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")))
                            .flatMap(userEntity -> {
                                // Load UserDetails using CustomUserDetailsService (Reactive adaptation)
                                return userDetailsService.findByUsername(username)
                                        .map(userDetails -> {
                                            // Fetch user roles and add them to the JWT token
                                            List<String> roles = new ArrayList<>(userEntity.getRoles());

                                            // Generate the JWT token
                                            String token = jwtUtil.generateToken(userDetails, roles);

                                            // Log the generated token
                                            logger.info("Generated token for user: {}", token);

                                            return token;
                                        });
                            });
                })
                .doOnError(e -> logger.error("Error during login: {}", e.getMessage()));
    }
}
