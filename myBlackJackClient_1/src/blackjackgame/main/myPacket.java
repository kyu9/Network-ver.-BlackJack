package blackjackgame.main;

import blackjackgame.model.cards.Card;

public class myPacket {
    public String code;
    public String UserName;
    public String data;
    public Card card;

    public myPacket(String userName, String code, String msg){
        this.code = code;
        this.UserName = userName;
        this.data = msg;
    }

}
