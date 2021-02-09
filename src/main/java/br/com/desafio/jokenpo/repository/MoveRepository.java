package br.com.desafio.jokenpo.repository;

import br.com.desafio.jokenpo.entity.Move;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class MoveRepository {

    private final List<Move> moves;

    public MoveRepository() {
        moves = new ArrayList<>();
    }

    public void addMove(Move move) {
        moves.add(move);
    }

    public void deleteMove(Move move) {
        moves.remove(move);
    }

    public Optional<List<Move>> getMoves() {
        return Optional.ofNullable(moves);
    }

    public Optional<Move> getMoveByMove(String move) {
        return moves.stream()
                .filter(entry -> entry.getMove().contains(move))
                .findAny();
    }

    public boolean findMove(String move) {
        return moves.stream().anyMatch(m -> m.getMove().contains(move));
    }

}
