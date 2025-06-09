package dev.netho.game.graphic;

import javax.swing.*;
import java.awt.*;

public class StatusPanel extends JPanel {
    public StatusPanel(String name1, Icon icon1,
                       String name2, Icon icon2,
                       int height, Color bg) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(bg);
        setPreferredSize(new Dimension(0, height));
        add(Box.createHorizontalStrut(13));

        JLabel lbl1 = new JLabel(name1, icon1, JLabel.LEFT);
        lbl1.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl1.setIconTextGap(16);

        JLabel lbl2 = new JLabel(name2, icon2, JLabel.LEFT);
        lbl2.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl2.setIconTextGap(16);

        lbl1.setForeground(Color.WHITE);
        lbl2.setForeground(Color.WHITE);
        lbl1.setFont(new Font("ComicSans", Font.BOLD, 24));
        lbl2.setFont(new Font("ComicSans", Font.BOLD, 24));

        add(lbl1);
        add(Box.createVerticalStrut(16));
        add(lbl2);
        add(Box.createVerticalStrut(16));

    }
}
