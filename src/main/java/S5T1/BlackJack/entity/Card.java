package S5T1.BlackJack.entity;

import lombok.Data;

@Data
public class Card {
    private String suit;
    private String rank;

    public Card(String suit, String rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public String getSuit() {
        return suit;
    }

    public String getRank() {
        return rank;
    }

    public int getValue() {
        // Return the value of the card (e.g., 2-10 are face value, J/Q/K are 10, A can be 1 or 11)
        if ("J".equals(rank) || "Q".equals(rank) || "K".equals(rank)) {
            return 10;
        } else if ("A".equals(rank)) {
            return 11; // You can handle Ace value separately based on hand value
        } else {
            return Integer.parseInt(rank);
        }
    }
}