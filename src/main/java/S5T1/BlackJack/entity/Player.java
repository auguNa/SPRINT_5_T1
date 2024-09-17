package S5T1.BlackJack.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("players")
@Getter
@Setter
public class Player {

    @Setter
    @Getter
    @Id
    private Long id;

    @Column("name")
    private String name;

    @Column("score")
    private Integer score;

    private int wins;
    private int losses;

    public Player() {
    }

    public Player(String name, int wins, int losses) {
        this.name = name;
        this.wins = wins;
        this.losses = losses;
    }
}
