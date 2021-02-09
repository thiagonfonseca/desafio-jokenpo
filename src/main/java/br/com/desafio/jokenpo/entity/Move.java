package br.com.desafio.jokenpo.entity;

import java.util.Objects;

public class Move {

    private String move;

    public Move() {
    }

    public Move(String move) {
        this.move = move;
    }

    public String getMove() {
        return move;
    }

    public void setMove(String move) {
        this.move = move;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move1 = (Move) o;
        return Objects.equals(move, move1.move);
    }

    @Override
    public int hashCode() {
        return Objects.hash(move);
    }
}
