package S5T1.BlackJack.init;

import S5T1.BlackJack.entity.Role;
import S5T1.BlackJack.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class DataInitializer {

    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Bean
    public CommandLineRunner initializeRoles() {
        return args -> {
            Mono<Boolean> userRoleExists = roleRepository.existsByName("ROLE_USER");
            Mono<Boolean> adminRoleExists = roleRepository.existsByName("ROLE_ADMIN");

            Mono<Void> initRoles = userRoleExists
                    .flatMap(exists -> exists ? Mono.empty() : saveRole("ROLE_USER"))
                    .then(adminRoleExists.flatMap(exists -> exists ? Mono.empty() : saveRole("ROLE_ADMIN")));

            initRoles.subscribe(
                    null,
                    error -> System.err.println("Error initializing roles: " + error),
                    () -> System.out.println("Roles initialized successfully")
            );
        };
    }

    private Mono<Void> saveRole(String roleName) {
        Role role = new Role();
        role.setName(roleName);
        return roleRepository.save(role).then();
    }
}
