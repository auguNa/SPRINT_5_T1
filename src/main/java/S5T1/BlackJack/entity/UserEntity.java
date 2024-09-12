package S5T1.BlackJack.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Table("user_entity")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    private Long id;

    @Column("username")
    private String username;

    @Column("password")
    private String password;

    @Column("roles")
    private String roles;
    public Set<String> getRoles() {
        return roles == null || roles.isEmpty() ? Set.of() : Set.of(roles.split(","));
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles == null ? "" : String.join(",", roles);
    }
}
