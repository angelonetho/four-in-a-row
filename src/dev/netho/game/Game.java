package dev.netho.game;

import dev.netho.game.entity.Board;
import dev.netho.game.entity.Disc;
import dev.netho.game.entity.Player;
import dev.netho.game.entity.Ranking;
import dev.netho.game.exception.IllegalMoveException;
import dev.netho.game.graphic.BoardPanel;
import dev.netho.game.graphic.DiscIcon;
import dev.netho.game.graphic.StatusPanel;


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

import static dev.netho.game.graphic.DialogGenerator.inputPlayer;
import static dev.netho.game.graphic.DialogGenerator.showWinner;

public class Game extends JFrame {
    private final Ranking ranking = new Ranking();
    private final Board tabuleiro = new Board();
    private final Player player1;
    private final Player player2;
    private Disc currentDisc = Disc.PLAYER1;

    private final static int PADDING = 16;
    private final static Color PLAYER1_COLOR = Color.RED;
    private final static Color PLAYER2_COLOR = new Color(0,203,254);
    private final static Color BACKGROUND_COLOR = new Color(0, 82, 155);

    public Game(String name1, String name2) {

        // Tenta carregar dados do disco, se não, cria novo
        loadRankingFromDisk();

        player1 = ranking.addPlayer(new Player(name1, 0, 0, LocalDateTime.now()));
        player2 = ranking.addPlayer(new Player(name2, 0, 0, LocalDateTime.now()));

        setTitle("Ligue-4");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(7 * 80 + 16, 6 * 80 + 39); // conta bordas
        setResizable(false);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // Gráficos

        BoardPanel boardPanel = new BoardPanel(
                tabuleiro,
                PADDING,
                BACKGROUND_COLOR,
                BACKGROUND_COLOR,    // furo usa mesma cor do fundo
                PLAYER1_COLOR,
                PLAYER2_COLOR
        );

        add(boardPanel, BorderLayout.CENTER);
        add(new StatusPanel(
                player1.getName(), new DiscIcon(PLAYER1_COLOR, 80),
                player2.getName(), new DiscIcon(PLAYER2_COLOR, 80),
                boardPanel.getPreferredSize().height/2 - 32,
                BACKGROUND_COLOR
        ), BorderLayout.SOUTH);

        pack();
        setVisible(true);

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

                // checa empate
                if (tabuleiro.checkDrawCondition()) {
                    JOptionPane.showMessageDialog(boardPanel, "Empate!", "Fim de jogo", JOptionPane.INFORMATION_MESSAGE);
                    player1.incrementVictories();
                    player2.incrementVictories();

                    saveRankingToDisk();
                    showRankingWindow();
                    System.exit(0);
                }

                // checa vitória
                if (tabuleiro.checkWinCondition(currentDisc)) {
                    String winner = (currentDisc == Disc.PLAYER1 ? player1.getName() : player2.getName());

                    DiscIcon winIcon = new DiscIcon(
                            currentDisc == Disc.PLAYER1
                                    ? PLAYER1_COLOR
                                    : PLAYER2_COLOR,
                            40
                    );

                    showWinner(boardPanel, winner, winIcon);

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
    }

    // MÉTODO DA JANELA DE RANKING ATUALIZADO COM BOTÃO
private void showRankingWindow() {
    List<Player> copia = new ArrayList<>(ranking.getPlayers());
    copia.sort(Comparator.comparingInt(Player::getVictories).reversed());

    String[] colNames = {"Posição", "Nome", "Vitórias", "Derrotas", "Primeiro Jogo"};
    Object[][] data = new Object[copia.size()][5];
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    for (int i = 0; i < copia.size(); i++) {
        Player p = copia.get(i);
        data[i][0] = (i + 1);
        data[i][1] = p.getName();
        data[i][2] = p.getVictories();
        data[i][3] = p.getDefeats();
        data[i][4] = p.getFirstPlayedAt().format(fmt);
    }

    JTable table = new JTable(data, colNames);
    table.setEnabled(false);
    JScrollPane scroll = new JScrollPane(table);
    scroll.setPreferredSize(new Dimension(500, Math.min(300, copia.size() * 25 + 30)));

    String[] options = {"Exportar para CSV", "Fechar"};

    int choice = JOptionPane.showOptionDialog(
            this,
            scroll,
            "🏆 Ranking de Jogadores",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            options,
            options[1]
    );

    if (choice == 0) {
        exportRankingToCSV();
    }
    
    }

    private void loadRankingFromDisk() {
        File f = new File("ranking.bin");
        if (!f.exists()) return;

        System.out.println("Carregando ranking do disco...");
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            Ranking loaded = (Ranking) ois.readObject();
            this.ranking.getPlayers().clear();
            this.ranking.getPlayers().addAll(loaded.getPlayers());
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar ranking: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveRankingToDisk() {
        System.out.println("Salvando ranking no disco...");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("ranking.bin"))) {
            oos.writeObject(this.ranking);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar ranking: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportRankingToCSV() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("ranking.csv"))) {
            writer.println("Nome,Vitorias,Derrotas");

            for (Player player : ranking.getPlayers()) {
                String csvLine = player.getName() + "," + player.getVictories() + "," + player.getDefeats();
                writer.println(csvLine);
            }

            JOptionPane.showMessageDialog(this,
                    "Ranking exportado com sucesso para ranking.csv!",
                    "Exportação Concluída",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao exportar ranking para CSV: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            // ícones para cada jogador
            DiscIcon redDisc  = new DiscIcon(PLAYER1_COLOR, 40);
            DiscIcon blueDisc = new DiscIcon(PLAYER2_COLOR, 40);

            String p1 = inputPlayer("Nome do Jogador 1:", redDisc, "Jogador 1");

            String p2 = inputPlayer("Nome do Jogador 2:", blueDisc, "Jogador 2");

            new Game(p1, p2);
        });
    }

}
