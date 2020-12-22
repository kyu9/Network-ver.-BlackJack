
package blackjackgame.main;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtUserName;
    private JTextField txtIpAddress;
    private JTextField txtPortNumber;
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try{
                    Main frame = new Main();
                    frame.setVisible(true);
                    frame.setTitle("Welcome to BlackJack Game");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    public Main(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 508, 321);
        //처음에 클라이언트에 접속하기 전에 사진을 만들려고했지만 안어울림
        //-> 디자인 변경하게 되면 그림판으로 그림파일 열어서 픽셀을 클라이언트의 크기에 맞춰서 만들어줘야함
//        ImageIcon image = new ImageIcon(getClass().getResource("/blackjackgame/gui/images/start.png"));
//        contentPane = new JPanel() {
//        	public void paintComponent(Graphics g) {
//        		g.drawImage(image.getImage(), 0, 0, null);
//        		setOpaque(false);
//            	super.paintComponent(g);
//        	}
//        };

        contentPane = new JPanel();
        contentPane.setBackground(new Color(22,122,9));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);  

        JLabel lblNewLabel = new JLabel("User Name");
        lblNewLabel.setBounds(12, 39, 82, 33);
        lblNewLabel.setForeground(new Color(255,255,255));
        contentPane.add(lblNewLabel);

        txtUserName = new JTextField();
        txtUserName.setHorizontalAlignment(SwingConstants.CENTER);
        txtUserName.setBounds(101, 39, 116, 33);
        contentPane.add(txtUserName);
        txtUserName.setColumns(10);

        JLabel lblIpAddress = new JLabel("IP Address");
        lblIpAddress.setBounds(12, 100, 82, 33);
        lblIpAddress.setForeground(new Color(255,255,255));
        contentPane.add(lblIpAddress);

        txtIpAddress = new JTextField();
        txtIpAddress.setHorizontalAlignment(SwingConstants.CENTER);
        txtIpAddress.setText("127.0.0.1");
        txtIpAddress.setColumns(10);
        txtIpAddress.setBounds(101, 100, 116, 33);
        contentPane.add(txtIpAddress);


        JLabel lblPortNumber = new JLabel("Port Number");
        lblPortNumber.setBounds(12, 163, 82, 33);
        lblPortNumber.setForeground(new Color(255,255,255));
        contentPane.add(lblPortNumber);

        txtPortNumber = new JTextField();
        txtPortNumber.setText("30000");
        txtPortNumber.setHorizontalAlignment(SwingConstants.CENTER);
        txtPortNumber.setColumns(10);
        txtPortNumber.setBounds(101, 163, 116, 33);
        contentPane.add(txtPortNumber);

        JButton btnConnect = new JButton("Game Start!");
        btnConnect.setBounds(12, 223, 205, 38);
        btnConnect.setBackground(new Color(255,255,255));
        contentPane.add(btnConnect);

        JScrollPane scroll = new JScrollPane();
        scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMinimum());
        scroll.setBounds(227,39,250,222);
        contentPane.add(scroll);

        JTextArea desc = new JTextArea();
        desc.setEditable(false);
        desc.append("WELCOME to BlackJack Game!\n");
        desc.append("게임은 3개의 덱(52*3 = 156장)으로 진행\n\n");
        desc.append("게임규칙\n");
        desc.append("1. 합이 21이 된다면 바로 승자로 간주,\n  베팅한 금액만큼 더 받습니다.\n");
        desc.append("2. 딜러와 플레이어의 카드합이 같을 경우,\n  베팅했던 금액만 그대로 돌려받습니다.\n");
        desc.append("3. 플레이어, 딜러 모두 최대로 받을 수 있는\n  카드의 수는 5장입니다.\n");
        desc.append("4. 시작시 20개의 칩을 가지고 시작하고,\n  칩이 0개면 자동 퇴장됩니다.\n");

        contentPane.add(desc);

        scroll.setViewportView(desc);
        
        Myaction action = new Myaction();
        btnConnect.addActionListener(action);
        txtUserName.addActionListener(action);
        txtIpAddress.addActionListener(action);
        txtPortNumber.addActionListener(action);
    }

    class Myaction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = txtUserName.getText().trim();
            String ip_addr = txtIpAddress.getText().trim();
            String port_no = txtPortNumber.getText().trim();
            Game game = new Game(username, ip_addr, port_no);

            game.startGame();
            setVisible(false);
        }
    }
}
