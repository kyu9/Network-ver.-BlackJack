package blackjackgame.main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Main extends JFrame{

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextArea textArea;
    private JTextField txtPortNumber;

    private ServerSocket socket; 
    private Socket client_socket;
    private Vector UserVec = new Vector();
    public String[] userNames = {"", "", ""};
    public String[] userBettedChip = {"", "", ""};
    public String[] userCurChip = {"", "", ""};
    public String[] userTurnChoice = {"", "", ""};
    private static final int BUF_LEN = 128;
    public int betEnd=0;
    public int setEnd=0;
    public int turnEnd=0;
    public Game game;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Main frame = new Main();
                    frame.setVisible(true);
                    frame.setTitle("BlackJack Game");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Main(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 338, 440);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        contentPane.setBackground(new Color(22, 122, 9));

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(12, 10, 300, 298);
        contentPane.add(scrollPane);

        textArea = new JTextArea();
        textArea.setEditable(false);
        scrollPane.setViewportView(textArea);

        JLabel lblNewLabel = new JLabel("Port Number");
        lblNewLabel.setBounds(13, 318, 87, 26);
        lblNewLabel.setForeground(new Color(255,255,255));
        contentPane.add(lblNewLabel);

        txtPortNumber = new JTextField();
        txtPortNumber.setHorizontalAlignment(SwingConstants.CENTER);
        txtPortNumber.setText("30000");
        txtPortNumber.setBounds(112, 318, 199, 26);
        contentPane.add(txtPortNumber);
        txtPortNumber.setColumns(10);

        JButton btnServerStart = new JButton("Click to Start Server!!!");
        btnServerStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    socket = new ServerSocket(Integer.parseInt(txtPortNumber.getText()));
                } catch (NumberFormatException | IOException e1) {
                    e1.printStackTrace();
                }
                AppendText("BlackJack Server Running..");
                btnServerStart.setText("BlackJack Server Running..");
                btnServerStart.setEnabled(false); 
                txtPortNumber.setEnabled(false);
                AcceptServer accept_server = new AcceptServer();
                accept_server.start();

            }
        });
        btnServerStart.setBounds(12, 356, 300, 35);
        btnServerStart.setBackground(new Color(255,255,255));
        contentPane.add(btnServerStart);
    }

    class AcceptServer extends Thread {
        @SuppressWarnings("unchecked")
        public void run() {
            while (true) {
                try {
                    AppendText("Waiting new clients ...");
                    client_socket = socket.accept(); // accept가 일어나기 전까지는 무한 대기중
                    AppendText("새로운 참가자 from " + client_socket);
                    // User 당 하나씩 Thread 생성
                    UserService new_user = new UserService(client_socket);
                    UserVec.add(new_user); // 새로운 참가자 배열에 추가
                    new_user.start(); // 만든 객체의 스레드 실행
                    AppendText("현재 참가자 수 " + UserVec.size());
                } catch (IOException e) {
                    AppendText("accept() error");
                }
            }
        }
    }

    public void AppendText(String str) {
        textArea.append(str + "\n");
        textArea.setCaretPosition(textArea.getText().length());
    }

    public void AppendObject(myPacket msg) {
        textArea.append("code = " + msg.code + "\n");
        textArea.append("id = " + msg.UserName + "\n");
        textArea.append("data = " + msg.data + "\n");
        textArea.setCaretPosition(textArea.getText().length());
    }

    class UserService extends Thread {
        private InputStream is;
        private OutputStream os;
        private DataInputStream dis;
        private DataOutputStream dos;

        private ObjectInputStream ois;
        private ObjectOutputStream oos;

        private Socket client_socket;
        private Vector user_vc;
        public String UserName = "";

        public UserService(Socket client_socket) {
            this.client_socket = client_socket;
            this.user_vc = UserVec;
            try {
                oos = new ObjectOutputStream(client_socket.getOutputStream());
                oos.flush();
                ois = new ObjectInputStream(client_socket.getInputStream());
                game = new Game();
            } catch (Exception e) {
                AppendText("userService error");
            }
        }
        
        public void Login() {
            AppendText("새로운 참가자 " + UserName + " 입장.");
            WriteOne("Welcome to Java chat server\n");
            WriteOne(UserName + "님 환영합니다.\n"); // 연결된 사용자에게 정상접속을 알림
            String msg = "[" + UserName + "]님이 입장 하였습니다.\n";
            WriteOthers(msg); // 아직 user_vc에 새로 입장한 user는 포함되지 않았다.
        }

        public void Logout() {
            String msg = "[" + UserName + "]님이 퇴장 하였습니다.\n";
            UserVec.removeElement(this); // Logout한 현재 객체를 벡터에서 지운다
            WriteAll(msg); // 나를 제외한 다른 User들에게 전송
            this.client_socket = null;
            AppendText("사용자 " + "[" + UserName + "] 퇴장. 현재 참가자 수 " + UserVec.size());
        }

        public void nextTurn() {
            String userbets = userCurChip[0] + " " + userCurChip[1] + " " + userCurChip[2];
            myPacket playerBets = new myPacket("SERVER", "390", userbets);
            WriteAllObject(playerBets);
            for (int i = 0; i < userNames.length; i++) {
                 if(userCurChip[i].equals("0")){
                    myPacket playerExit = new myPacket(userNames[i], "404", userNames[i]+"강제퇴장!");
                    WriteAllObject(playerExit);
                 }

            }
            myPacket nextTurn = new myPacket("SERVER", "999", Integer.toString(game.getTurn()));
            WriteAllObject(nextTurn);
            userTurnChoice[0]="";userTurnChoice[1]="";userTurnChoice[2]="";
            game.getDealerHand().clearHand();
            game.getPHand().clearHand();
            game.getP2Hand().clearHand();
            game.getP3Hand().clearHand();
        }

        public void CardCalculate(){

            //딜러가 승리
            if(userTurnChoice[0].equals("d") && userTurnChoice[1].equals("d") && userTurnChoice[2].equals("d")){
                userBettedChip[0]="0";userBettedChip[1]="0";userBettedChip[2]="0";
                myPacket p1lose = new myPacket(userNames[0], "601", userBettedChip[0]+" "+userCurChip[0]);
                WriteAllObject(p1lose);
                myPacket p2lose = new myPacket(userNames[1], "601", userBettedChip[1]+" "+userCurChip[1]);
                WriteAllObject(p2lose);
                myPacket p3lose = new myPacket(userNames[2], "601", userBettedChip[2]+" "+userCurChip[2]);
                WriteAllObject(p3lose);
            }
            int beforedv = game.getDealerHand().getHandValue();
            int p1v = game.getPHand().getHandValue();
            int p2v = game.getP2Hand().getHandValue();
            int p3v = game.getP3Hand().getHandValue();

            //숫자가 21보다 낮고, 카드가 5장이 이하이고, 플레이어2명한테 값이 지고 있으면 자동으로 hit
            //지금 버스트한것들의 값도 비교해서 문제임
            if(userTurnChoice[0].equals("d")&&userTurnChoice[1].equals("d")){
                //플레이어2명이 버스트이면 굳이 딜러가 hit할필요가없음
            }else if(userTurnChoice[0].equals("d")&&userTurnChoice[2].equals("d")){
                //플레이어2명이 버스트이면 굳이 딜러가 hit할필요가없음
            }else if(userTurnChoice[1].equals("d")&&userTurnChoice[2].equals("d")){
                //플레이어2명이 버스트이면 굳이 딜러가 hit할필요가없음
            }else{
                //플레이어 한명만 버스트되었을때는 버스트안된 두명꺼보다 작을때만 hit
                if(userTurnChoice[0].equals("d")){
                    while(beforedv<p2v && beforedv<p3v){
                        if(game.getDealerHand().getHandValue()>=21) break;
                        if(game.getDealerHand().getCards().size() >= 5)break;
                        String dcAdd = game.drawCard("d");
                        myPacket dAddCard = new myPacket("da", "210", dcAdd);
                        WriteAllObject(dAddCard);
                    }
                }else if(userTurnChoice[1].equals("d")){
                    while(beforedv<p1v && beforedv<p3v){
                        if(game.getDealerHand().getHandValue()>=21) break;
                        if(game.getDealerHand().getCards().size() >= 5)break;
                        String dcAdd = game.drawCard("d");
                        myPacket dAddCard = new myPacket("da", "210", dcAdd);
                        WriteAllObject(dAddCard);
                    }
                }else if(userTurnChoice[2].equals("d")){
                    while(beforedv<p1v&&beforedv<p2v){
                        if(game.getDealerHand().getHandValue()>=21) break;
                        if(game.getDealerHand().getCards().size() >= 5)break;
                        String dcAdd = game.drawCard("d");
                        myPacket dAddCard = new myPacket("da", "210", dcAdd);
                        WriteAllObject(dAddCard);
                    }
                }
                //모두가 stay일때비교
                else if(userTurnChoice[0].equals("s")&&userTurnChoice[1].equals("s")&&userTurnChoice[2].equals("s")){
                    while ((beforedv<p1v&&beforedv<p2v)||(beforedv<p1v&&beforedv<p3v)||(beforedv<p2v&&beforedv<p3v)||(beforedv<p1v&&beforedv<p2v&&beforedv<p3v)) {
                        if(game.getDealerHand().getHandValue()>=21) break;
                        if(game.getDealerHand().getCards().size() >= 5)break;
                        String dcAdd = game.drawCard("d");
                        myPacket dAddCard = new myPacket("da", "210", dcAdd);
                        WriteAllObject(dAddCard);
                    }
                }
            }
            if (game.getDealerHand().getHandValue() > 21) {
                //딜러 bust
                userCurChip[0] = Integer.toString(Integer.parseInt(userCurChip[0])+Integer.parseInt(userBettedChip[0])*2);
                userCurChip[1] = Integer.toString(Integer.parseInt(userCurChip[1])+Integer.parseInt(userBettedChip[1])*2);
                userCurChip[2] = Integer.toString(Integer.parseInt(userCurChip[2])+Integer.parseInt(userBettedChip[2])*2);
                userBettedChip[0]="0";userBettedChip[1]="0";userBettedChip[2]="0";
                if(!userTurnChoice[0].equals("d")){
                    myPacket p1win = new myPacket(userNames[0], "603", userBettedChip[0]+" "+userCurChip[0]);
                    WriteAllObject(p1win);
                    userTurnChoice[0]="d";
                }
                if(!userTurnChoice[1].equals("d")){
                    myPacket p2win = new myPacket(userNames[1], "603", userBettedChip[1]+" "+userCurChip[1]);
                    WriteAllObject(p2win);
                    userTurnChoice[1]="d";
                }
                if(!userTurnChoice[2].equals("d")){
                    myPacket p3win = new myPacket(userNames[2], "603", userBettedChip[2]+" "+userCurChip[2]);
                    WriteAllObject(p3win);
                    userTurnChoice[2]="d";
                }
            }
            //딜러 블랙잭
            else if(game.getDealerHand().getHandValue() == 21){
                //플레이어도 블랙잭
                if(game.getPHand().getHandValue() == 21){
                    if(!userTurnChoice[0].equals("d")){
                        userCurChip[0] = Integer.toString(Integer.parseInt(userCurChip[0])+Integer.parseInt(userBettedChip[0]));
                        userBettedChip[0]="0";
                        myPacket p1draw = new myPacket(userNames[0], "602", userBettedChip[0]+" "+userCurChip[0]);
                        WriteAllObject(p1draw);
                        userTurnChoice[0]="d";
                    }
                }
                if(game.getP2Hand().getHandValue() ==21){
                    if(!userTurnChoice[0].equals("d")){
                        userCurChip[1] = Integer.toString(Integer.parseInt(userCurChip[1])+Integer.parseInt(userBettedChip[1]));
                        userBettedChip[1] = "0";
                        myPacket p2draw = new myPacket(userNames[1], "602", userBettedChip[1]+" "+userCurChip[1]);
                        WriteAllObject(p2draw);
                        userTurnChoice[1]="d";
                    }
                }
                if(game.getP3Hand().getHandValue() == 21){
                    if(!userTurnChoice[0].equals("d")){
                        userCurChip[2] = Integer.toString(Integer.parseInt(userCurChip[2])+Integer.parseInt(userBettedChip[2]));
                        userBettedChip[2]="0";
                        myPacket p3draw = new myPacket(userNames[2], "602", userBettedChip[2]+" "+userCurChip[2]);
                        WriteAllObject(p3draw);
                        userTurnChoice[2]="d";
                    }
                }
                else{
                    userBettedChip[0]="0";userBettedChip[1]="0";userBettedChip[2]="0";
                    if(!userTurnChoice[0].equals("d")){
                        myPacket p1lose = new myPacket(userNames[0], "601", userBettedChip[0]+" "+userCurChip[0]);
                        WriteAllObject(p1lose);
                        userTurnChoice[0]="d";
                    }
                    if(!userTurnChoice[1].equals("d")){
                        myPacket p2lose = new myPacket(userNames[1], "601", userBettedChip[1]+" "+userCurChip[1]);
                        WriteAllObject(p2lose);
                        userTurnChoice[1]="d";
                    }
                    if(!userTurnChoice[2].equals("d")){
                        myPacket p3lose = new myPacket(userNames[2], "601", userBettedChip[2]+" "+userCurChip[2]);
                        WriteAllObject(p3lose);
                        userTurnChoice[2]="d";
                    }
                }
            }
            //딜러가 21보다 낮으면
            //딜러와 플레이어의 카드값을 비교해봐야함
            else{
                int dealerv = game.getDealerHand().getHandValue();
                if(dealerv<21){
                    if(dealerv>p1v){
                        if(!userTurnChoice[0].equals("d")) {
                            userBettedChip[0] = "0";
                            myPacket p1lose = new myPacket(userNames[0], "601", userBettedChip[0] + " " + userCurChip[0]);
                            WriteAllObject(p1lose);
                            userTurnChoice[0]="d";
                        }
                    }
                    else if(dealerv>p2v){
                        if(!userTurnChoice[1].equals("d")){
                            userBettedChip[1] = "0";
                            myPacket p2lose = new myPacket(userNames[1], "601", userBettedChip[1]+" "+userCurChip[1]);
                            WriteAllObject(p2lose);
                            userTurnChoice[1]="d";
                        }
                    }
                    else if(dealerv>p3v){
                        if(!userTurnChoice[2].equals("d")){
                            userBettedChip[2] = "0";
                            myPacket p3lose = new myPacket(userNames[2], "601", userBettedChip[2]+" "+userCurChip[2]);
                            WriteAllObject(p3lose);
                            userTurnChoice[2]="d";
                        }
                    }
                }
                //딜러와 동점
                if(dealerv<21){
                    if(dealerv==p1v){
                        if(!userTurnChoice[0].equals("d")){
                            userCurChip[0] = Integer.toString(Integer.parseInt(userCurChip[0])+Integer.parseInt(userBettedChip[0]));
                            userBettedChip[0] = "0";
                            myPacket p1draw = new myPacket(userNames[0], "602", userBettedChip[0]+" "+userCurChip[0]);
                            WriteAllObject(p1draw);
                            userTurnChoice[0]="d";
                        }
                    }
                    if(dealerv==p2v){
                        if(!userTurnChoice[1].equals("d")){
                            userCurChip[1] = Integer.toString(Integer.parseInt(userCurChip[1])+Integer.parseInt(userBettedChip[1]));
                            userBettedChip[1] = "0";
                            myPacket p2draw = new myPacket(userNames[1], "602", userBettedChip[1]+" "+userCurChip[1]);
                            WriteAllObject(p2draw);
                            userTurnChoice[1]="d";
                        }
                    }
                    if(dealerv==p3v){
                        if(!userTurnChoice[2].equals("d")){
                            userCurChip[2] = Integer.toString(Integer.parseInt(userCurChip[2])+Integer.parseInt(userBettedChip[2]));
                            userBettedChip[2] = "0";
                            myPacket p3draw = new myPacket(userNames[2], "602", userBettedChip[2]+" "+userCurChip[2]);
                            WriteAllObject(p3draw);
                            userTurnChoice[2]="d";
                        }
                    }
                }
                //플레이어 승리
                if(dealerv<21){
                    if(dealerv<p1v){
                        if(!userTurnChoice[0].equals("d")){
                            userCurChip[0] = Integer.toString(Integer.parseInt(userCurChip[0])+Integer.parseInt(userBettedChip[0])*2);
                            userBettedChip[0] = "0";
                            myPacket p1win = new myPacket(userNames[0], "603", userBettedChip[0]+" "+userCurChip[0]);
                            WriteAllObject(p1win);
                            userTurnChoice[0]="d";
                        }
                    }
                    if(dealerv<p2v){
                        if(!userTurnChoice[1].equals("d")){
                            userCurChip[1] = Integer.toString(Integer.parseInt(userCurChip[1])+Integer.parseInt(userBettedChip[1])*2);
                            userBettedChip[1] = "0";
                            myPacket p2win = new myPacket(userNames[1], "603", userBettedChip[1]+" "+userCurChip[1]);
                            WriteAllObject(p2win);
                            userTurnChoice[1]="d";
                        }
                    }
                    if(dealerv<p3v){
                        if(!userTurnChoice[2].equals("d")){
                            userCurChip[2] = Integer.toString(Integer.parseInt(userCurChip[2])+Integer.parseInt(userBettedChip[2])*2);
                            userBettedChip[2] = "0";
                            myPacket p3win = new myPacket(userNames[2], "603", userBettedChip[2]+" "+userCurChip[2]);
                            WriteAllObject(p3win);
                            userTurnChoice[2]="d";
                        }
                    }
                }
            }

        }

        public void WriteAll(String str) {
            for (int i = 0; i < user_vc.size(); i++) {
                UserService user = (UserService) user_vc.elementAt(i);
                user.WriteOne(str);
            }
        }

        public void WriteAllObject(myPacket obj){
            for(int i=0;i<user_vc.size();i++){
                UserService user = (UserService) user_vc.elementAt(i);
                user.WriteMsg(obj);
            }
        }

        public void WriteOthers(String str){
            for(int i=0; i<user_vc.size(); i++){
                UserService user = (UserService) user_vc.elementAt(i);
                user.WriteOne(str);
            }
        }

        public byte[] MakePacket(String msg) {
            byte[] packet = new byte[BUF_LEN];
            byte[] bb = null;
            int i;
            for (i = 0; i < BUF_LEN; i++)
                packet[i] = 0;
            try {
                bb = msg.getBytes("euc-kr");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            for (i = 0; i < bb.length; i++)
                packet[i] = bb[i];
            return packet;
        }

        public void WriteOne(String msg) {
            myPacket obcm = new myPacket("SERVER", "200", msg);
            WriteMsg(obcm);
        }

        public void WriteMsg(myPacket obj) {
            try {
                oos.writeObject(obj.code);
                oos.writeObject(obj.UserName);
                oos.writeObject(obj.data);
            }
            catch (IOException e) {
                AppendText("oos.writeObject(ob) error");
                try {
                    ois.close();
                    oos.close();
                    client_socket.close();
                    client_socket = null;
                    ois = null;
                    oos = null;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                Logout();
            }
        }


        public myPacket ReadMsg() {
            Object obj = null;
            String msg = null;
            myPacket cm = new myPacket("", "", "");
            try {
                obj = ois.readObject();
                cm.code = (String) obj;
                obj = ois.readObject();
                cm.UserName = (String) obj;
                obj = ois.readObject();
                cm.data = (String) obj;
            } catch (ClassNotFoundException e) {
                Logout();
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                Logout();
                return null;
            }
            return cm;
        }
        public void run() {
            while(user_vc.size() <= 3){
                myPacket cm = null;
                if(client_socket == null){
                    break;
                }
                cm = ReadMsg();
                if(cm.code.length()==0){
                    break;
                }
                AppendObject(cm);

                if(cm.code.matches("100")){
                    UserName = cm.UserName;
                    Login();
                    if(user_vc.size() == 3 ){
                        String pnames = "";
                        for(int i=0; i<user_vc.size(); i++){
                            UserService user = (UserService) user_vc.elementAt(i);
                            pnames = pnames + user.UserName+" ";
                            userNames[i] = user.UserName;
                            System.out.println(userNames[i]);
                        }
                        if(userNames.length==3){
                            myPacket players = new myPacket("SERVER","110", pnames);
                            WriteAllObject(players);
                        }
                    }
                }
                else if(cm.code.matches("200")){
                    String msg = String.format("[%s] %s", cm.UserName, cm.data);
                    AppendText(msg);
                    String[] args = msg.split(" ");
                }
                else if(cm.code.matches("215")){
                    setEnd++;
                    if(setEnd == 3){
                        if(userNames[0].equals(cm.UserName)){
                            myPacket startAction = new myPacket(cm.UserName, "500", "startAction");
                            WriteAllObject(startAction);
                        }else if(userNames[1].equals(cm.UserName)){
                            myPacket startAction = new myPacket(cm.UserName, "500", "startAction");
                            WriteAllObject(startAction);
                        }else if(userNames[2].equals(cm.UserName)){
                            myPacket startAction = new myPacket(cm.UserName, "500", "startAction");
                            WriteAllObject(startAction);
                        }

                    }
                }
                else if (cm.code.matches("300")) {

                    for (int i = 0; i < user_vc.size(); i++) {
                        UserService user = (UserService) user_vc.elementAt(i);
                        if (user.UserName.equals(cm.UserName)) {
                            String tmpdata = cm.data;
                            String[] tmp = tmpdata.split(" ");
                            userBettedChip[i] = tmp[0];
                            userCurChip[i] = tmp[1];
                            //System.out.println(cm.UserName + "님의 베팅 칩수는 "+userBettedChip[i] + "이고 현재 보유중인 칩수는 "+userCurChip[i] + "입니다.");
                            myPacket pBets = new myPacket(cm.UserName, "300", cm.data);
                            WriteAllObject(pBets);
                        }
                    }
                    //플레이어들의 베팅을 받았고, 받은 베팅으로 각 클라이언트에 기록하고 다음으로는 첫번째 카드의 배분이다.
                }
                else if(cm.code.matches("310")){
                    //System.out.println(cm.UserName + "님의 베팅 완료를 알립니다.");
                    betEnd++;
                    if(betEnd == 3){
                        //System.out.println("bet를 세는데 성공");
                        game.startGame();
                        userTurnChoice[0]="";userTurnChoice[1]="";userTurnChoice[2]="";
                        turnEnd=0;
                        //현재턴을 알림
                        int now = game.getTurn();
                        myPacket curTurn = new myPacket("SERVER", "900", Integer.toString(now));
                        WriteAllObject(curTurn);
                        
                        String p1c1 = game.drawCard("p");
                        String p1c2 = game.drawCard("p");
                        //System.out.println(p1c1 + " and " + p1c2);
                        myPacket p1FirstCard1 = new myPacket(userNames[0], "210", p1c1);
                        myPacket p1FirstCard2 = new myPacket(userNames[0], "210", p1c2);
                        WriteAllObject(p1FirstCard1);WriteAllObject(p1FirstCard2);
                        System.out.println(userNames[0]+" send");

                        String p2c1 = game.drawCard("p2");
                        String p2c2 = game.drawCard("p2");
                        //System.out.println(p2c1 + " and " + p2c2);
                        myPacket p2FirstCard1 = new myPacket(userNames[1], "210", p2c1);
                        myPacket p2FirstCard2 = new myPacket(userNames[1], "210", p2c2);
                        WriteAllObject(p2FirstCard1);WriteAllObject(p2FirstCard2);
                        System.out.println(userNames[1]+" send");

                        String p3c1 = game.drawCard("p3");
                        String p3c2 = game.drawCard("p3");
                        //System.out.println(p3c1 + " and " + p3c2);
                        myPacket p3FirstCard1 = new myPacket(userNames[2], "210", p3c1);
                        myPacket p3FirstCard2 = new myPacket(userNames[2], "210", p3c2);
                        WriteAllObject(p3FirstCard1);WriteAllObject(p3FirstCard2);
                        System.out.println(userNames[2]+" send");

                        String dc1 = game.drawCard("d");
                        String dc2 = game.drawCard("d");
                        myPacket dFirstCard1 = new myPacket("d", "210", dc1);
                        myPacket dFirstCard2 = new myPacket("d", "210", dc2);
                        WriteAllObject(dFirstCard1);WriteAllObject(dFirstCard2);
                        System.out.println("Dealer card send");
                        betEnd=0;
                    }

                }
                else if(cm.code.matches("330")){
                    myPacket Record = new myPacket(cm.UserName, "330", cm.data);
                    WriteAllObject(Record);
                }
                else if (cm.code.matches("400")) {
                    Logout();
                    break;
                }
                else if(cm.code.matches("501")){
                    //여기서 c1의 선택을 저장해두고
                    String addP1Card = game.drawCard("p");
                    myPacket p1AddCard = new myPacket(userNames[0], "501", addP1Card);
                    WriteAllObject(p1AddCard);
                    System.out.println("by hit, add card to p1 send");
                    userTurnChoice[0] = "h";
                }
                else if(cm.code.matches("502")){
                    //여기서 c2의 선택을 저장해두고
                    
                        String addP2Card = game.drawCard("p2");
                        myPacket p2AddCard = new myPacket(userNames[1], "502", addP2Card);
                        WriteAllObject(p2AddCard);
                        System.out.println("by hit, add card to p2 send");
                        userTurnChoice[1] = "h";

                }
                else if(cm.code.matches("503")){
                    //c3의 선택
                    String addP3Card = game.drawCard("p3");
                    myPacket p3AddCard = new myPacket(userNames[2], "503", addP3Card);
                    WriteAllObject(p3AddCard);
                    System.out.println("by hit, add card to p3 send");
                    userTurnChoice[2] = "h";
                }
                else if(cm.code.matches("510")){
                    //플레이어가 stay를 눌렀을때
                    if(cm.UserName.equals(userNames[0])){
                        if(cm.data.equals("p1bj")){
                            userTurnChoice[0] = "s";
                            myPacket p1bj = new myPacket("server", "510", "p1bj");
                            WriteAllObject(p1bj);
                        }else if(cm.data.equals("p1 stay")){
                            userTurnChoice[0] = "s";
                            myPacket p1Stay = new myPacket("server", "510", "p1Stay");
                            WriteAllObject(p1Stay);
                        }
                    }else if(cm.UserName.equals(userNames[1])){
                        if(cm.data.equals("bj")){
                            userTurnChoice[1] = "s";
                            myPacket p2bj = new myPacket("server", "510", "p2bj");
                            WriteAllObject(p2bj);
                        }else if(cm.data.equals("p2 stay")){
                            userTurnChoice[1] = "s";
                            myPacket p2Stay = new myPacket("server", "510", "p2Stay");
                            WriteAllObject(p2Stay);
                        }
                    }else if(cm.UserName.equals(userNames[2])){
                        if(cm.data.equals("bj")){
                            userTurnChoice[2] = "s";
                            myPacket p3bj = new myPacket("server", "510", "p3bj");
                            WriteAllObject(p3bj);
                        }else if(cm.data.equals("p3 stay")){
                            userTurnChoice[2] = "s";
                            myPacket p3Stay = new myPacket("server", "510", "p3Stay");
                            WriteAllObject(p3Stay);
                        }
                        if((userTurnChoice[0].equals("s")||userTurnChoice[0].equals("d"))&&
                                (userTurnChoice[1].equals("s")||userTurnChoice[1].equals("d"))&&
                                (userTurnChoice[2].equals("s")||userTurnChoice[2].equals("d"))){
                            CardCalculate();
                        }
                    }

                }
                else if(cm.code.matches("520")){
                    //플레이어가 die를 눌렀을때
                    if(userNames[0].equals(cm.UserName)){
                        if(cm.data.equals("bust die")) {
                            userTurnChoice[0] = "d";
                            myPacket p1bust = new myPacket(userNames[0], "530", "p1bust");
                            WriteAllObject(p1bust);
                        }else if(cm.data.equals("p1 die")){
                            userTurnChoice[0] = "d";
                            game.getPHand().clearHand();
                            myPacket p1dead = new myPacket(userNames[0], "530", "p1Dead");
                            WriteAllObject(p1dead);
                        }

                    }else if(userNames[1].equals(cm.UserName)){
                        if (cm.data.equals("bust die")) {
                            userTurnChoice[1] = "d";
                            myPacket p2bust = new myPacket(userNames[1], "530", "p2bust");
                            WriteAllObject(p2bust);
                        }else if(cm.data.equals("p2 die")){
                            userTurnChoice[1] = "d";
                            game.getP2Hand().clearHand();
                            myPacket p2dead = new myPacket(userNames[1], "530", "p2Dead");
                            WriteAllObject(p2dead);
                        }

                    }else if(userNames[2].equals(cm.UserName)){
                        if(cm.data.equals("bust die")){
                            userTurnChoice[2]="d";
                            myPacket p3bust = new myPacket(userNames[2], "530", "p3bust");
                            WriteAllObject(p3bust);
                        }else if(cm.data.equals("p3 die")){
                            userTurnChoice[2]="d";
                            game.getP3Hand().clearHand();
                            myPacket p3dead = new myPacket(userNames[2], "530", "p3Dead");
                            WriteAllObject(p3dead);
                        }

                        if((userTurnChoice[0].equals("s")||userTurnChoice[0].equals("d"))&&
                                (userTurnChoice[1].equals("s")||userTurnChoice[1].equals("d"))&&
                                (userTurnChoice[2].equals("s")||userTurnChoice[2].equals("d"))){
                            CardCalculate();
                        }
                    }
                } else if (cm.code.matches("990")) {
                    System.out.println(cm.data);
                    turnEnd++;
                    if(turnEnd == 3){
                        nextTurn();
                    }

                }
            }
        }
    }

}
