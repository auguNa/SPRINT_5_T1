package S5T1.BlackJack.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

import java.util.List;

@Data
@Document(collection = "games")
@Getter
@Setter
public class Game {
    @Id
    private String id;
    private String playerId;
    private List<Card> dealerHand;
    private List<Card> playerHand;
    private String status;
    private int bet;
    private Deck deck;

    public void setPlayer(Player player) {
    }
}


