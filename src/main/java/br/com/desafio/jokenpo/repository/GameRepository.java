package br.com.desafio.jokenpo.repository;

import br.com.desafio.jokenpo.entity.MoveEntry;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class GameRepository {

    private final Map<Integer, List<MoveEntry>> games;

    public GameRepository() {
        games = new HashMap<>();
    }

    public void addGame(Integer key, List<MoveEntry> entries) {
        games.put(key, entries);
    }

    public void deleteGame(Integer key) {
        games.remove(key);
    }

    public Map<Integer, List<MoveEntry>> findGames() {
        return games;
    }

    public Optional<Map.Entry<Integer, List<MoveEntry>>> findGameById(Integer id) {
        return games.entrySet().stream()
                .filter(g -> g.getKey().equals(id))
                .findAny();
    }

    public Integer lastGame() {
        return games.size() - 1;
    }

}
