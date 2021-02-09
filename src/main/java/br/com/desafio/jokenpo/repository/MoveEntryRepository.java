package br.com.desafio.jokenpo.repository;

import br.com.desafio.jokenpo.entity.MoveEntry;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MoveEntryRepository {

    private List<MoveEntry> entries;

    public MoveEntryRepository() {
        entries = new ArrayList<>();
    }

    public void addEntry(MoveEntry me) {
        entries.add(me);
    }

    public void clearEntries() {
        entries = new ArrayList<>();
    }

    public Optional<List<MoveEntry>> getEntries() {
        return Optional.ofNullable(entries);
    }

    public List<MoveEntry> getWinnerMove(String winnerMove) {
        return entries.stream()
                .filter(e -> e.getMove().toUpperCase().contains(winnerMove.toUpperCase()))
                .collect(Collectors.toList());
    }

    public boolean findEntryByPlayer(String player) {
        return entries.stream().anyMatch(e -> e.getPlayer().contains(player));
    }

    public boolean findEntryByMove(String move) {
        return entries.stream().anyMatch(e -> e.getMove().contains(move));
    }

    public boolean findPlayerOnEntries(String player) {
        return entries.stream().anyMatch(e -> e.getPlayer().toUpperCase().contains(player.toUpperCase()));
    }

    public boolean findMoveOnEntries(String move) {
        return entries.stream().anyMatch(e -> e.getMove().toUpperCase().contains(move.toUpperCase()) ||
                e.getMove().toUpperCase().contains("Jogada".toUpperCase() + " " + move.toUpperCase()));
    }

    public Integer countEntries() {
        return entries.size();
    }
}
