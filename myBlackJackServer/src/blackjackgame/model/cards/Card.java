package blackjackgame.model.cards;

import blackjackgame.model.cards.Enums.*;

public class Card {
    private final Suit suit;
    private final Value value;
    
    public Card(Suit s, Value v) {
        suit = s;
        value = v;
    }
    
    public Suit getSuit() {
        return suit;
    }
    
    public Value getValue() {
        return value;
    }
}
