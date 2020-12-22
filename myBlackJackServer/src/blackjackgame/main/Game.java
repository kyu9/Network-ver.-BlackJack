package blackjackgame.main;

import blackjackgame.model.cards.Card;
import blackjackgame.model.cards.Deck;
import blackjackgame.model.dealer.DealerHand;
import blackjackgame.model.player.PlayerHand;


public class Game extends Main {
    private final PlayerHand pHand;
    
    //2, 3 플레이어
    private final PlayerHand p2Hand;
    private final PlayerHand p3Hand;
    private final DealerHand dHand;
    private final Deck deck;
    private int turn=0;

    //Card conditions
    public static final int SAFE = 1;
    public static final int BLACKJACK = 2;
    public static final int FIVE = 5;
    public static final int BUST = 4;
    private int forIndex[]={
            //1
            14,25,39,3,25,36,2,44,43,12,5,0,17,10
            ,//2
            25,48,36,51,20,2,10,37
            ,//3
            23,46,41,33,13,30,51,6,29,37
    };
    public int icount = 0;
    
    public Game() {
        pHand = new PlayerHand(this);

        //2,3player init
        p2Hand = new PlayerHand(this);
        p3Hand = new PlayerHand(this);

        dHand = new DealerHand(this);
        deck = new Deck(this);
        //score = new Score(this);
    }
    public String drawCard(String target){
        //랜덤방식
        return ForsendCard(target, deck.randomCardIndex());

        //정해진순서로 하는방식
        //int tmp = icount;
        //icount++;
        //return ForsendCard(target, forIndex[tmp]);
    }

    public void startGame() {
        turn++;
        deck.populateDeck(3);
    }
    public int getTurn(){return turn;}

    public void mainGame() {
        this.checkHands();
    }
    
    public void checkHands() {
        if (pHand.checkHand() > dHand.checkHand()) {
        }
        else if (pHand.checkHand() == BLACKJACK) {
        }
        else if (pHand.checkHand() == FIVE) {
        }
        if (dHand.checkHand() == BLACKJACK) {
        }
    }

    public int p1CheckHand() {
        if (pHand.checkHand() > dHand.checkHand()) {
            return 1;
        }
        else if (pHand.checkHand() == dHand.checkHand()) {
            return 0;
        }
        else if (pHand.checkHand() < dHand.checkHand()) {
            return -1;
        }
        return 404;
    }
    public int p2CheckHand() {
        if (p2Hand.checkHand() > dHand.checkHand()) {
            return 1;
        }
        else if (p2Hand.checkHand() == dHand.checkHand()) {
            return 0;
        }
        else if (p2Hand.checkHand() < dHand.checkHand()) {
            return -1;
        }
        return 404;
    }
    public int p3CheckHand() {
        if (p3Hand.checkHand() > dHand.checkHand()) {
            return 1;
        }
        else if (p3Hand.checkHand() == dHand.checkHand()) {
            return 0;
        }
        else if (p3Hand.checkHand() < dHand.checkHand()) {
            return -1;
        }
        return 404;
    }

    public String ForsendCard(String target, int index){
        String cardInfo="";
        Card c = deck.getCard(index);
        if("p".equals(target)){
            pHand.addCard(c);
            cardInfo = c.getSuit().name()+" "+c.getValue().name();
            return cardInfo;
        }else if("p2".equals(target)){
            p2Hand.addCard(c);
            cardInfo = c.getSuit().name()+" "+c.getValue().name();
            return cardInfo;
        }else if("p3".equals(target)){
            p3Hand.addCard(c);
            cardInfo = c.getSuit().name()+" "+c.getValue().name();
            return cardInfo;
        }else if("d".equals(target)){
            dHand.addCard(c);
            cardInfo = c.getSuit().name()+" "+c.getValue().name();
            return cardInfo;
        }
        return "";
    }

    public PlayerHand getPHand() { return pHand; }
    public DealerHand getDealerHand() { return dHand; }
    public PlayerHand getP2Hand() {return p2Hand;}
    public PlayerHand getP3Hand(){return p3Hand;}
}
