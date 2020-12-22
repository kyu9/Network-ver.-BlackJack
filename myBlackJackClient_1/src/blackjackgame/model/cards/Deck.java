package blackjackgame.model.cards;

import blackjackgame.main.Game;
import blackjackgame.model.cards.Enums.*;
import java.util.ArrayList;
import java.util.Random;

public final class Deck {
    private final ArrayList<Card> deck;
    private Game game;
    private Card card;
    private Random generator;
    
    public Deck(Game g) {
        setGame(g);
        deck = new ArrayList();
        generator = new Random();
    }
    
    public void populateDeck(int n) {
        clearDeck();
        for (int i = 0; i < n; i++) {
            for (Suit s : Suit.values()) {
                for (Value v : Value.values()) {
                    card = new Card(s,v);
                    deck.add(card);
                }
            }
        }  
    }
    
    public void clearDeck()
    {
        deck.clear();
    }
    
    public void removeCard(Suit s, Value v)
    {
        removeCardWithIndex(getCardIndex(s,v));
    }
    
    public int getCardIndex(Suit s, Value v) {
        int index = 0;
        for (Card c : deck) {
            if (c.getSuit() == s && c.getValue() == v) {
                index = deck.indexOf(c);
            }
        }
        return index;
    }
    
    public void removeCardWithIndex(int index)
    {
        deck.remove(index);
    }
    
    public int randomCardIndex() {
        int index;
        index = generator.nextInt(deck.size());
        return index;
    }
    public void setGame(Game g) {
        game = g;
    }
    public void printDeck() {
        for (Card c : deck) {
            System.out.println(c.getSuit()+" : "+c.getValue());
        }
        System.out.println("Number of cards: "+deck.size());
    }
}