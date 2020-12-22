package blackjackgame.gui;

import blackjackgame.main.Game;
import javax.swing.JFrame;

public class Frame extends JFrame
{
    private Game game;
    private ContainerPanel containerPanel;
    
    public Frame(Game g) {
        setGame(g);
        containerPanel = new ContainerPanel(this);
        createFrame();
    }
    
    public void createFrame() {
        setTitle("BlackJack");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200,900);
        add(containerPanel);
        setTitle("BlackJack Game");
        setVisible(true);
    }

    public void setGame(Game g) {
        game = g;
    }
    
    public Game getGame() {
        return game;
    }
    
    public ContainerPanel getContainerPanel() {
        return containerPanel;
    }
}
