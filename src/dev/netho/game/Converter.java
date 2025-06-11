package dev.netho.game;

import dev.netho.game.entity.Player;
import dev.netho.game.entity.Ranking;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Converter {

    public static void main(String[] args) {
        String csvPath = "ranking.csv";
        String binPath = "ranking.bin";
        Ranking ranking = new Ranking();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        try (Scanner scanner = new Scanner(new File(csvPath))) {
            scanner.nextLine(); // Pular o cabeçalho

            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(",");

                if (parts.length < 4) continue;

                String nome = parts[0].trim();
                int vitorias = Integer.parseInt(parts[1].trim());
                int derrotas = Integer.parseInt(parts[2].trim());
                LocalDateTime dataCadastro = LocalDateTime.parse(parts[3].trim(), formatter);

                Player player = new Player(nome, vitorias, derrotas, dataCadastro);
                ranking.addPlayer(player);
            }


            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(binPath))) {
                oos.writeObject(ranking);
                System.out.println("Arquivo '" + binPath + "' gerado com sucesso com os dados do CSV.");
            }

        } catch (FileNotFoundException e) {
            System.err.println("Arquivo CSV não encontrado: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Erro ao salvar arquivo binário: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro ao processar CSV: " + e.getMessage());
        }
    }
}
