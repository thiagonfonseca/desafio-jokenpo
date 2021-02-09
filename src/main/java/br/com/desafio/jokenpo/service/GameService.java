package br.com.desafio.jokenpo.service;

import br.com.desafio.jokenpo.entity.MoveEntry;
import br.com.desafio.jokenpo.entity.Move;
import br.com.desafio.jokenpo.entity.Player;
import br.com.desafio.jokenpo.exception.BadRequestException;
import br.com.desafio.jokenpo.exception.DataConflictException;
import br.com.desafio.jokenpo.exception.DataNotFoundException;
import br.com.desafio.jokenpo.repository.GameRepository;
import br.com.desafio.jokenpo.repository.MoveEntryRepository;
import br.com.desafio.jokenpo.repository.MoveRepository;
import br.com.desafio.jokenpo.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameService {

    @Autowired
    private MoveRepository moveRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private MoveEntryRepository moveEntryRepository;

    @Autowired
    private GameRepository gameRepository;

    public void createPlayer(String player) throws DataConflictException {
        Optional<Player> verifyPlayer = verifyPlayer(player);
        if (verifyPlayer.isPresent()) {
            throw new DataConflictException("O jogador já está cadastrado!");
        }
        playerRepository.addPlayer(new Player(player));
    }

    public List<Player> findPlayers() throws DataNotFoundException {
        return playerRepository.getPlayers().orElseThrow(() -> new DataNotFoundException("Não há jogadores cadastrados!"));
    }

    public Player findByPlayer(String player) throws DataNotFoundException {
        return verifyPlayer(player).orElseThrow(() -> new DataNotFoundException("O jogador não está cadastrado!"));
    }

    public void deletePlayer(String player) throws DataNotFoundException, DataConflictException {
        Player p = verifyPlayer(player).orElseThrow(() -> new DataNotFoundException("O jogador não está cadastrado!"));
        if (moveEntryRepository.findEntryByPlayer(player)) {
            throw new DataConflictException("O jogador está registrado no jogo atual!");
        }
        playerRepository.deletePlayer(p);
    }

    public void createMove(String move) throws DataConflictException, BadRequestException {
        Optional<Move> verifyMove = verifyMove(move);
        if (verifyMove.isPresent()) {
            throw new DataConflictException("A jogada já está cadastrada!");
        }
        if (!move.equalsIgnoreCase("Spock") && !move.equalsIgnoreCase("Jogada Spock") &&
                !move.equalsIgnoreCase("Tesoura") && !move.equalsIgnoreCase("Jogada Tesoura") &&
                !move.equalsIgnoreCase("Papel") && !move.equalsIgnoreCase("Jogada Papel") &&
                !move.equalsIgnoreCase("Pedra") && !move.equalsIgnoreCase("Jogada Pedra") &&
                !move.equalsIgnoreCase("Lagarto") && !move.equalsIgnoreCase("Jogada Lagarto")) {
            throw new BadRequestException("Você pode cadastrar apenas os movimentos Spock, Tesoura, Papel, Pedra e Lagarto");
        }
        moveRepository.addMove(new Move(move));
    }

    public List<Move> findMoves() throws DataNotFoundException {
        return moveRepository.getMoves().orElseThrow(() -> new DataNotFoundException("Não há jogadas cadastradas!"));
    }

    public Move findByMove(String move) throws DataNotFoundException {
        return verifyMove(move).orElseThrow(() -> new DataNotFoundException("A jogada não está cadastrada!"));
    }

    public void deleteMove(String move) throws DataNotFoundException, DataConflictException {
        Move m = verifyMove(move).orElseThrow(() -> new DataNotFoundException("A jogada não está cadastrada!"));
        if (moveEntryRepository.findEntryByMove(move)) {
            throw new DataConflictException("Esta jogada está registrada no jogo atual!");
        }
        moveRepository.deleteMove(m);
    }

    public String insertMoveEntry(String entry) throws DataNotFoundException, BadRequestException, DataConflictException {
        String[] tmpEntry = entry.split(" e ");
        Optional<String> play = Arrays.stream(tmpEntry)
                .filter(e -> e.toUpperCase().contains("Jogar".toUpperCase()))
                .findFirst();
        if (play.isPresent()) {
            if (moveEntryRepository.countEntries() < 2) {
                throw new BadRequestException("O jogo possui menos que dois jogadores!");
            }
            boolean isSpock = verifyMoveOnEntries("Spock");
            boolean isTesoura = verifyMoveOnEntries("Tesoura");
            boolean isPapel = verifyMoveOnEntries("Papel");
            boolean isPedra = verifyMoveOnEntries("Pedra");
            boolean isLagarto = verifyMoveOnEntries("Lagarto");
            String result;
            if (isSpock && (isTesoura || isPedra) && !isPapel && !isLagarto) {
                result = produceResult("Spock");
            } else if (isTesoura && (isPapel || isLagarto) && !isSpock && !isPedra) {
                result = produceResult("Tesoura");
            } else if (isPapel && (isPedra || isSpock) && !isTesoura && !isLagarto) {
                result = produceResult("Papel");
            } else if (isPedra && (isLagarto || isTesoura) && !isSpock && !isPapel) {
                result = produceResult("Pedra");
            } else if (isLagarto && (isSpock || isPapel) && !isTesoura && !isPedra) {
                result = produceResult("Lagarto");
            } else {
                result = "Resultado Empate";
            }
            int lastElement = gameRepository.lastGame();
            List<MoveEntry> entries = moveEntryRepository.getEntries().orElseThrow(() -> new DataNotFoundException("Não há um jogo ativo!"));
            gameRepository.addGame(lastElement + 1, entries);
            moveEntryRepository.clearEntries();
            return result;
        } else {
            if (tmpEntry.length == 2) {
                String player = tmpEntry[0];
                String move = tmpEntry[1];
                boolean foundPlayer = playerRepository.findPlayer(tmpEntry[0]);
                if (!foundPlayer) {
                    throw new DataNotFoundException("O jogador não está cadastrado!");
                }
                if (verifyPlayerOnEntries(player)) {
                    throw new DataConflictException("O jogador " + player + " já fez sua jogada!");
                }
                boolean foundMove = moveRepository.findMove(tmpEntry[1]);
                if (!foundMove) {
                    throw new DataNotFoundException("A jogada não está cadastrada!");
                }
                moveEntryRepository.addEntry(new MoveEntry(player, move));
                return "";
            } else {
                throw new BadRequestException("Jogada invalida");
            }
        }
    }

    public Map<Integer, List<MoveEntry>> findGames() {
        return gameRepository.findGames();
    }

    public List<MoveEntry> findGameById(Integer id) throws DataNotFoundException {
        Map.Entry<Integer, List<MoveEntry>> game = verifyGame(id).orElseThrow(() -> new DataNotFoundException("Este jogo não está cadastrado!"));
        return game.getValue();
    }

    public void deleteGame(Integer id) throws DataNotFoundException {
        Map.Entry<Integer, List<MoveEntry>> game = verifyGame(id).orElseThrow(() -> new DataNotFoundException("Este jogo não está cadastrado!"));
        gameRepository.deleteGame(game.getKey());
    }

    private Optional<Player> verifyPlayer(String player) {
        return playerRepository.getPlayerByPlayer(player);
    }

    private Optional<Move> verifyMove(String move) {
        return moveRepository.getMoveByMove(move);
    }

    private Optional<Map.Entry<Integer, List<MoveEntry>>> verifyGame(Integer id) {
        return gameRepository.findGameById(id);
    }

    private boolean verifyPlayerOnEntries(String player) {
        return moveEntryRepository.findPlayerOnEntries(player);
    }

    private boolean verifyMoveOnEntries(String move) {
        return moveEntryRepository.findMoveOnEntries(move);
    }

    private String produceResult(String winnerMove) {
        List<MoveEntry> winners = moveEntryRepository.getWinnerMove(winnerMove);
        if (winners.size() == 1) {
            return "Resultado " + winners.get(0).getPlayer() + " Vitória";
        } else {
            StringBuilder result = new StringBuilder("Resultado ");
            for (MoveEntry winner : winners) {
                result.append(winner.getPlayer());
                if (winners.get(winners.size()-1).equals(winner)) {
                    result.append(" ");
                } else {
                    result.append(" e ");
                }
            }
            result.append("Vitória");
            return result.toString();
        }
    }

}
