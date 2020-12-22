
package blackjackgame.gui;

import blackjackgame.model.cards.Card;
import blackjackgame.model.cards.Enums.Suit;
import blackjackgame.model.cards.Enums.Value;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import static blackjackgame.main.Game.playername;

public class HandPanel extends JPanel{
    private JButton hit, stay, restart, quit, die, dd;
    private JLabel pValue, dValue, ident, sb;
    private JLabel pchip, pcurChip, pBettedChip;
    private JPanel etc, stat, hand, sp, btn, pinfo, pBettingChip;
    private JTextField pBet;
    public static JTextArea textArea = new JTextArea();
    public JLabel Dscore, P1score, P2score, P3score;
    public JButton betConfirm;
    JScrollPane scrollPane;

    private String pname="";
    protected ContainerPanel containerPanel;
    JLabel[] cards;
    int noOfCards = 5;
    String BetChip[] = {"0", "1", "2", "3", "4", "5", "10", "ALL IN"};
    JComboBox<String> pbet;

    public void AppendText(String str) {
        textArea.append(str + "\n");
        textArea.setCaretPosition(textArea.getText().length());
    }

    public void createHand() {
        LineBorder lb = new LineBorder(new Color(255,255,0), 1);
        btn = new JPanel();
        etc = new JPanel();
        stat = new JPanel();
        pinfo = new JPanel();
        hand = new JPanel();
        sp = new JPanel();

        ident = new JLabel();
        pValue = new JLabel();
        pchip = new JLabel();
        pBet = new JTextField(8);
        pbet = new JComboBox<String>(BetChip);

        btn.setLayout(new GridLayout(2,1));
        pValue.setFont(new Font("monospace", Font.BOLD, 20));
        pValue.setForeground(new Color(255,255,255));

        ident.setFont(new Font("monospace", Font.BOLD, 25));
        ident.setForeground(new Color(255,255,255));
        ident.setHorizontalAlignment(SwingConstants.CENTER);

        pinfo.setBackground(new Color(22, 122, 9));
        pinfo.setLayout(new GridLayout(1,2));

        pchip.setBackground(new Color(22, 122, 9));
        pchip.setLayout(new GridLayout(3,1));
        pcurChip = new JLabel();
        pBettedChip = new JLabel();
        pBettingChip = new JPanel();
        JLabel betn = new JLabel("베팅 : ");betn.setForeground(new Color(255,255,255));;
        pBettingChip.add(betn);
        pBettingChip.add(pbet);

        pBettingChip.setBorder(lb);pcurChip.setBorder(lb);pBettedChip.setBorder(lb);
        pBettingChip.setBackground(new Color(0,0,0));
        pcurChip.setBackground(new Color(0,0,0));
        pcurChip.setOpaque(true);pcurChip.setHorizontalAlignment(JLabel.CENTER);
        pBettedChip.setBackground(new Color(0,0,0));
        pBettedChip.setOpaque(true);pBettedChip.setHorizontalAlignment(JLabel.CENTER);
        pBettedChip.setForeground(new Color(255,255,255));
        pcurChip.setForeground(new Color(255,255,255));
        pchip.add(pBettedChip);pchip.add(pBettingChip);pchip.add(pcurChip);

        pinfo.add(ident);
        pinfo.add(pchip);

        hit = new JButton("Hit");
        stay = new JButton("Stay");
        die = new JButton("Die");
        dd = new JButton("DoubleDown");
        
        die.setBackground(new Color(0,0,0));
        die.setForeground(new Color(255,255,255));
        hit.setBackground(new Color(255,255,0));
        stay.setBackground(new Color(255,0,0));
        stay.setForeground(new Color(255,255,255));
        dd.setBackground(new Color(0,0,255));
        dd.setForeground(new Color(255,255,255));
        dd.setVisible(true);

        sp.add(hit);
        sp.add(stay);
        sp.add(die);
        sp.add(dd);


        btn.add(pValue);
        btn.add(sp);
        btn.setBackground(new Color(22,122,9));
        sp.setBackground(new Color(22, 122, 9));

        setLayout(new GridLayout(2, 1));

        etc.setLayout(new GridLayout(2, 1));

        stat.add(btn);

        etc.add(pinfo);
        etc.add(stat);

        cards = new JLabel[noOfCards];
        hand.setLayout(new GridLayout(1, noOfCards));
        ImageIcon image = new ImageIcon(getClass().getResource("/blackjackgame/gui/images/b1fv.png"));
        for (int i = 0; i < noOfCards; i++) {
            cards[i] = new JLabel(image);
            hand.add(cards[i]);
        }
        stat.setBackground(new Color(22, 122, 9));
        etc.setBackground(new Color(22, 122, 9));
        hand.setBackground(new Color(22, 122, 9));

        this.add(etc);
        this.add(hand);

    }

    public void createDHand() {
        etc = new JPanel();
        stat = new JPanel();
        ident = new JLabel();
        hand = new JPanel();

        dValue = new JLabel("Dealer: 0");
        ident = new JLabel("DEALER");

        ident.setFont(new Font("monospace", Font.BOLD, 25));
        ident.setForeground(new Color(255,255,255));
        ident.setBorder(new LineBorder(new Color(0,0,0)));
        dValue.setFont(new Font("monospace", Font.BOLD, 20));
        dValue.setForeground(new Color(255,255,255));

        setLayout(new GridLayout(2, 1));

        etc.setLayout(new GridLayout(2, 1));

        etc.add(ident);
        etc.add(dValue);

        cards = new JLabel[noOfCards];
        hand.setLayout(new GridLayout(1, noOfCards));
        ImageIcon image = new ImageIcon(getClass().getResource("/blackjackgame/gui/images/b1fv.png"));
        for (int i = 0; i < noOfCards; i++) {
            cards[i] = new JLabel(image);
            hand.add(cards[i]);
        }
        dValue.setHorizontalAlignment(JLabel.CENTER);
        ident.setHorizontalAlignment(JLabel.CENTER);
        stat.setBackground(new Color(22, 122, 9));
        etc.setBackground(new Color(22, 122, 9));
        hand.setBackground(new Color(22, 122, 9));

        this.add(etc);
        this.add(hand);
    }
    
    public void clearHand() {
        ImageIcon image = new ImageIcon(getClass().getResource("/blackjackgame/gui/images/b1fv.png"));
        for (int i=0;i<noOfCards;i++) {
            cards[i].setIcon(image);
        }
    }
    
    public void changeIcon() {
        ImageIcon image2 = new ImageIcon(getClass().getResource("/blackjackgame/gui/images/sk.png"));
        cards[5].setIcon(image2);
    }
    
    public void displayCard(String target,int index) {
        ImageIcon image;
        String imagePath;
        String suit;
        String value;
        Card card;
        if ("p".equals(target)){
            card =  containerPanel.getFrame().getGame().getPHand().getCards().get(index);
        }

        else if("p2".equals(target)){
            card =  containerPanel.getFrame().getGame().getP2Hand().getCards().get(index);
        }
        else if("p3".equals(target)){
            card =  containerPanel.getFrame().getGame().getP3Hand().getCards().get(index);
        }
        else {
            card =  containerPanel.getFrame().getGame().getDealerHand().getCards().get(index);
        }
        
        suit = getCardSuit(card);
        
        value = getCardValue(card);
        
        imagePath = "/blackjackgame/gui/images/"+suit+value+".png";
        image = new ImageIcon(getClass().getResource(imagePath));
        cards[index].setIcon(image);
    }
    
    public String getCardSuit(Card card) {
        String suit = null;
        
        if ("DIAMOND".equals(card.getSuit().toString())) {
            suit = "d";
        }
        else if ("CLUB".equals(card.getSuit().toString())) {
            suit = "c";
        }
        else if ("HEART".equals(card.getSuit().toString())) {
            suit = "h";
        }
        else if ("SPADE".equals(card.getSuit().toString())) {
            suit = "s";
        }
        return suit;
    }
    
    public String getCardValue(Card card) {
        String value = null;
        
        if ("ACE".equals(card.getValue().toString())) {
            value = "1";
        }
        else if ("ACE".equals(card.getValue().toString())) {
            value = "1";
        }
        else if ("TWO".equals(card.getValue().toString())) {
            value = "2";
        }
        else if ("THREE".equals(card.getValue().toString())) {
            value = "3";
        }
        else if ("FOUR".equals(card.getValue().toString())) {
            value = "4";
        }
        else if ("FIVE".equals(card.getValue().toString())) {
            value = "5";
        }
        else if ("SIX".equals(card.getValue().toString())) {
            value = "6";
        }
        else if ("SEVEN".equals(card.getValue().toString())) {
            value = "7";
        }
        else if ("EIGHT".equals(card.getValue().toString())) {
            value = "8";
        }
        else if ("NINE".equals(card.getValue().toString())) {
            value = "9";
        }
        else if ("TEN".equals(card.getValue().toString())) {
            value = "10";
        }
        else if ("JACK".equals(card.getValue().toString())) {
            value = "j";
        }
        else if ("QUEEN".equals(card.getValue().toString())) {
            value = "q";
        }
        else if ("KING".equals(card.getValue().toString())) {
            value = "k";
        }
        return value;
    }
    public void setContainerPanel(ContainerPanel cp) {
        containerPanel = cp;
    }
    public void fn(){

        this.setBackground(new Color(255,255,255));
        restart = new JButton("Ready for Next turn");
        restart.setBackground(new Color(0,0,0));
        restart.setForeground(new Color(255,255,255));
        restart.setFont(new Font("monospace", Font.BOLD, 20));
        restart.setBounds(50,350,295,60);
        containerPanel.getFrame().add(restart);

        quit = new JButton("나가기");
        quit.setBackground(new Color(255,0,0));
        quit.setForeground(new Color(255,255,255));
        quit.setFont(new Font("monospace", Font.BOLD, 20));
        quit.setBounds(50,20,295,60);
        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                containerPanel.getFrame().getGame().quitClient(pname, "400", "Bye");
            }
        });
        containerPanel.getFrame().add(quit);

        sb = new JLabel();
        sb.setBackground(Color.ORANGE);
        sb.setHorizontalAlignment(SwingConstants.CENTER);
        sb.setBounds(50,130,295,30);
        sb.setText("현재 게임에 참가하고 있는 사용자들");
        sb.setBorder(new LineBorder(new Color(0,0,0)));

        Dscore = new JLabel();
        Dscore.setBounds(50,160, 295,30);
        Dscore.setBorder(new LineBorder(new Color(0,0,0)));
        Dscore.setHorizontalAlignment(JLabel.CENTER);

        P1score = new JLabel();
        P1score.setBounds(50,190, 295,30);
        P1score.setBorder(new LineBorder(new Color(0,0,0)));
        P1score.setHorizontalAlignment(JLabel.CENTER);

        P2score = new JLabel();
        P2score.setBounds(50,220, 295,30);
        P2score.setBorder(new LineBorder(new Color(0,0,0)));
        P2score.setHorizontalAlignment(JLabel.CENTER);

        P3score = new JLabel();
        P3score.setBounds(50,250, 295,30);
        P3score.setBorder(new LineBorder(new Color(0,0,0)));
        P3score.setHorizontalAlignment(JLabel.CENTER);

        containerPanel.getFrame().add(sb);
        containerPanel.getFrame().add(Dscore);
        containerPanel.getFrame().add(P1score);
        containerPanel.getFrame().add(P2score);
        containerPanel.getFrame().add(P3score);
    }

    public void records(){
        textArea = new JTextArea();
        scrollPane = new JScrollPane();
        scrollPane.setBounds(787,0,395,430);
        containerPanel.getFrame().add(scrollPane);

        textArea.setEditable(false);
        textArea.setFont(new Font("monospace", Font.PLAIN, 15));

        scrollPane.setViewportView(textArea);
        AppendText("**********여기에는 모든 행동들이 기록됩니다***********");
    }

    public void setHandValue(String target,int val) {
        String [] unames = containerPanel.getFrame().getGame().getAllNames();
        JLabel j = null;
        String s ="";
        String d = "Dealer hand value: ";
        String curv = "Current value: ";
        if ("p".equals(target)) {
            j = pValue;
            s = unames[0] + " hand value: ";
        }else if("p2".equals(target)){
            j = pValue;
            s = unames[1] + " hand value: ";
        }else if("p3".equals(target)){
            j = pValue;
            s = unames[2] + " hand value: ";
        }
        else if("d".equals(target)) {
            j = dValue;
            s = d;
        }
        if (val < 21 && containerPanel.getFrame().getGame().getPHand().getCards().size() != 5 && "p".equals(target)) {
            j.setText(curv+val);
        }
        if (val < 21 && containerPanel.getFrame().getGame().getP2Hand().getCards().size() != 5 && "p2".equals(target)) {
            j.setText(curv+val);
        }
        if (val < 21 && containerPanel.getFrame().getGame().getP3Hand().getCards().size() != 5 && "p3".equals(target)) {
            j.setText(curv+val);
        }
        if (val < 21 && containerPanel.getFrame().getGame().getDealerHand().getCards().size() != 5 && "d".equals(target)) {
            j.setText(curv+val);
        }
        if(val >21 && containerPanel.getFrame().getGame().getDealerHand().getCards().size() != 5 && "d".equals(target)){
            j.setText(curv+val+" / BUST");
            AppendText("Dealer는 Bust되었습니다!");
        }
    }

    public void disableButton(String b) {
        switch(b) {
            case "h":
                hit.setEnabled(false);break;
            case "s":
                stay.setEnabled(false);break;
            case "r":
                restart.setEnabled(false);break;
            case "d":
                die.setEnabled(false);break;
            case "dd":
                dd.setEnabled(false);break;
        }
    }

    public void enableButton(String b) {
        switch(b) {
            case "h":
                hit.setEnabled(true);break;
            case "s":
                stay.setEnabled(true);break;
            case "r":
                restart.setEnabled(true);break;
            case "d":
                die.setEnabled(true);break;
            case "dd":
                dd.setEnabled(true);break;
        }
    }

    public JLabel getIdent(){ return ident; }
    public JLabel getPcurChip(){return pcurChip;}
    public JLabel getPBettedChip(){return pBettedChip;}
    public JComboBox getpbet(){return pbet;}
    public JPanel getHand(){return hand;}
    public JButton getDd(){return dd;}
    public JButton getHit(){return hit;}
    public JButton getStay(){return stay;}
    public JButton getDie(){return die;}
    public JTextField getPBet(){return pBet;}
    public JButton getRestart(){return restart;}
    public JLabel getDscore(){return Dscore;}
    public JLabel getP1score(){return P1score;}
    public JLabel getP2score(){return P2score;}
    public JLabel getP3score(){return P3score;}
    public JLabel getpValue(){return pValue;}
    public JLabel getdValue(){return dValue;}
    public JLabel getpBettedChip(){return pBettedChip;}
}
