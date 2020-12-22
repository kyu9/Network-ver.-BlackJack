package blackjackgame.model.player;

import blackjackgame.main.Game;
import blackjackgame.model.cards.Hand;

public class PlayerHand extends Hand {
    
    public PlayerHand(Game g)
    {
        setGame(g);
    }
}
