package br.com.desafio.jokenpo.repository;

import br.com.desafio.jokenpo.entity.Player;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PlayerRepository {

    private final List<Player> players;

    public PlayerRepository() {
        players = new ArrayList<>();
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void deletePlayer(Player player) {
        players.remove(player);
    }

    public Optional<List<Player>> getPlayers() {
        return Optional.ofNullable(players);
    }

    public Optional<Player> getPlayerByPlayer(String player) {
        return players.stream()
                .filter(entry -> entry.getPlayer().contains(player))
                .findAny();
    }

    public boolean findPlayer(String player) {
        return players.stream().anyMatch(p -> p.getPlayer().contains(player));
    }

}
