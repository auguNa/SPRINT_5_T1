package S5T1.BlackJack.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class AuthRequest {
    private String username;
    private String password;
    private Set<String> roles;
}