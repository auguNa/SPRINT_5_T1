package S5T1.BlackJack.entity;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;

@Table("roles")
@Data
public class Role {

    @Id
    private Long id;
    @Column("name")
    private String name;

    public Role() {
    }

    public Role(String name) {
        this.name = name;
    }
}
