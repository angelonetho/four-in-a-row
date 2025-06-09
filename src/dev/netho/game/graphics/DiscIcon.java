package dev.netho.game.graphics;

import javax.swing.*;
import java.awt.*;

// dentro da classe Game, antes do construtor:
public class DiscIcon implements Icon {
    private final Color color;
    private final int size;

    public DiscIcon(Color color, int size) {
        this.color = color;
        this.size = size;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // preenche
        g2.setColor(color);
        g2.fillOval(x, y, size, size);

        // sombra no disco
        g2.setColor(new Color(0, 0, 0, 50));
        g2.fillOval(x + 7, y + 7, size - 10, size - 10);


        // contorno preto
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3));
        g2.drawOval(x, y, size, size);

        g2.dispose();
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }
}
