package blackjackgame.main;

import blackjackgame.gui.Frame;
import blackjackgame.model.cards.Card;
import blackjackgame.model.cards.Deck;
import blackjackgame.model.cards.Enums;
import blackjackgame.model.dealer.DealerHand;
import blackjackgame.model.player.PlayerHand;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

import static java.lang.System.exit;


public class Game{
    private static final long serialVersionUID = 1L;
    private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
    private Socket socket; // 연결소켓
    private InputStream is;
    private OutputStream os;
    private DataInputStream dis;
    private DataOutputStream dos;

    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    private final Frame myFrame;
    private final PlayerHand pHand;
    
    //2, 3 플레이어
    private final PlayerHand p2Hand;
    private final PlayerHand p3Hand;
    private final DealerHand dHand;
    private final Deck deck;

    private int deckNum=0;
    
    //Card conditions
    public static final int SAFE = 1;
    public static final int BLACKJACK = 2;
    public static final int FIVE = 5;
    public static final int BUST = 4;


    public static String playername="";
    private String ip="";
    private String port="";

    public String[] userNames = {"","",""};
    public String[] userBettedChip = {"0","0","0"};
    public String[] userCurChip = {"20","20","20"};


    public int p1bet = 0;
    public int p2bet = 0;
    public int p3bet = 0;

    public Card card;
    
    public Game(String username,String ip_addr,String port_no) {
        myFrame = new Frame(this);
        pHand = new PlayerHand(this);

        //2,3player init
        p2Hand = new PlayerHand(this);
        p3Hand = new PlayerHand(this);

        dHand = new DealerHand(this);
        deck = new Deck(this);

        playername = username;
        ip=ip_addr;
        port=port_no;

        p1DisAllBtn();p2DisAllBtn();p3DisAllBtn();
        myFrame.getContainerPanel().getFnPanel().disableButton("r");

        myFrame.getContainerPanel().getPlayer1HandPanel().getpbet().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myFrame.getContainerPanel().getPlayer1HandPanel().getpbet().setEnabled(false);
                String ex = myFrame.getContainerPanel().getPlayer1HandPanel().getpbet().getSelectedItem().toString();
                //p1bet = (ex.equals("ALL IN")) ? Integer.parseInt(userCurChip[0]) : Integer.parseInt(ex);
                if(ex.equals("ALL IN")){
                    p1bet = Integer.parseInt(userCurChip[0]);
                }else{
                    p1bet = Integer.parseInt(ex);
                }
                if(Integer.parseInt(userCurChip[0]) < p1bet){
                    System.out.println("베팅하려는 금액이 보유중인 칩보다 많습니다.");
                }
                else{
                    userBettedChip[0] = Integer.toString(Integer.parseInt(userBettedChip[0]) + p1bet);
                    userCurChip[0] = Integer.toString(Integer.parseInt(userCurChip[0]) - p1bet);
                    //bet정보를 server로
                    String betchip = userBettedChip[0] +" "+ userCurChip[0];
                    myPacket p1Bettinginfo = new myPacket(userNames[0], "300", betchip);
                    SendMsg(p1Bettinginfo);
                    //기록을 모두에게 알려주기 위해서 server에 기록해야할 문장을 보냄
                    String record = userNames[0]+"님이 "+userBettedChip[0]+"만큼 베팅하셨고 현재 남은 금액은 "+userCurChip[0]+"입니다.";
                    myPacket p1BetRecord = new myPacket(userNames[0], "330", record);
                    SendMsg(p1BetRecord);
                }
            }
        });
        myFrame.getContainerPanel().getPlayer2HandPanel().getpbet().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myFrame.getContainerPanel().getPlayer2HandPanel().getpbet().setEnabled(false);
                String ex = myFrame.getContainerPanel().getPlayer2HandPanel().getpbet().getSelectedItem().toString();
                if(ex.equals("ALL IN")){
                    p2bet = Integer.parseInt(userCurChip[1]);
                }else{
                    p2bet = Integer.parseInt(ex);
                }
                if(Integer.parseInt(userCurChip[1]) < p2bet){
                    System.out.println("베팅하려는 금액이 보유중인 칩보다 많습니다.");
                }
                else{
                    userBettedChip[1] = Integer.toString(Integer.parseInt(userBettedChip[1]) + p2bet);
                    userCurChip[1] = Integer.toString(Integer.parseInt(userCurChip[1]) - p2bet);
                    //bet정보를 server로
                    String betchip = userBettedChip[1] + " " + userCurChip[1];
                    myPacket p2Bettinginfo = new myPacket(userNames[1], "300", betchip);
                    SendMsg(p2Bettinginfo);
                    //기록을 모두에게 알려주기 위해서 server에 기록해야할 문장을 보냄
                    String record = userNames[1]+"님이 "+userBettedChip[1]+"만큼 베팅하셨고 현재 남은 금액은 "+userCurChip[1]+"입니다.";
                    myPacket p2BetRecord = new myPacket(userNames[1], "330", record);
                    SendMsg(p2BetRecord);
                }
            }
        });
        myFrame.getContainerPanel().getPlayer3HandPanel().getpbet().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myFrame.getContainerPanel().getPlayer3HandPanel().getpbet().setEnabled(false);
                String ex = myFrame.getContainerPanel().getPlayer3HandPanel().getpbet().getSelectedItem().toString();
                if(ex.equals("ALL IN")){
                    p3bet = Integer.parseInt(userCurChip[2]);
                }else{
                    p3bet = Integer.parseInt(ex);
                }
                if(Integer.parseInt(userCurChip[2]) < p3bet){
                    System.out.println("베팅하려는 금액이 보유중인 칩보다 많습니다.");
                }
                else{
                    userBettedChip[2] = Integer.toString(Integer.parseInt(userBettedChip[2]) + p3bet);
                    userCurChip[2] = Integer.toString(Integer.parseInt(userCurChip[2]) - p3bet);
                    //bet정보를 server로
                    String betchip = userBettedChip[2] + " " + userCurChip[2];
                    myPacket p3Bettinginfo = new myPacket(userNames[2], "300", betchip);
                    SendMsg(p3Bettinginfo);
                    //기록을 모두에게 알려주기 위해서 server에 기록해야할 문장을 보냄
                    String record = userNames[2]+"님이 "+userBettedChip[2]+"만큼 베팅하셨고 현재 남은 금액은 "+userCurChip[2]+"입니다.";
                    myPacket p3BetRecord = new myPacket(userNames[2], "330", record);
                    SendMsg(p3BetRecord);
                }
            }
        });

        myFrame.getContainerPanel().getPlayer1HandPanel().getHit().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myFrame.getContainerPanel().getPlayer1HandPanel().disableButton("dd");
                myPacket p1hit = new myPacket(userNames[0], "501", "p1 hit");
                SendMsg(p1hit);
                //기록을 모두에게 알려주기 위해서 server에 기록해야할 문장을 보냄
                String hrecord1 = userNames[0]+"님이 hit 하셨습니다!";
                myPacket p1HitRecord = new myPacket(userNames[0], "330", hrecord1);
                SendMsg(p1HitRecord);
            }
        });
        myFrame.getContainerPanel().getPlayer2HandPanel().getHit().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myFrame.getContainerPanel().getPlayer2HandPanel().disableButton("dd");
                myPacket p2hit = new myPacket(userNames[1], "502", "p2 hit");
                SendMsg(p2hit);
                //기록을 모두에게 알려주기 위해서 server에 기록해야할 문장을 보냄
                String hrecord2 = userNames[1]+"님이 hit 하셨습니다!";
                myPacket p2HitRecord = new myPacket(userNames[1], "330", hrecord2);
                SendMsg(p2HitRecord);
            }
        });
        myFrame.getContainerPanel().getPlayer3HandPanel().getHit().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myFrame.getContainerPanel().getPlayer3HandPanel().disableButton("dd");
                myPacket p3hit = new myPacket(userNames[2], "503", "p3 hit");
                SendMsg(p3hit);
                //기록을 모두에게 알려주기 위해서 server에 기록해야할 문장을 보냄
                String hrecord3 = userNames[2]+"님이 hit 하셨습니다!";
                myPacket p3HitRecord = new myPacket(userNames[2], "330", hrecord3);
                SendMsg(p3HitRecord);
            }
        });

        myFrame.getContainerPanel().getPlayer1HandPanel().getStay().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                p1DisAllBtn();
                myPacket p1stay = new myPacket(userNames[0], "510", "p1 stay");
                SendMsg(p1stay);
                //기록을 모두에게 알려주기 위해서 server에 기록해야할 문장을 보냄
                String srecord1 = userNames[0]+"님이 stay 하셨습니다!";
                myPacket p1StayRecord = new myPacket(userNames[0], "330", srecord1);
                SendMsg(p1StayRecord);
            }
        });
        myFrame.getContainerPanel().getPlayer2HandPanel().getStay().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                p2DisAllBtn();
                myPacket p2stay = new myPacket(userNames[1], "510", "p2 stay");
                SendMsg(p2stay);
                //기록을 모두에게 알려주기 위해서 server에 기록해야할 문장을 보냄
                String srecord2 = userNames[1]+"님이 stay 하셨습니다!";
                myPacket p2StayRecord = new myPacket(userNames[1], "330", srecord2);
                SendMsg(p2StayRecord);
            }
        });
        myFrame.getContainerPanel().getPlayer3HandPanel().getStay().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                p3DisAllBtn();
                myPacket p3stay = new myPacket(userNames[2], "510", "p3 stay");
                SendMsg(p3stay);
                //기록을 모두에게 알려주기 위해서 server에 기록해야할 문장을 보냄
                String srecord3 = userNames[2]+"님이 stay 하셨습니다!";
                myPacket p3StayRecord = new myPacket(userNames[2], "330", srecord3);
                SendMsg(p3StayRecord);
            }
        });

        myFrame.getContainerPanel().getPlayer1HandPanel().getDie().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //카드 덮고
                myFrame.getContainerPanel().getPlayer1HandPanel().clearHand();
                //버튼 비활성화하고
                p1DisAllBtn();
                //카드값 지우고
                myFrame.getContainerPanel().getPlayer1HandPanel().getpValue().setText("현재 턴에 게임 참가가 불가능합니다.");
                //베팅값지우고
                myFrame.getContainerPanel().getPlayer1HandPanel().getpBettedChip().setText("걸려있는 칩 수 : 0");
                //서버에 넘기고
                myPacket p1Die = new myPacket(userNames[0], "520", "p1 die");
                SendMsg(p1Die);
                //기록을 모두에게 알려주기 위해서 server에 기록해야할 문장을 보냄
                String drecord1 = userNames[0]+"님이 die 하셨습니다!";
                myPacket p1DeadRecord = new myPacket(userNames[0], "330", drecord1);
                SendMsg(p1DeadRecord);
            }
        });
        myFrame.getContainerPanel().getPlayer2HandPanel().getDie().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myFrame.getContainerPanel().getPlayer2HandPanel().clearHand();
                p2DisAllBtn();
                myFrame.getContainerPanel().getPlayer2HandPanel().getpValue().setText("햔제 턴에 게임 참가가 불가능합니다.");
                myFrame.getContainerPanel().getPlayer2HandPanel().getpBettedChip().setText("걸려있는 칩 수 : 0");
                myPacket p2Die = new myPacket(userNames[1], "520", "p2 die");
                SendMsg(p2Die);
                //기록을 모두에게 알려주기 위해서 server에 기록해야할 문장을 보냄
                String drecord2 = userNames[1]+"님이 die 하셨습니다!";
                myPacket p2DeadRecord = new myPacket(userNames[1], "330", drecord2);
                SendMsg(p2DeadRecord);
            }
        });
        myFrame.getContainerPanel().getPlayer3HandPanel().getDie().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                p3DisAllBtn();
                myFrame.getContainerPanel().getPlayer3HandPanel().getpValue().setText("햔제 턴에 게임 참가가 불가능합니다.");
                myFrame.getContainerPanel().getPlayer3HandPanel().getpBettedChip().setText("걸려있는 칩 수 : 0");
                myPacket p3Die = new myPacket(userNames[2], "520", "p3 die");
                SendMsg(p3Die);
                //기록을 모두에게 알려주기 위해서 server에 기록해야할 문장을 보냄
                String drecord3 = userNames[2]+"님이 die 하셨습니다!";
                myPacket p3DeadRecord = new myPacket(userNames[2], "330", drecord3);
                SendMsg(p3DeadRecord);
            }
        });

        myFrame.getContainerPanel().getPlayer1HandPanel().getDd().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myFrame.getContainerPanel().getPlayer1HandPanel().getpbet().setEnabled(true);
            }
        });
        myFrame.getContainerPanel().getPlayer2HandPanel().getDd().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myFrame.getContainerPanel().getPlayer2HandPanel().getpbet().setEnabled(true);
            }
        });
        myFrame.getContainerPanel().getPlayer3HandPanel().getDd().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myFrame.getContainerPanel().getPlayer3HandPanel().getpbet().setEnabled(true);
            }
        });
        myFrame.getContainerPanel().getFnPanel().getRestart().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myPacket ready = new myPacket(playername, "990", playername+" ready for next");
                SendMsg(ready);
                myFrame.getContainerPanel().getFnPanel().disableButton("r");
            }
        });



        try{
            socket = new Socket(ip_addr, Integer.parseInt(port_no));

            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(socket.getInputStream());

            myPacket obcm = new myPacket(username, "100", "hello");
            SendMsg(obcm);

            ListenNetwork net = new ListenNetwork();
            net.start();
        }catch (NumberFormatException | IOException e){
            e.printStackTrace();

        }
    }

    public void startGame() {
        userBettedChip[0]="1";
        int p1b = Integer.parseInt(userCurChip[0]); p1b--;
        userCurChip[0] = Integer.toString(p1b);
        myFrame.getContainerPanel().getPlayer1HandPanel().getPBettedChip().setText("걸려있는 칩 수 : "+userBettedChip[0]);
        myFrame.getContainerPanel().getPlayer1HandPanel().getPcurChip().setText("현재 보유 칩수 : "+userCurChip[0]);
        if (userCurChip[0].equals("0")) {
            //모든 칩이 다 없어진다면
            System.out.println("No chip to play game!");
            exit(0);
        }
        userBettedChip[1]="1";
        int p2b = Integer.parseInt(userCurChip[1]); p2b--;
        userCurChip[1] = Integer.toString(p2b);
        myFrame.getContainerPanel().getPlayer2HandPanel().getPBettedChip().setText("걸려있는 칩 수 : "+userBettedChip[1]);
        myFrame.getContainerPanel().getPlayer2HandPanel().getPcurChip().setText("현재 보유 칩수 : "+userCurChip[1]);
        if (userCurChip[1].equals("0")) {
            //모든 칩이 다 없어진다면
            System.out.println("No chip to play game!");
            exit(0);
        }
        userBettedChip[2]="1";
        int p3b = Integer.parseInt(userCurChip[2]); p3b--;
        userCurChip[2] = Integer.toString(p3b);
        myFrame.getContainerPanel().getPlayer3HandPanel().getPBettedChip().setText("걸려있는 칩 수 : "+userBettedChip[2]);
        myFrame.getContainerPanel().getPlayer3HandPanel().getPcurChip().setText("현재 보유 칩수 : "+userCurChip[2]);
        if (userCurChip[2].equals("0")) {
            //모든 칩이 다 없어진다면
            System.out.println("No chip to play game!");
            exit(0);
        }

        myFrame.getContainerPanel().getPlayer1HandPanel().disableButton("h");
        myFrame.getContainerPanel().getPlayer2HandPanel().disableButton("h");
        myFrame.getContainerPanel().getPlayer3HandPanel().disableButton("h");
        myFrame.getContainerPanel().getPlayer1HandPanel().disableButton("s");
        myFrame.getContainerPanel().getPlayer2HandPanel().disableButton("s");
        myFrame.getContainerPanel().getPlayer3HandPanel().disableButton("s");
        deck.populateDeck(3);
        deckNum = 3;
        firstDraw();
    }
    
    public void mainGame() {
        //이렇게 사용하면 플레이어가 받은 카드를 계산해서 출력되는거까지는 성공됨
        myFrame.getContainerPanel().getPlayer1HandPanel().setHandValue("p", pHand.getHandValue());
        myFrame.getContainerPanel().getPlayer2HandPanel().setHandValue("p2", p2Hand.getHandValue());
        myFrame.getContainerPanel().getPlayer3HandPanel().setHandValue("p3", p3Hand.getHandValue());
        myFrame.getContainerPanel().getDHandPanel().setHandValue("c", dHand.getHandValue());

    }
    
    public void firstDraw() {
        if(playername.equals(userNames[0])){
            myFrame.getContainerPanel().getPlayer1HandPanel().enableButton("h");
            myFrame.getContainerPanel().getPlayer1HandPanel().enableButton("s");
            myFrame.getContainerPanel().getPlayer1HandPanel().enableButton("d");
            myFrame.getContainerPanel().getPlayer1HandPanel().enableButton("dd");
        }else if(playername.equals(userNames[1])){
            myFrame.getContainerPanel().getPlayer2HandPanel().enableButton("h");
            myFrame.getContainerPanel().getPlayer2HandPanel().enableButton("s");
            myFrame.getContainerPanel().getPlayer2HandPanel().enableButton("d");
            myFrame.getContainerPanel().getPlayer2HandPanel().enableButton("dd");
        }else if(playername.equals(userNames[2])){
            myFrame.getContainerPanel().getPlayer3HandPanel().enableButton("h");
            myFrame.getContainerPanel().getPlayer3HandPanel().enableButton("s");
            myFrame.getContainerPanel().getPlayer3HandPanel().enableButton("d");
            myFrame.getContainerPanel().getPlayer3HandPanel().enableButton("dd");
        }
    }

    public void p1DisAllBtn(){
        myFrame.getContainerPanel().getPlayer1HandPanel().disableButton("h");
        myFrame.getContainerPanel().getPlayer1HandPanel().disableButton("s");
        myFrame.getContainerPanel().getPlayer1HandPanel().disableButton("d");
        myFrame.getContainerPanel().getPlayer1HandPanel().disableButton("dd");
    }
    public void p1EnAllBtn(){
        myFrame.getContainerPanel().getPlayer1HandPanel().enableButton("h");
        myFrame.getContainerPanel().getPlayer1HandPanel().enableButton("s");
        myFrame.getContainerPanel().getPlayer1HandPanel().enableButton("d");
        myFrame.getContainerPanel().getPlayer1HandPanel().enableButton("dd");
    }

    public void p2DisAllBtn(){
        myFrame.getContainerPanel().getPlayer2HandPanel().disableButton("h");
        myFrame.getContainerPanel().getPlayer2HandPanel().disableButton("s");
        myFrame.getContainerPanel().getPlayer2HandPanel().disableButton("d");
        myFrame.getContainerPanel().getPlayer2HandPanel().disableButton("dd");
    }
    public void p2EnAllBtn(){
        myFrame.getContainerPanel().getPlayer2HandPanel().enableButton("h");
        myFrame.getContainerPanel().getPlayer2HandPanel().enableButton("s");
        myFrame.getContainerPanel().getPlayer2HandPanel().enableButton("d");
        myFrame.getContainerPanel().getPlayer2HandPanel().enableButton("dd");
    }

    public void p3DisAllBtn(){
        myFrame.getContainerPanel().getPlayer3HandPanel().disableButton("h");
        myFrame.getContainerPanel().getPlayer3HandPanel().disableButton("s");
        myFrame.getContainerPanel().getPlayer3HandPanel().disableButton("d");
        myFrame.getContainerPanel().getPlayer3HandPanel().disableButton("dd");
    }
    public void p3EnAllBtn(){
        myFrame.getContainerPanel().getPlayer3HandPanel().enableButton("h");
        myFrame.getContainerPanel().getPlayer3HandPanel().enableButton("s");
        myFrame.getContainerPanel().getPlayer3HandPanel().enableButton("d");
        myFrame.getContainerPanel().getPlayer3HandPanel().enableButton("dd");
    }
    
    public PlayerHand getPHand()
    {
        return pHand;
    }
    public DealerHand getDealerHand()
    {
        return dHand;
    }
    public PlayerHand getP2Hand() {return p2Hand;}
    public PlayerHand getP3Hand(){return p3Hand;}
    
    //////////////////////////////////////////////
    //Network 관련 함수들
    //////////////////////////////////////////////
    public myPacket ReadChatMsg() {
        Object obj = null;
        String msg = null;
        myPacket cm = new myPacket("", "", "");

        // Android와 호환성을 위해 각각의 Field를 따로따로 읽는다.

        try {
            obj = ois.readObject();
            cm.code = (String) obj;
            obj = ois.readObject();
            cm.UserName = (String) obj;
            obj = ois.readObject();
            cm.data = (String) obj;

        } catch (ClassNotFoundException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            try {
                oos.close();
                socket.close();
                ois.close();
                socket = null;
                return null;
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                try {
                    oos.close();
                    socket.close();
                    ois.close();
                } catch (IOException e2) {
                    // TODO Auto-generated catch block
                    e2.printStackTrace();
                }
                socket = null;
                return null;
            }
        }
        return cm;
    }

    // Server Message를 수신해서 화면에 표시
    class ListenNetwork extends Thread {
        public void run() {
            while (true) {
                myPacket cm = ReadChatMsg();
                if (cm==null)
                    break;
                if (socket == null)
                    break;
                switch (cm.code) {
                    case "100":
                        myFrame.getContainerPanel().getRecordsPanel().AppendText(cm.UserName + "님이 접속하셨습니다!");
                        break;
                    case "110":
                        //player의 접속 순서대로 차례대로 넘어온다.
                        //System.out.println(cm.data);
                        String players[] = {" ", " ", " "};
                        int playerNum = 0;
                        players= cm.data.split(" ");
                        userNames[0] = players[0];
                        userNames[1] = players[1];
                        userNames[2] = players[2];
                        for(int i=0; i<userNames.length; i++){
                            if(playername.equals(userNames[i])){
                                playerNum = i;
                            }
                        }
                        switch (playerNum){
                            case 0:
                                //p1이기 때문에 2,3의 버튼을 모두 비활성화
                                p2DisAllBtn();p3DisAllBtn();
                                myFrame.getContainerPanel().getPlayer1HandPanel().getIdent().setBackground(Color.YELLOW);
                                myFrame.getContainerPanel().getPlayer1HandPanel().getIdent().setForeground(Color.BLACK);
                                myFrame.getContainerPanel().getPlayer1HandPanel().getIdent().setOpaque(true);
                                break;
                            case 1:
                                //p2이기 때문에 1,3의 버튼을 모두 비활성화
                                p1DisAllBtn();p3DisAllBtn();
                                myFrame.getContainerPanel().getPlayer2HandPanel().getIdent().setBackground(Color.YELLOW);
                                myFrame.getContainerPanel().getPlayer2HandPanel().getIdent().setForeground(Color.BLACK);
                                myFrame.getContainerPanel().getPlayer2HandPanel().getIdent().setOpaque(true);
                                break;
                            case 2:
                                //p3이기 때문에 1,2의 버튼을 모두 비활성화
                                p1DisAllBtn();p2DisAllBtn();
                                myFrame.getContainerPanel().getPlayer3HandPanel().getIdent().setBackground(Color.YELLOW);
                                myFrame.getContainerPanel().getPlayer3HandPanel().getIdent().setForeground(Color.BLACK);
                                myFrame.getContainerPanel().getPlayer3HandPanel().getIdent().setOpaque(true);
                                break;
                        }
                        myFrame.getContainerPanel().getPlayer1HandPanel().getIdent().setText(userNames[0]);
                        myFrame.getContainerPanel().getPlayer2HandPanel().getIdent().setText(userNames[1]);
                        myFrame.getContainerPanel().getPlayer3HandPanel().getIdent().setText(userNames[2]);
                        myFrame.getContainerPanel().getFnPanel().getDscore().setText("Dealer");
                        myFrame.getContainerPanel().getFnPanel().getP1score().setText("Player1 : "+userNames[0]);
                        myFrame.getContainerPanel().getFnPanel().getP2score().setText("Player2 : "+userNames[1]);
                        myFrame.getContainerPanel().getFnPanel().getP3score().setText("Player3 : "+userNames[2]);

                        break;
                    case "200": // chat message
                        //AppendText(msg);
                        //System.out.println(cm.data);
                        break;
                    case "210":
                        String tdata = cm.data;
                        String[] tmpcard = tdata.split(" ");
                        int index = 0;
                        if(userNames[0].equals(cm.UserName)){
                            Enums.Suit s = Enums.Suit.valueOf(tmpcard[0]);
                            Enums.Value v = Enums.Value.valueOf(tmpcard[1]);
                            Card card = new Card(s,v);
                            pHand.addCard(card);
                            index = pHand.getCards().size() - 1 ;
                            myFrame.getContainerPanel().getPlayer1HandPanel().displayCard("p", index);

                        }else if(userNames[1].equals(cm.UserName)){
                            Enums.Suit s = Enums.Suit.valueOf(tmpcard[0]);
                            Enums.Value v = Enums.Value.valueOf(tmpcard[1]);
                            Card card = new Card(s,v);
                            p2Hand.addCard(card);
                            index = p2Hand.getCards().size() - 1 ;
                            myFrame.getContainerPanel().getPlayer2HandPanel().displayCard("p2", index);

                        }else if(userNames[2].equals(cm.UserName)){
                            Enums.Suit s = Enums.Suit.valueOf(tmpcard[0]);
                            Enums.Value v = Enums.Value.valueOf(tmpcard[1]);
                            Card card = new Card(s,v);
                            p3Hand.addCard(card);
                            index = p3Hand.getCards().size() - 1 ;
                            myFrame.getContainerPanel().getPlayer3HandPanel().displayCard("p3", index);
                        }else if("d".equals(cm.UserName)){
                            Enums.Suit s = Enums.Suit.valueOf(tmpcard[0]);
                            Enums.Value v = Enums.Value.valueOf(tmpcard[1]);
                            Card card = new Card(s,v);
                            dHand.addCard(card);
                            index = dHand.getCards().size() - 1;
                            myFrame.getContainerPanel().getDHandPanel().displayCard("d", index);
                            myPacket fd = new myPacket(playername, "215", "cardSetOK");
                            SendMsg(fd);
                        }else if("da".equals(cm.UserName)){
                            if(dHand.getCards().size() <= 5){
                                Enums.Suit s = Enums.Suit.valueOf(tmpcard[0]);
                                Enums.Value v = Enums.Value.valueOf(tmpcard[1]);
                                Card card = new Card(s,v);
                                dHand.addCard(card);
                                index = dHand.getCards().size()-1;
                                myFrame.getContainerPanel().getDHandPanel().displayCard("d", index);
                                myFrame.getContainerPanel().getDHandPanel().setHandValue("d", dHand.getHandValue());
                                if(dHand.getHandValue()>21){
                                    myFrame.getContainerPanel().getDHandPanel().getdValue().setText("Current value : "+dHand.getHandValue() + " / BUST");
                                    myFrame.getContainerPanel().getRecordsPanel().AppendText("Dealer는 BUST되었습니다!");
                                }else{
                                    myFrame.getContainerPanel().getDHandPanel().getdValue().setText("Current value : "+dHand.getHandValue());
                                }

                            }
                        }
                        myFrame.getContainerPanel().getPlayer1HandPanel().setHandValue("p", pHand.getHandValue());
                        myFrame.getContainerPanel().getPlayer2HandPanel().setHandValue("p2", p2Hand.getHandValue());
                        myFrame.getContainerPanel().getPlayer3HandPanel().setHandValue("p3", p3Hand.getHandValue());
                        myFrame.getContainerPanel().getDHandPanel().setHandValue("d", dHand.getHandValue());
                        break;

                    case "300":
                        String tmpdata = cm.data;
                        String[] tmp = tmpdata.split(" ");
                        if(userNames[0].equals(cm.UserName)){
                            userBettedChip[0] = tmp[0];
                            userCurChip[0] = tmp[1];
                            myFrame.getContainerPanel().getPlayer1HandPanel().getpbet().setEnabled(false);
                            myFrame.getContainerPanel().getPlayer1HandPanel().getPBettedChip().setText("걸려있는 칩 수 : "+userBettedChip[0]);
                            myFrame.getContainerPanel().getPlayer1HandPanel().getPcurChip().setText("현재 보유 칩 수 : "+userCurChip[0]);
                            p1EnAllBtn();
                        }else if(userNames[1].equals(cm.UserName)){
                            userBettedChip[1] = tmp[0];
                            userCurChip[1] = tmp[1];
                            myFrame.getContainerPanel().getPlayer2HandPanel().getpbet().setEnabled(false);
                            myFrame.getContainerPanel().getPlayer2HandPanel().getPBettedChip().setText("걸려있는 칩 수 : "+userBettedChip[1]);
                            myFrame.getContainerPanel().getPlayer2HandPanel().getPcurChip().setText("현재 보유 칩수 : "+userCurChip[1]);
                            p2EnAllBtn();
                        }else if(userNames[2].equals(cm.UserName)){
                            userBettedChip[2] = tmp[0];
                            userCurChip[2] = tmp[1];
                            myFrame.getContainerPanel().getPlayer3HandPanel().getpbet().setEnabled(false);
                            myFrame.getContainerPanel().getPlayer3HandPanel().getPBettedChip().setText("걸려있는 칩 수 : "+userBettedChip[2]);
                            myFrame.getContainerPanel().getPlayer3HandPanel().getPcurChip().setText("현재 보유 칩수 : "+userCurChip[2]);
                            p3EnAllBtn();
                            myPacket fd = new myPacket(playername, "310", "betOK");
                            SendMsg(fd);
                        }
                        break;

                    case "330":
                        myFrame.getContainerPanel().getRecordsPanel().AppendText(cm.data);
                        if(cm.data.startsWith("C")){
                            if(userNames[0].equals(cm.UserName)){
                                myFrame.getContainerPanel().getPlayer1HandPanel().getpValue().setText("Current value: "+pHand.getHandValue()+" / Stay");
                            }else if(userNames[1].equals(cm.UserName)){
                                myFrame.getContainerPanel().getPlayer2HandPanel().getpValue().setText("Current value: "+p2Hand.getHandValue()+" / Stay");
                            }else if(userNames[2].equals(cm.UserName)){
                                myFrame.getContainerPanel().getPlayer3HandPanel().getpValue().setText("Current value: "+p3Hand.getHandValue()+" / Stay");
                            }
                        }
                        break;

                    case "390":
                        String lastdata = cm.data;
                        String[] lastbets = lastdata.split(" ");
                        for (int i = 0; i < userCurChip.length; i++) {
                            userCurChip[i] = lastbets[i];
                        }
                        myFrame.getContainerPanel().getPlayer1HandPanel().getPcurChip().setText("현재 보유 칩수 : "+userCurChip[0]);
                        myFrame.getContainerPanel().getPlayer2HandPanel().getPcurChip().setText("현재 보유 칩수 : "+userCurChip[1]);
                        myFrame.getContainerPanel().getPlayer3HandPanel().getPcurChip().setText("현재 보유 칩수 : "+userCurChip[2]);
                        break;

                    case "500":
                        if(userNames[0].equals(cm.UserName)){
                            myFrame.getContainerPanel().getPlayer1HandPanel().getDd().setVisible(true);
                            p1EnAllBtn();
                        }else if(userNames[1].equals(cm.UserName)){
                            myFrame.getContainerPanel().getPlayer2HandPanel().getDd().setVisible(true);
                            p2EnAllBtn();
                        }else if(userNames[2].equals(cm.UserName)){
                            myFrame.getContainerPanel().getPlayer3HandPanel().getDd().setVisible(true);
                            p3EnAllBtn();
                        }
                        break;
                    case "501":
                        String cdata = cm.data;
                        String[] tmpc = cdata.split(" ");
                        int p1index = 0;
                        Enums.Suit s = Enums.Suit.valueOf(tmpc[0]);
                        Enums.Value v = Enums.Value.valueOf(tmpc[1]);
                        //System.out.println(s + "/" + v);
                        Card nc = new Card(s,v);
                        pHand.addCard(nc);
                        p1index = pHand.getCards().size() - 1;
                        myFrame.getContainerPanel().getPlayer1HandPanel().displayCard("p", p1index);
                        myFrame.getContainerPanel().getPlayer1HandPanel().setHandValue("p", pHand.getHandValue());
                        //myFrame.getContainerPanel().getRecordsPanel().AppendText(userNames[0]+"님이 hit 하였습니다.");
                        //여기에서 카드값을 계산해서 bust, 블랙잭, 카드5장 처리
                        if(pHand.getHandValue() > 21){
                            //bust
                            //myFrame.getContainerPanel().getPlayer1HandPanel().getpValue().setText("Current value: "+pHand.getHandValue()+" / BUST!");
                            p1DisAllBtn();
                            myFrame.getContainerPanel().getRecordsPanel().AppendText(userNames[0]+"님이 BUST했습니다.");
                            myPacket p1BDie = new myPacket(userNames[0], "520", "bust die");
                            SendMsg(p1BDie);
                        }
                        else if(pHand.getHandValue() == 21){
                            //blackjack
                            //myFrame.getContainerPanel().getPlayer1HandPanel().getpValue().setText("Current value: "+pHand.getHandValue()+" / BlackJack!");
                            p1DisAllBtn();
                            myFrame.getContainerPanel().getRecordsPanel().AppendText(userNames[0]+"님이 BlackJack입니다!!!");
                            myPacket p1BJ = new myPacket(userNames[0], "510", "p1bj");
                            SendMsg(p1BJ);
                        }
                        else if(pHand.getCards().size() == 5 && pHand.getHandValue()<21){
                            //5Card!
                            //myFrame.getContainerPanel().getPlayer1HandPanel().getpValue().setText("Current value: "+pHand.getHandValue()+" / Card MAX!");
                            myFrame.getContainerPanel().getPlayer1HandPanel().disableButton("h");
                            myFrame.getContainerPanel().getPlayer1HandPanel().disableButton("dd");
                        }
                        break;
                    case "502":
                        String c2data = cm.data;
                        String[] tmpc2 = c2data.split(" ");
                        int p2index = 0;
                        Enums.Suit s2 = Enums.Suit.valueOf(tmpc2[0]);
                        Enums.Value v2 = Enums.Value.valueOf(tmpc2[1]);
                        //System.out.println(s2 + "/" + v2);
                        Card nc2 = new Card(s2,v2);
                        p2Hand.addCard(nc2);
                        p2index = p2Hand.getCards().size() - 1;
                        myFrame.getContainerPanel().getPlayer2HandPanel().displayCard("p2", p2index);
                        myFrame.getContainerPanel().getPlayer2HandPanel().setHandValue("p2", p2Hand.getHandValue());
                        //myFrame.getContainerPanel().getRecordsPanel().AppendText(userNames[1]+"님이 hit 하였습니다.");
                        //여기에서 카드값을 계산해서 bust, 블랙잭, 카드5장 처리
                        if(p2Hand.getHandValue() > 21){
                            //bust
                            myFrame.getContainerPanel().getPlayer2HandPanel().getpValue().setText("Current value: "+p2Hand.getHandValue()+" / BUST!");
                            p2DisAllBtn();
                            myFrame.getContainerPanel().getRecordsPanel().AppendText(userNames[1]+"님이 BUST했습니다.");
                            myPacket p2BDie = new myPacket(userNames[1], "520", "bust die");
                            SendMsg(p2BDie);
                        }
                        else if(p2Hand.getHandValue() == 21){
                            //blackjack
                            //myFrame.getContainerPanel().getPlayer2HandPanel().getpValue().setText("Current value: "+p2Hand.getHandValue()+" / BlackJack!");
                            p2DisAllBtn();
                            myFrame.getContainerPanel().getRecordsPanel().AppendText(userNames[1]+"님이 BlackJack입니다!!!");
                            myPacket p2BJ = new myPacket(userNames[1], "510", "bj");
                            SendMsg(p2BJ);
                        }
                        else if(p2Hand.getCards().size() == 5 && p2Hand.getHandValue()<21){
                            //5Card!
                            myFrame.getContainerPanel().getPlayer2HandPanel().getpValue().setText("Current value: "+p2Hand.getHandValue()+" / Card MAX!");
                            myFrame.getContainerPanel().getPlayer2HandPanel().disableButton("h");
                            myFrame.getContainerPanel().getPlayer2HandPanel().disableButton("dd");
                        }
                        break;
                    case "503":
                        String c3data = cm.data;
                        String[] tmpc3 = c3data.split(" ");
                        int p3index = 0;
                        Enums.Suit s3 = Enums.Suit.valueOf(tmpc3[0]);
                        Enums.Value v3 = Enums.Value.valueOf(tmpc3[1]);
                        //System.out.println(s3 + "/" + v3);
                        Card nc3 = new Card(s3,v3);
                        p3Hand.addCard(nc3);
                        p3index = p3Hand.getCards().size() - 1;
                        myFrame.getContainerPanel().getPlayer3HandPanel().displayCard("p3", p3index);
                        myFrame.getContainerPanel().getPlayer3HandPanel().setHandValue("p3", p3Hand.getHandValue());
                        //myFrame.getContainerPanel().getRecordsPanel().AppendText(userNames[2]+"님이 hit 하였습니다.");
                        //여기에서 카드값을 계산해서 bust, 블랙잭, 카드5장 처리
                        if(p3Hand.getHandValue() > 21){
                            //bust
                            myFrame.getContainerPanel().getPlayer3HandPanel().getpValue().setText("Current value: "+p3Hand.getHandValue()+" / BUST!");
                            p3DisAllBtn();
                            myFrame.getContainerPanel().getRecordsPanel().AppendText(userNames[2]+"님이 BUST했습니다.");
                            myPacket p3BDie = new myPacket(userNames[2], "520", "bust die");
                            SendMsg(p3BDie);
                        }
                        else if(p3Hand.getHandValue() == 21){
                            //blackjack
                            //myFrame.getContainerPanel().getPlayer3HandPanel().getpValue().setText("Current value: "+p3Hand.getHandValue()+" / BlackJack!");
                            p3DisAllBtn();
                            myFrame.getContainerPanel().getRecordsPanel().AppendText(userNames[2]+"님이 BlackJack입니다!!!");
                            myPacket p3BJ = new myPacket(userNames[2], "510", "bj");
                            SendMsg(p3BJ);
                        }
                        else if(p3Hand.getCards().size() == 5 && p3Hand.getHandValue()<21){
                            //5Card!
                            myFrame.getContainerPanel().getPlayer3HandPanel().getpValue().setText("Current value: "+p3Hand.getHandValue()+" / Card MAX!");
                            myFrame.getContainerPanel().getPlayer3HandPanel().disableButton("h");
                            myFrame.getContainerPanel().getPlayer3HandPanel().disableButton("dd");
                        }
                        break;
                    case "510":
                        if(cm.data.equals("p1Stay")){
                            p1DisAllBtn();
                            myFrame.getContainerPanel().getPlayer1HandPanel().getpValue().setText("Current value: "+pHand.getHandValue()+" / Stay");
                        }else if(cm.data.equals("p2Stay")){
                            p2DisAllBtn();
                            myFrame.getContainerPanel().getPlayer2HandPanel().getpValue().setText("Current value: "+p2Hand.getHandValue()+" / Stay");
                        }else if(cm.data.equals("p3Stay")){
                            p3DisAllBtn();
                            myFrame.getContainerPanel().getPlayer3HandPanel().getpValue().setText("Current value: "+p3Hand.getHandValue()+" / Stay");
                        }else if(cm.data.equals("p1bj")){
                            p1DisAllBtn();
                            myFrame.getContainerPanel().getPlayer1HandPanel().getpValue().setText("Current value: "+pHand.getHandValue()+" / Blackjack");
                        }else if(cm.data.equals("p2bj")){
                            p2DisAllBtn();
                            myFrame.getContainerPanel().getPlayer2HandPanel().getpValue().setText("Current value: "+p2Hand.getHandValue()+" / Blackjack");
                        }else if(cm.data.equals("p3bj")){
                            p3DisAllBtn();
                            myFrame.getContainerPanel().getPlayer3HandPanel().getpValue().setText("Current value: "+p3Hand.getHandValue()+" / Blackjack");
                        }

                        break;
                    case "530":
                        if(cm.data.equals("p1Dead")){
                            p1DisAllBtn();
                            myFrame.getContainerPanel().getPlayer1HandPanel().getpValue().setText("현재 턴에 게임 참가가 불가능합니다.");
                            myFrame.getContainerPanel().getPlayer1HandPanel().getpBettedChip().setText("걸려있는 칩 수 : 0");
                            myFrame.getContainerPanel().getPlayer1HandPanel().clearHand();
                        }else if(cm.data.equals("p2Dead")){
                            p2DisAllBtn();
                            myFrame.getContainerPanel().getPlayer2HandPanel().getpValue().setText("현재 턴에 게임 참가가 불가능합니다.");
                            myFrame.getContainerPanel().getPlayer2HandPanel().getpBettedChip().setText("걸려있는 칩 수 : 0");
                            myFrame.getContainerPanel().getPlayer2HandPanel().clearHand();
                        }else if(cm.data.equals("p3Dead")){
                            p3DisAllBtn();
                            myFrame.getContainerPanel().getPlayer3HandPanel().getpValue().setText("현재 턴에 게임 참가가 불가능합니다.");
                            myFrame.getContainerPanel().getPlayer3HandPanel().getpBettedChip().setText("걸려있는 칩 수 : 0");
                            myFrame.getContainerPanel().getPlayer3HandPanel().clearHand();
                        }else if(cm.data.equals("p1bust")){
                            p1DisAllBtn();
                            myFrame.getContainerPanel().getPlayer1HandPanel().getpValue().setText("Current value: "+pHand.getHandValue()+" / BUST!");
                            myFrame.getContainerPanel().getPlayer1HandPanel().getpBettedChip().setText("걸려있는 칩 수 : 0");
                        }else if(cm.data.equals("p2bust")){
                            p2DisAllBtn();
                            myFrame.getContainerPanel().getPlayer2HandPanel().getpValue().setText("Current value: "+p2Hand.getHandValue()+" / BUST!");
                            myFrame.getContainerPanel().getPlayer2HandPanel().getpBettedChip().setText("걸려있는 칩 수 : 0");
                        }else if(cm.data.equals("p3bust")){
                            p3DisAllBtn();
                            myFrame.getContainerPanel().getPlayer3HandPanel().getpValue().setText("Current value: "+p3Hand.getHandValue()+" / BUST!");
                            myFrame.getContainerPanel().getPlayer3HandPanel().getpBettedChip().setText("걸려있는 칩 수 : 0");
                        }
                        break;
                    case "601":
                        myFrame.getContainerPanel().getFnPanel().enableButton("r");
                        String ldata = cm.data;
                        String[] ltmp = ldata.split(" ");
                        //myFrame.getContainerPanel().getDHandPanel().setHandValue("d", dHand.getHandValue());
                        if(userNames[0].equals(cm.UserName)){
                            userBettedChip[0] = ltmp[0];
                            myFrame.getContainerPanel().getPlayer1HandPanel().getpBettedChip().setText("걸려있는 칩 수 : "+ userBettedChip[0]);
                            myFrame.getContainerPanel().getRecordsPanel().AppendText(cm.UserName+"님은 패배! 베팅한 칩을 모두 잃습니다");
                        }
                        if(userNames[1].equals(cm.UserName)){
                            userBettedChip[1] = ltmp[0];
                            myFrame.getContainerPanel().getPlayer2HandPanel().getpBettedChip().setText("걸려있는 칩 수 : "+ userBettedChip[1]);
                            myFrame.getContainerPanel().getRecordsPanel().AppendText(cm.UserName+"님은 패배! 베팅한 칩을 모두 잃습니다");
                        }
                        if(userNames[2].equals(cm.UserName)){
                            userBettedChip[2] = ltmp[0];
                            myFrame.getContainerPanel().getPlayer3HandPanel().getpBettedChip().setText("걸려있는 칩 수 : "+ userBettedChip[2]);
                            myFrame.getContainerPanel().getRecordsPanel().AppendText(cm.UserName+"님은 패배! 베팅한 칩을 모두 잃습니다");
                        }
                        break;
                    case "602":
                        myFrame.getContainerPanel().getFnPanel().enableButton("r");
                        String ddata = cm.data;
                        String[] dtmp = ddata.split(" ");
                        //myFrame.getContainerPanel().getDHandPanel().setHandValue("d", dHand.getHandValue());
                        if(userNames[0].equals(cm.UserName)){
                            userBettedChip[0] = dtmp[0];
                            myFrame.getContainerPanel().getPlayer1HandPanel().getpBettedChip().setText("걸려있는 칩 수 : "+ userBettedChip[0]);
                            userCurChip[0] = dtmp[1];
                            myFrame.getContainerPanel().getPlayer1HandPanel().getPcurChip().setText("현재 보유 칩수 : "+userCurChip[0]);
                            myFrame.getContainerPanel().getRecordsPanel().AppendText(cm.UserName+"님은 동점! 베팅한 칩을 돌려받습니다");
                        }
                        if(userNames[1].equals(cm.UserName)){
                            userBettedChip[1] = dtmp[0];
                            myFrame.getContainerPanel().getPlayer2HandPanel().getpBettedChip().setText("걸려있는 칩 수 : "+ userBettedChip[1]);
                            userCurChip[1] =dtmp[1];
                            myFrame.getContainerPanel().getPlayer2HandPanel().getPcurChip().setText("현재 보유 칩수 : "+userCurChip[1]);
                            myFrame.getContainerPanel().getRecordsPanel().AppendText(cm.UserName+"님은 동점! 베팅한 칩을 돌려받습니다");
                        }
                        if(userNames[2].equals(cm.UserName)){
                            userBettedChip[2] = dtmp[0];
                            myFrame.getContainerPanel().getPlayer3HandPanel().getpBettedChip().setText("걸려있는 칩 수 : "+ userBettedChip[2]);
                            userCurChip[2] = dtmp[1];
                            myFrame.getContainerPanel().getPlayer3HandPanel().getPcurChip().setText("현재 보유 칩수 : "+userCurChip[2]);
                            myFrame.getContainerPanel().getRecordsPanel().AppendText(cm.UserName+"님은 동점! 베팅한 칩을 돌려받습니다");
                        }
                        break;
                    case "603":
                        myFrame.getContainerPanel().getFnPanel().enableButton("r");
                        String wdata = cm.data;
                        String[] wtmp = wdata.split(" ");
                        //myFrame.getContainerPanel().getDHandPanel().setHandValue("d", dHand.getHandValue());
                        if(userNames[0].equals(cm.UserName)){
                            userBettedChip[0] = wtmp[0];
                            myFrame.getContainerPanel().getPlayer1HandPanel().getpBettedChip().setText("걸려있는 칩 수 : " + userBettedChip[0]);
                            userCurChip[0] = wtmp[1];
                            myFrame.getContainerPanel().getPlayer1HandPanel().getPcurChip().setText("현재 보유 칩수 : "+userCurChip[0]);
                            if(pHand.getHandValue() != 21){
                                myFrame.getContainerPanel().getRecordsPanel().AppendText(cm.UserName+"님은 승리! 베팅한 칩의 두배를 돌려받습니다");
                            }
                        }
                        if(userNames[1].equals(cm.UserName)){
                            userBettedChip[1] = wtmp[0];
                            myFrame.getContainerPanel().getPlayer2HandPanel().getpBettedChip().setText("걸려있는 칩 수 : " + userBettedChip[1]);
                            userCurChip[1] = wtmp[1];
                            myFrame.getContainerPanel().getPlayer2HandPanel().getPcurChip().setText("현재 보유 칩수 : "+userCurChip[1]);
                            if(p2Hand.getHandValue() != 21){
                                myFrame.getContainerPanel().getRecordsPanel().AppendText(cm.UserName+"님은 승리! 베팅한 칩의 두배를 돌려받습니다");
                            }
                        }
                        if(userNames[2].equals(cm.UserName)){
                            userBettedChip[2] = wtmp[0];
                            myFrame.getContainerPanel().getPlayer3HandPanel().getpBettedChip().setText("걸려있는 칩 수 : " + userBettedChip[2]);
                            userCurChip[2] = wtmp[1];
                            myFrame.getContainerPanel().getPlayer3HandPanel().getPcurChip().setText("현재 보유 칩수 : "+userCurChip[2]);
                            if(p3Hand.getHandValue() != 21){
                                myFrame.getContainerPanel().getRecordsPanel().AppendText(cm.UserName+"님은 승리! 베팅한 칩의 두배를 돌려받습니다");
                            }
                        }
                        break;
                    case "900":
                        myFrame.getContainerPanel().getRecordsPanel().AppendText(cm.data+"턴이 시작됩니다...");
                        break;
                    case "999":
                        myFrame.getContainerPanel().getRecordsPanel().AppendText(cm.data+"턴이 끝났습니다");
                        pHand.clearHand();
                        p2Hand.clearHand();
                        p3Hand.clearHand();
                        dHand.clearHand();
                        deck.clearDeck();
                        myFrame.getContainerPanel().getPlayer1HandPanel().clearHand();
                        myFrame.getContainerPanel().getPlayer2HandPanel().clearHand();
                        myFrame.getContainerPanel().getPlayer3HandPanel().clearHand();
                        myFrame.getContainerPanel().getDHandPanel().clearHand();
                        myFrame.getContainerPanel().getPlayer1HandPanel().getpValue().setText(" ");
                        myFrame.getContainerPanel().getPlayer2HandPanel().getpValue().setText(" ");
                        myFrame.getContainerPanel().getPlayer3HandPanel().getpValue().setText(" ");
                        myFrame.getContainerPanel().getDHandPanel().getdValue().setText(" ");
                        p1EnAllBtn();myFrame.getContainerPanel().getPlayer1HandPanel().getpbet().setEnabled(true);
                        p2EnAllBtn();myFrame.getContainerPanel().getPlayer2HandPanel().getpbet().setEnabled(true);
                        p3EnAllBtn();myFrame.getContainerPanel().getPlayer3HandPanel().getpbet().setEnabled(true);
                        for (int i = 0; i < userBettedChip.length; i++) {
                            userBettedChip[i]="0";
                        }
                        break;
                    case "404":
                        //cm.user가 칩이없는 플레이어임
                        //다른 플레이어들은 칩없플이 퇴장했다고 알려야한다.
                        //칩없플은 자동으로 퇴장해야한다.
                        if(userNames[0].equals(cm.UserName)){
                            //만약 칩없플이 플1이라면
                            myFrame.getContainerPanel().getRecordsPanel().AppendText(userNames[0]+"님의 칩이 다떨어졌음으로 강제종료되었습니다");
                            myFrame.getContainerPanel().getFnPanel().getP1score().setText("Player1 : ");
                            myFrame.getContainerPanel().getPlayer1HandPanel().getIdent().setText("");
                        }else if(userNames[1].equals(cm.UserName)){
                            //만약 칩없플이 플2이라면
                            myFrame.getContainerPanel().getRecordsPanel().AppendText(userNames[1]+"님의 칩이 다떨어졌음으로 강제종료되었습니다");
                            myFrame.getContainerPanel().getFnPanel().getP2score().setText("Player2 : ");
                            myFrame.getContainerPanel().getPlayer2HandPanel().getIdent().setText("");
                        }else if(userNames[2].equals(cm.UserName)){
                            //만약 칩없플이 플3이라면
                            myFrame.getContainerPanel().getRecordsPanel().AppendText(userNames[2]+"님의 칩이 다떨어졌음으로 강제종료되었습니다");
                            myFrame.getContainerPanel().getFnPanel().getP3score().setText("Player3 : ");
                            myFrame.getContainerPanel().getPlayer3HandPanel().getIdent().setText("");
                        }
                        if(playername.equals(cm.UserName)){
                            exit(0);
                        }
                }
            }
        }
    }

    // Windows 처럼 message 제외한 나머지 부분은 NULL 로 만들기 위한 함수
    public byte[] MakePacket(String msg) {
        byte[] packet = new byte[BUF_LEN];
        byte[] bb = null;
        int i;
        for (i = 0; i < BUF_LEN; i++)
            packet[i] = 0;
        try {
            bb = msg.getBytes("euc-kr");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            exit(0);
        }
        for (i = 0; i < bb.length; i++)
            packet[i] = bb[i];
        return packet;
    }

    // 하나의 Message 보내는 함수
    // Android와 호환성을 위해 code, UserName, data 모드 각각 전송한다.
    public void SendMsg(myPacket obj) {
        try {
            oos.writeObject(obj.code);
            oos.writeObject(obj.UserName);
            oos.writeObject(obj.data);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                oos.close();
                socket.close();
                ois.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    public void quitClient(String name, String code, String data){
        myPacket obcm = new myPacket(name, code, data);
        SendMsg(obcm);
        exit(0);
    }

    public String getPlayername(){
        return playername;
    }
    public String[] getAllNames(){return userNames;}

}
