package S5T1.BlackJack.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories(basePackages = "S5T1.BlackJack.repository")
@EnableReactiveMongoRepositories(basePackages = "S5T1.BlackJack.repository")
public class DatabaseConfig {
}