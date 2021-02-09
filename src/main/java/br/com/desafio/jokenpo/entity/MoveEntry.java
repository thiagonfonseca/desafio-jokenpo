package br.com.desafio.jokenpo.entity;

import java.util.Objects;

public class MoveEntry {

    private String player;
    private String move;

    public MoveEntry() {
    }

    public MoveEntry(String player, String move) {
        this.player = player;
        this.move = move;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
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
        MoveEntry game = (MoveEntry) o;
        return Objects.equals(player, game.player) && Objects.equals(move, game.move);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, move);
    }
}
