package dev.netho.game;

import dev.netho.game.entity.Board;
import dev.netho.game.entity.Disc;
import dev.netho.game.entity.Player;
import dev.netho.game.entity.Ranking;
import dev.netho.game.exception.IllegalMoveException;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Game extends JFrame {
    private final Ranking ranking = new Ranking();
    private final Board tabuleiro = new Board();
    private final Player player1;
    private final Player player2;
    private Disc currentDisc = Disc.PLAYER1;

    public Game(String name1, String name2) {

        // Tenta carregar dados do disco, se não, cria novo
        loadRankingFromDisk();
        showRankingWindow();

        player1 = ranking.addPlayer(new Player(name1, 0, 0, LocalDateTime.now()));
        player2 = ranking.addPlayer(new Player(name2, 0, 0, LocalDateTime.now()));

        setTitle("Ligue 4");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(7 * 80 + 16, 6 * 80 + 39); // conta bordas
        setLocationRelativeTo(null);

        // Gráficos
        JPanel boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // desenha fundo azul e círculos
                for (int row = 0; row < Board.ROWS; row++) {
                    for (int col = 0; col < Board.COLS; col++) {
                        // quadrado de fundo
                        g.setColor(Color.ORANGE);
                        g.fillRect(col * 80, row * 80, 80, 80);

                        // disco
                        Disc d = tabuleiro.getGrid()[row][col];
                        switch (d) {
                            case EMPTY  -> g.setColor(Color.WHITE);
                            case PLAYER1-> g.setColor(Color.RED);
                            case PLAYER2-> g.setColor(Color.CYAN);
                        }
                        g.fillOval(col * 80 + 10, row * 80 + 10, 60, 60);
                    }
                }
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(Board.COLS * 80, Board.ROWS * 80);
            }
        };

        // Mouse listener para inserção
        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int coluna = (e.getX() / 80);
                try {
                    if (!tabuleiro.insert(coluna, currentDisc)) {
                        JOptionPane.showMessageDialog(boardPanel, "Coluna cheia!", "Jogada Inválida", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } catch (IllegalMoveException ex) {
                    throw new RuntimeException(ex);
                }

                // repaint para atualizar os gráficos do tabuleiro
                boardPanel.repaint();

                // checa vitória
                if (tabuleiro.checkWinCondition(currentDisc)) {
                    String winner = (currentDisc == Disc.PLAYER1 ? player1.getName() : player2.getName());
                    JOptionPane.showMessageDialog(boardPanel, winner + " venceu!", "Fim de jogo", JOptionPane.INFORMATION_MESSAGE);

                    // Atualiza as estatística

                    if (currentDisc == Disc.PLAYER1) {
                        player1.incrementVictories();
                        player2.incrementDefeats();
                    } else {
                        player2.incrementVictories();
                        player1.incrementDefeats();
                    }

                    saveRankingToDisk();
                    showRankingWindow();
                    System.exit(0);
                }

                // troca de jogador/disco
                currentDisc = (currentDisc == Disc.PLAYER1 ? Disc.PLAYER2 : Disc.PLAYER1);
            }
        });

        add(boardPanel);
        pack();
        setVisible(true);
    }

    private void showRankingWindow() {
        // copia e ordena
        List<Player> copia = new ArrayList<>(ranking.getPlayers());
        copia.sort(Comparator.comparingInt(Player::getVictories).reversed());

        // prepara colunas e dados
        String[] colNames = {"Posição","Nome","Vitórias","Derrotas","Primeiro Jogo"};
        Object[][] data = new Object[copia.size()][5];
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (int i = 0; i < copia.size(); i++) {
            Player p = copia.get(i);
            data[i][0] = (i+1);
            data[i][1] = p.getName();
            data[i][2] = p.getVictories();
            data[i][3] = p.getDefeats();
            data[i][4] = p.getFirstPlayedAt().format(fmt);
        }

        // tabela
        JTable table = new JTable(data, colNames);
        table.setEnabled(false);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(500, Math.min(300, copia.size()*25 + 30)));

        JOptionPane.showMessageDialog(
                this,
                scroll,
                "🏆 Ranking de Jogadores",
                JOptionPane.PLAIN_MESSAGE
        );
    }

    private void loadRankingFromDisk() {

        File f = new File("ranking.bin");
        if (!f.exists()) return;

        System.out.println("Loading ranking from disk");
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(f))) {
            Ranking loaded = (Ranking) ois.readObject();

            // substitui o ranking vazio pelo carregado
            this.ranking.getPlayers().clear();
            this.ranking.getPlayers().addAll(loaded.getPlayers());

        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar ranking: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveRankingToDisk() {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream("ranking.bin"))) {
            oos.writeObject(ranking);
            System.out.println("Ranking salvo com sucesso!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar ranking: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String p1 = JOptionPane.showInputDialog("Nome do Jogador 1:");
            String p2 = JOptionPane.showInputDialog("Nome do Jogador 2:");
            new Game(p1, p2);
        });
    }
}
