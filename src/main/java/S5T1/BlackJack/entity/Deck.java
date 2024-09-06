package S5T1.BlackJack.entity;

import lombok.Data;

import java.util.Collections;
import java.util.Stack;

@Data
public class Deck {
    private Stack<Card> cards;

    public Deck() {
        this.cards = initializeDeck();
        Collections.shuffle(cards);
    }

    private Stack<Card> initializeDeck() {
        Stack<Card> deck = new Stack<>();
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};

        for (String suit : suits) {
            for (String rank : ranks) {
                deck.add(new Card(suit, rank));
            }
        }
        return deck;
    }

    public Card drawCard() {
        return cards.pop();
    }
}