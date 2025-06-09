package dev.netho.game.graphic;

import dev.netho.game.entity.Board;
import dev.netho.game.entity.Disc;

import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {
    private final Board board;
    private final int padding;
    private final Color background;
    private final Color holeColor;
    private final Color player1Color;
    private final Color player2Color;

    public BoardPanel(Board board,
                      int padding,
                      Color background,
                      Color holeColor,
                      Color player1Color,
                      Color player2Color) {
        this.board = board;
        this.padding = padding;
        this.background = background;
        this.holeColor = holeColor;
        this.player1Color = player1Color;
        this.player2Color = player2Color;
        setOpaque(true);
        setBackground(background);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        // fundo
        g2.setColor(background);
        g2.fillRect(0, 0, getWidth(), getHeight());

        for (int row = 0; row < Board.ROWS; row++) {
            for (int col = 0; col < Board.COLS; col++) {
                int x0 = padding + col * 80;
                int y0 = padding + row * 80;

                // quadrado de fundo (tabuleiro)
                g2.setColor(new Color(251, 200, 39));
                g2.fillRect(x0, y0, 80, 80);

                // disco ou furo
                Disc d = board.getGrid()[row][col];
                switch (d) {
                    case EMPTY:
                        g2.setColor(holeColor);
                        break;
                    case PLAYER1:
                        g2.setColor(player1Color);
                        break;
                    case PLAYER2:
                        g2.setColor(player2Color);
                        break;
                }
                g2.fillOval(x0 + 10, y0 + 10, 60, 60);

                // sombra
                g2.setColor(new Color(0, 0, 0, 50));
                g2.fillOval(x0 + 17, y0 + 17, 50, 50);

                // contorno
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(3));
                g2.drawOval(x0 + 10, y0 + 10, 60, 60);
            }
        }

        // borda externa
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(padding, padding, Board.COLS * 80, Board.ROWS * 80);

        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(
                Board.COLS * 80 + padding * 2,
                Board.ROWS * 80 + padding * 2
        );
    }
}
