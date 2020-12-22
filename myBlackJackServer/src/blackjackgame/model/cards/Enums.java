
package blackjackgame.model.cards;

public class  Enums {
    public enum Suit {
        DIAMOND, CLUB, HEART, SPADE; 
    }
    
    public enum Value {
        //13개
        //0, 13, 26, 39
       ACE(11),
       TWO(2),
       THREE(3),
       FOUR(4),
       FIVE(5),
       SIX(6),
       SEVEN(7),
       EIGHT(8),
       NINE(9),
       TEN(10),
       JACK(10),
       QUEEN(10),
        //12, 25, 38, 51
       KING(10); 
       
       int value;
       Value(int i) {
           value = i;
       }
       
       public int getValue() {
           return value;
       }
    }
}
