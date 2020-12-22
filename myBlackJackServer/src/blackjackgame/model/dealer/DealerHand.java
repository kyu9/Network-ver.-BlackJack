package blackjackgame.model.dealer;

import blackjackgame.main.Game;
import blackjackgame.model.cards.Hand;

public class DealerHand extends Hand {
    
    public DealerHand(Game g) {
        setGame(g);
    }
}
