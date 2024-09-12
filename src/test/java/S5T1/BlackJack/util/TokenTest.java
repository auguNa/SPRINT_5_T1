package S5T1.BlackJack.util;

import S5T1.BlackJack.BlackJackApplication;
import S5T1.BlackJack.security.CustomUserDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest(classes = BlackJackApplication.class)
public class TokenTest {

    private final JwtUtil jwtUtil;

    @Autowired
    public TokenTest(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Test
    public void testTokenGeneration() {
        String username = "testUser";
        List<String> roles = Arrays.asList("ROLE_USER");

        // Generate token
        CustomUserDetails userDetails = new CustomUserDetails(username, roles);
        String token = jwtUtil.generateToken(userDetails, roles);
        System.out.println("Generated Token: " + token);

        // Validate token
        try {
            if (jwtUtil.validateToken(token, userDetails)) {
                System.out.println("Token is valid.");
            } else {
                System.out.println("Token is invalid.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
