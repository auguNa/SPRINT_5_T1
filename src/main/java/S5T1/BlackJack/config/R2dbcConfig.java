//package S5T1.BlackJack.config;
//
//
//import io.r2dbc.spi.ConnectionFactories;
//import io.r2dbc.spi.ConnectionFactory;
//import io.r2dbc.spi.ConnectionFactoryOptions;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
//import org.springframework.r2dbc.connection.R2dbcTransactionManager;
//import org.springframework.transaction.ReactiveTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//import static io.r2dbc.spi.ConnectionFactoryOptions.*;
//@Configuration
//@EnableTransactionManagement
//
//public class R2dbcConfig  {
//
//    @Bean
//    public ConnectionFactory connectionFactory() {
//        return ConnectionFactories.get(ConnectionFactoryOptions.builder()
//                .option(DRIVER, "mysql")
//                .option(HOST, "localhost")
//                .option(PORT, 3306)  // Optional, default is 3306
//                .option(USER, "root")
//                .option(PASSWORD, "password")
//                .option(DATABASE, "Augu82")
//                .build());
//    }
//
//
//    @Bean
//    public R2dbcEntityTemplate r2dbcEntityTemplate(ConnectionFactory connectionFactory) {
//        return new R2dbcEntityTemplate(connectionFactory);
//    }
//
//    @Bean
//    public ReactiveTransactionManager transactionManager(ConnectionFactory connectionFactory) {
//        return new R2dbcTransactionManager(connectionFactory);
//    }
//}