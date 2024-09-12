package S5T1.BlackJack.config;

import S5T1.BlackJack.service.CustomUserDetailsService;
import S5T1.BlackJack.util.CustomReactiveAuthenticationManager;
import S5T1.BlackJack.util.JwtUtil;
import S5T1.BlackJack.util.ReactiveJwtAuthenticationFilter;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;

import javax.crypto.SecretKey;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(@Lazy JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, ReactiveJwtAuthenticationFilter jwtFilter) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)  // Disable CSRF
                .authorizeExchange(authorize -> authorize
                        .pathMatchers("/api/admin/register").permitAll()
                        .pathMatchers("/api/auth/**").permitAll()
                        .pathMatchers("/admin/**").hasRole("ADMIN")
                        .pathMatchers("/user/**").hasRole("USER")
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)  // Apply JWT filter
                .authenticationManager(customReactiveAuthenticationManager())  // Use CustomReactiveAuthenticationManager
                .build();
    }

    @Bean
    @Lazy
    public CustomReactiveAuthenticationManager customReactiveAuthenticationManager() {
        return new CustomReactiveAuthenticationManager(jwtUtil, userDetailsService);
    }

    @Bean
    public ReactiveJwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil,
                                                                   CustomUserDetailsService userDetailsService,
                                                                   ServerSecurityContextRepository securityContextRepository) {
        return new ReactiveJwtAuthenticationFilter(jwtUtil, userDetailsService, securityContextRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecretKey secretKey() {
        return Keys.secretKeyFor(SignatureAlgorithm.HS256); // Same key for both classes
    }
}
