package dev.netho.game.entity;

import dev.netho.game.exception.PlayerNotFoundException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Ranking implements Serializable {

    private final List<Player> players;

    public Ranking() {
        players = new ArrayList<>();
    }

    public Player addPlayer(Player player) {

        try {
            Player playerInMemory = getByName(player.getName());
            System.out.println("Player found in memory: " + player.getName());
            return playerInMemory;

        } catch (PlayerNotFoundException e) {
            System.out.println("Player not found: " + player.getName());
            System.out.println("Creating new player: " + player.getName());
            players.add(player);
            return player;
        }
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getByName(String name) throws PlayerNotFoundException {
        for (Player player : players) {
            if (player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }

        throw new PlayerNotFoundException("Jogador não encontrado");
    }
}
