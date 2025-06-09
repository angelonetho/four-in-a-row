package dev.netho.game.graphic;

import javax.swing.*;
import java.awt.*;

public class DialogGenerator {
    public static String inputPlayer(String title, Icon icon, String initial) {
        return (String) JOptionPane.showInputDialog(
                null,
                title,
                "Bem-vindo",
                JOptionPane.QUESTION_MESSAGE,
                icon,
                null,
                initial
        );
    }

    public static void showWinner(Component parent,
                                  String name, Icon icon) {
        JOptionPane.showMessageDialog(
                parent,
                name + " venceu!",
                "Fim de jogo",
                JOptionPane.INFORMATION_MESSAGE,
                icon
        );
    }
}
