
package blackjackgame.gui;

import java.awt.*;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public final class ContainerPanel extends JPanel {
    private Frame frame;
    private final PlayerHandPanel pHandPanel;
    private final PlayerHandPanel pHandPanel2;
    private final PlayerHandPanel pHandPanel3;
    private final FnPanel fn;
    private final RecordsPanel records;
    private final DealerHandPanel dHandPanel;

    
    public ContainerPanel(Frame f) {
        setFrame(f);
        fn = new FnPanel(this);
        records = new RecordsPanel(this);
        pHandPanel2 = new PlayerHandPanel(this);
        pHandPanel3 = new PlayerHandPanel(this);
        pHandPanel = new PlayerHandPanel(this);
        dHandPanel = new DealerHandPanel(this);

        createPanel();
    }
    
    public void createPanel() {
        this.setLayout(new GridLayout(2,3));
        fn.setBorder(new LineBorder(new Color(0, 0, 0)));
        dHandPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
        records.setBorder(new EmptyBorder(5,5,5,5));
        pHandPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
        pHandPanel2.setBorder(new LineBorder(new Color(0, 0, 0)));
        pHandPanel3.setBorder(new LineBorder(new Color(0, 0, 0)));
        this.add(fn);
        this.add(dHandPanel);
        this.add(records);

        this.add(pHandPanel);

        this.add(pHandPanel2);
        this.add(pHandPanel3);
        
        this.setBackground(new Color(22,122,9));
    }

    public void setFrame(Frame f)
    {
        frame = f;
    }
    public Frame getFrame()
    {
        return frame;
    }
    public DealerHandPanel getDHandPanel()
    {
        return dHandPanel;
    }
    public PlayerHandPanel getPlayer1HandPanel() { return pHandPanel; }
    public PlayerHandPanel getPlayer2HandPanel()
    {
        return pHandPanel2;
    }
    public PlayerHandPanel getPlayer3HandPanel()
    {
        return pHandPanel3;
    }
    public FnPanel getFnPanel() { return fn;}
    public RecordsPanel getRecordsPanel() { return records;}

}
