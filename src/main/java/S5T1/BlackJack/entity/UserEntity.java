package S5T1.BlackJack.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Set;

@Table("user_entity")
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column("username")
    private String username;


    @Column("roles")
    private String roles;

    public Set<String> getRoles() {
        return roles == null || roles.isEmpty() ? Set.of() : Set.of(roles.split(","));
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles == null ? "" : String.join(",", roles);
    }
}
