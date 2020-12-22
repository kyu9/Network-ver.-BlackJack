package blackjackgame.model.cards;

import blackjackgame.main.Game;
import java.util.ArrayList;

public abstract class Hand {
    private final ArrayList<Card> cards;
    protected Game game;
    
    public Hand() {
        cards = new ArrayList();
    }
    
    public void clearHand() {
        cards.clear();
    }
    
    public void addCard(Card card)
    {
        cards.add(card);
    }
    
    public int getHandValue() {
        int total = 0;
        int aces = 0;
        for (Card c : cards) {
            if (c.getValue().getValue() == 11) {
                aces++;
            }
            else {
            total += c.getValue().getValue();
            }
        }
        for (int i = 0; i < aces; i++) {
                if (total + 11 <= 21) {
                    total += 11;
                }

                else {
                    total +=1;
                }
            }
        return total;
    }
    
    public int checkHand() {
        int status;
        if (getHandValue() >21 ) {
            status = Game.BUST;
        }
        else if (getHandValue() == 21) {
            status = Game.BLACKJACK;
        }
        else if (cards.size() == 5) {
            status = Game.FIVE;
        }
        else {
            status = Game.SAFE;
        }
        return status;
    }
    
    public void setGame(Game g)
    {
        game = g;
    }
    
    public ArrayList<Card> getCards()
    {
        return cards;
    }
    public void printHand() {
        for (Card c : cards) {
            System.out.println(c.getSuit().name()+" "+c.getValue().name());
        }
    }
}
