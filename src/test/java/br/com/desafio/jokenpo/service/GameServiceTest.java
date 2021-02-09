package br.com.desafio.jokenpo.service;

import br.com.desafio.jokenpo.entity.Move;
import br.com.desafio.jokenpo.entity.MoveEntry;
import br.com.desafio.jokenpo.entity.Player;
import br.com.desafio.jokenpo.exception.BadRequestException;
import br.com.desafio.jokenpo.exception.DataConflictException;
import br.com.desafio.jokenpo.exception.DataNotFoundException;
import br.com.desafio.jokenpo.repository.GameRepository;
import br.com.desafio.jokenpo.repository.MoveEntryRepository;
import br.com.desafio.jokenpo.repository.MoveRepository;
import br.com.desafio.jokenpo.repository.PlayerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class GameServiceTest {

    @InjectMocks
    GameService gameService;

    @Mock
    PlayerRepository playerRepository;

    @Mock
    MoveRepository moveRepository;

    @Mock
    MoveEntryRepository moveEntryRepository;

    @Mock
    GameRepository gameRepository;

    @DisplayName("Testando a criação de um jogador")
    @Test
    void whenPlayerShouldBeCreated() throws DataConflictException {
        Player player = new Player("Jogador 4");

        when(playerRepository.getPlayerByPlayer(player.getPlayer())).thenReturn(Optional.empty());
        doNothing().when(playerRepository).addPlayer(player);

        gameService.createPlayer(player.getPlayer());

        verify(playerRepository, times(1)).getPlayerByPlayer(player.getPlayer());
        verify(playerRepository, times(1)).addPlayer(player);
    }

    @DisplayName("Testando a criaçao de um jogador ja cadastrado")
    @Test
    void whenPlayerAlreadyRegisteredThenAnExceptionShouldBeThrown() {
        Player player = new Player("Jogador 1");

        when(playerRepository.getPlayerByPlayer(player.getPlayer())).thenReturn(Optional.of(player));

        assertThrows(DataConflictException.class, () -> gameService.createPlayer(player.getPlayer()));
    }

    @DisplayName("Testando a busca de jogadores")
    @Test
    void whenListPlayersIsCalledThenReturnAListOfPlayers() throws DataNotFoundException {
        Player player = new Player("Jogador 1");

        when(playerRepository.getPlayers()).thenReturn(Optional.of(Collections.singletonList(player)));

        List<Player> players = gameService.findPlayers();

        assertEquals(players.get(0), player);
    }

    @DisplayName("Testando a busca de jogadores retornando uma lista vazia")
    @Test
    void whenListPlayersIsCalledThenReturnAEmptyListOfPlayers() throws DataNotFoundException {
        when(playerRepository.getPlayers()).thenReturn(Optional.of(Collections.EMPTY_LIST));

        List<Player> players = gameService.findPlayers();

        assertEquals(players, Collections.EMPTY_LIST);
    }

    @DisplayName("Testando a busca de jogador por nome")
    @Test
    void whenPlayerNameIsGivenThenReturnAPlayer() throws DataNotFoundException {
        Player player = new Player("Jogador 1");

        when(playerRepository.getPlayerByPlayer(player.getPlayer())).thenReturn(Optional.of(player));

        Player p = gameService.findByPlayer(player.getPlayer());

        assertEquals(p, player);
    }

    @DisplayName("Testando a busca de jogador por nome nao cadastrado")
    @Test
    void whenNotRegisteredPlayerNameIsGivenThenThrownAnException() {
        when(playerRepository.getPlayerByPlayer("Jogador 4")).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> gameService.findByPlayer("Jogador 4"));
    }

    @DisplayName("Testando a exclusao de um jogador")
    @Test
    void whenExclusionIsCalledThenAPlayerShouldBeDeleted() throws DataConflictException, DataNotFoundException {
        Player player = new Player("Jogador 3");

        when(playerRepository.getPlayerByPlayer(player.getPlayer())).thenReturn(Optional.of(player));
        when(moveEntryRepository.findEntryByPlayer(player.getPlayer())).thenReturn(false);
        doNothing().when(playerRepository).deletePlayer(player);

        gameService.deletePlayer(player.getPlayer());

        verify(playerRepository, times(1)).getPlayerByPlayer(player.getPlayer());
        verify(moveEntryRepository, times(1)).findEntryByPlayer(player.getPlayer());
        verify(playerRepository, times(1)).deletePlayer(player);
    }

    @DisplayName("Testando a exclusao de um jogador nao cadastrado")
    @Test
    void whenNotRegisteredPlayerExclusionIsCalledThenAnExceptionShouldBeThrown() {
        Player player = new Player("Jogador 4");

        when(playerRepository.getPlayerByPlayer(player.getPlayer())).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> gameService.deletePlayer(player.getPlayer()));
    }

    @DisplayName("Testando a exclusao de um jogador que esteja registrado em um jogo atual")
    @Test
    void whenRegisteredPlayerOnActualGameExclusionIsCalledThenAnExceptionShouldBeThrown() {
        Player player = new Player("Jogador 1");

        when(playerRepository.getPlayerByPlayer(player.getPlayer())).thenReturn(Optional.of(player));
        when(moveEntryRepository.findEntryByPlayer(player.getPlayer())).thenReturn(true);

        assertThrows(DataConflictException.class, () -> gameService.deletePlayer(player.getPlayer()));
    }

    @DisplayName("Testando a criação de uma jogada")
    @Test
    void whenMoveShouldBeCreated() throws DataConflictException, BadRequestException {
        Move move = new Move("Jogada Papel");

        when(moveRepository.getMoveByMove(move.getMove())).thenReturn(Optional.empty());
        doNothing().when(moveRepository).addMove(move);

        gameService.createMove(move.getMove());

        verify(moveRepository, times(1)).getMoveByMove(move.getMove());
        verify(moveRepository, times(1)).addMove(move);
    }

    @DisplayName("Testando a criaçao de uma jogada ja cadastrada")
    @Test
    void whenMoveAlreadyRegisteredThenAnExceptionShouldBeThrown() {
        Move move = new Move("Jogada Pedra");

        when(moveRepository.getMoveByMove(move.getMove())).thenReturn(Optional.of(move));

        assertThrows(DataConflictException.class, () -> gameService.createMove(move.getMove()));
    }

    @DisplayName("Testando a criaçao de uma jogada invalida")
    @Test
    void whenInvalidMoveInformedThenAnExceptionShouldBeThrown() {
        Move move = new Move("Jogada Melancia");

        when(moveRepository.getMoveByMove(move.getMove())).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> gameService.createMove(move.getMove()));
    }

    @DisplayName("Testando a busca de jogadas")
    @Test
    void whenListMovesIsCalledThenReturnAListOfMoves() throws DataNotFoundException {
        Move move = new Move("Jogada Pedra");

        when(moveRepository.getMoves()).thenReturn(Optional.of(Collections.singletonList(move)));

        List<Move> moves = gameService.findMoves();

        assertEquals(moves.get(0), move);
    }

    @DisplayName("Testando a busca de jogadas retornando uma lista vazia")
    @Test
    void whenListMovesIsCalledThenReturnAEmptyListOfMoves() throws DataNotFoundException {
        when(moveRepository.getMoves()).thenReturn(Optional.of(Collections.EMPTY_LIST));

        List<Move> moves = gameService.findMoves();

        assertEquals(moves, Collections.EMPTY_LIST);
    }

    @DisplayName("Testando a busca de jogada por nome")
    @Test
    void whenMoveNameIsGivenThenReturnAMove() throws DataNotFoundException {
        Move move = new Move("Jogada Pedra");

        when(moveRepository.getMoveByMove(move.getMove())).thenReturn(Optional.of(move));

        Move m = gameService.findByMove(move.getMove());

        assertEquals(m, move);
    }

    @DisplayName("Testando a busca de jogada por nome nao cadastrada")
    @Test
    void whenNotRegisteredMoveNameIsGivenThenThrownAnException() {
        when(moveRepository.getMoveByMove("Jogada Papel")).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> gameService.findByMove("Jogada Papel"));
    }

    @DisplayName("Testando a exclusao de uma jogada")
    @Test
    void whenExclusionIsCalledThenAMoveShouldBeDeleted() throws DataConflictException, DataNotFoundException {
        Move move = new Move("Jogada Spock");

        when(moveRepository.getMoveByMove(move.getMove())).thenReturn(Optional.of(move));
        when(moveEntryRepository.findEntryByMove(move.getMove())).thenReturn(false);
        doNothing().when(moveRepository).deleteMove(move);

        gameService.deleteMove(move.getMove());

        verify(moveRepository, times(1)).getMoveByMove(move.getMove());
        verify(moveEntryRepository, times(1)).findEntryByMove(move.getMove());
        verify(moveRepository, times(1)).deleteMove(move);
    }

    @DisplayName("Testando a exclusao de uma jogada nao cadastrada")
    @Test
    void whenNotRegisteredMoveExclusionIsCalledThenAnExceptionShouldBeThrown() {
        Move move = new Move("Jogada Papel");

        when(moveRepository.getMoveByMove(move.getMove())).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> gameService.deleteMove(move.getMove()));
    }

    @DisplayName("Testando a exclusao de uma jogada que esteja registrada em um jogo atual")
    @Test
    void whenRegisteredMoveOnActualGameExclusionIsCalledThenAnExceptionShouldBeThrown() {
        Move move = new Move("Jogada Pedra");

        when(moveRepository.getMoveByMove(move.getMove())).thenReturn(Optional.of(move));
        when(moveEntryRepository.findEntryByMove(move.getMove())).thenReturn(true);

        assertThrows(DataConflictException.class, () -> gameService.deleteMove(move.getMove()));
    }

    @DisplayName("Testando a inserção de uma entrada de jogo")
    @Test
    void whenMoveEntryShouldBeCreated() throws DataConflictException, BadRequestException, DataNotFoundException {
        String me = "Jogador 3 e Jogada Spock";
        Player player = new Player("Jogador 3");
        Move move = new Move("Jogada Spock");
        MoveEntry moveEntry = new MoveEntry(player.getPlayer(), move.getMove());

        when(playerRepository.findPlayer(moveEntry.getPlayer())).thenReturn(true);
        when(moveRepository.findMove(moveEntry.getMove())).thenReturn(true);
        doNothing().when(moveEntryRepository).addEntry(moveEntry);

        String result = gameService.insertMoveEntry(me);

        assertEquals(result, "");
    }

    @DisplayName("Testando uma entrada invalida")
    @Test
    void whenInvalidMoveEntryThenAnExceptionShouldBeThrown() {
        String me = "Teste";

        assertThrows(BadRequestException.class, () -> gameService.insertMoveEntry(me));
    }

    @DisplayName("Testando uma entrada de jogo com jogador nao cadastrado")
    @Test
    void whenMoveEntryWithoutRegisteredPlayerIsInformedThenAnExceptionShouldBeThrown() {
        String me = "Jogador 4 e Jogada Spock";

        assertThrows(DataNotFoundException.class, () -> gameService.insertMoveEntry(me));
    }

    @DisplayName("Testando uma entrada de jogo com jogador que ja fez sua jogada")
    @Test
    void whenMoveEntryWithPlayerAlreadyPlayedIsInformedThenAnExceptionShouldBeThrown() {
        String me = "Jogador 1 e Jogada Spock";
        Player player = new Player("Jogador 1");
        Move move = new Move("Jogada Spock");
        MoveEntry moveEntry = new MoveEntry(player.getPlayer(), move.getMove());

        when(playerRepository.findPlayer(moveEntry.getPlayer())).thenReturn(true);
        when(moveEntryRepository.findPlayerOnEntries(moveEntry.getPlayer())).thenReturn(true);

        assertThrows(DataConflictException.class, () -> gameService.insertMoveEntry(me));
    }

    @DisplayName("Testando uma entrada de jogo com jogada nao cadastrada")
    @Test
    void whenMoveEntryWithoutRegisteredMoveIsInformedThenAnExceptionShouldBeThrown() {
        String me = "Jogador 3 e Jogada Papel";

        assertThrows(DataNotFoundException.class, () -> gameService.insertMoveEntry(me));
    }

    @DisplayName("Testando uma entrada Jogar para que o jogo seja realizado")
    @Test
    void whenMoveEntryIsPlayThenReturnAGameResult() throws BadRequestException, DataNotFoundException, DataConflictException {
        String me = "Jogar";
        List<MoveEntry> entries = new ArrayList<>();
        entries.add(new MoveEntry("Jogador 1", "Jogada Pedra"));
        entries.add(new MoveEntry("Jogador 2", "Jogada Tesoura"));
        MoveEntry winner = new MoveEntry("Jogador 1", "Jogada Pedra");

        when(moveEntryRepository.countEntries()).thenReturn(2);
        when(moveEntryRepository.findMoveOnEntries("Spock")).thenReturn(false);
        when(moveEntryRepository.findMoveOnEntries("Tesoura")).thenReturn(true);
        when(moveEntryRepository.findMoveOnEntries("Papel")).thenReturn(false);
        when(moveEntryRepository.findMoveOnEntries("Pedra")).thenReturn(true);
        when(moveEntryRepository.findMoveOnEntries("Lagarto")).thenReturn(false);
        when(moveEntryRepository.getWinnerMove("Pedra")).thenReturn(Collections.singletonList(winner));
        when(gameRepository.lastGame()).thenReturn(-1);
        when(moveEntryRepository.getEntries()).thenReturn(Optional.of(entries));
        doNothing().when(gameRepository).addGame(0, entries);
        doNothing().when(moveEntryRepository).clearEntries();

        String result = gameService.insertMoveEntry(me);

        assertEquals(result, "Resultado Jogador 1 Vitória");
    }

    @DisplayName("Testando uma entrada Jogar que resulte em jogo empatado")
    @Test
    void whenMoveEntryIsPlayThenReturnADrawResult() throws BadRequestException, DataNotFoundException, DataConflictException {
        String me = "Jogar";
        List<MoveEntry> entries = new ArrayList<>();
        entries.add(new MoveEntry("Jogador 1", "Jogada Pedra"));
        entries.add(new MoveEntry("Jogador 2", "Jogada Tesoura"));
        entries.add(new MoveEntry("Jogador 3", "Jogada Papel"));

        when(moveEntryRepository.countEntries()).thenReturn(3);
        when(moveEntryRepository.findMoveOnEntries("Spock")).thenReturn(false);
        when(moveEntryRepository.findMoveOnEntries("Tesoura")).thenReturn(true);
        when(moveEntryRepository.findMoveOnEntries("Papel")).thenReturn(true);
        when(moveEntryRepository.findMoveOnEntries("Pedra")).thenReturn(true);
        when(moveEntryRepository.findMoveOnEntries("Lagarto")).thenReturn(false);
        when(gameRepository.lastGame()).thenReturn(-1);
        when(moveEntryRepository.getEntries()).thenReturn(Optional.of(entries));
        doNothing().when(gameRepository).addGame(0, entries);
        doNothing().when(moveEntryRepository).clearEntries();

        String result = gameService.insertMoveEntry(me);

        assertEquals(result, "Resultado Empate");
    }

    @DisplayName("Testando uma entrada Jogar com apenas 1 jogador participando")
    @Test
    void whenMoveEntryIsPlayWithOnePlayerThenAnExceptionShouldBeThrown() {
        String me = "Jogar";

        when(moveEntryRepository.countEntries()).thenReturn(1);

        assertThrows(BadRequestException.class, () -> gameService.insertMoveEntry(me));
    }

    @DisplayName("Testando uma entrada Jogar com jogo nao ativo")
    @Test
    void whenMoveEntryIsPlayWithoutGameThenAnExceptionShouldBeThrown() {
        String me = "Jogar";

        MoveEntry winner = new MoveEntry("Jogador 1", "Jogada Pedra");

        when(moveEntryRepository.countEntries()).thenReturn(2);
        when(moveEntryRepository.findMoveOnEntries("Spock")).thenReturn(false);
        when(moveEntryRepository.findMoveOnEntries("Tesoura")).thenReturn(true);
        when(moveEntryRepository.findMoveOnEntries("Papel")).thenReturn(false);
        when(moveEntryRepository.findMoveOnEntries("Pedra")).thenReturn(true);
        when(moveEntryRepository.findMoveOnEntries("Lagarto")).thenReturn(false);
        when(moveEntryRepository.getWinnerMove("Pedra")).thenReturn(Collections.singletonList(winner));
        when(gameRepository.lastGame()).thenReturn(-1);
        when(moveEntryRepository.getEntries()).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> gameService.insertMoveEntry(me));
    }

    @DisplayName("Testando a busca de jogos")
    @Test
    void whenListGamesIsCalledThenReturnAListOfGames() {
        List<MoveEntry> entries = new ArrayList<>();
        entries.add(new MoveEntry("Jogador 1", "Jogada Pedra"));
        entries.add(new MoveEntry("Jogador 2", "Jogada Tesoura"));
        Map<Integer, List<MoveEntry>> game = new HashMap<>();
        game.put(0, entries);

        when(gameRepository.findGames()).thenReturn(game);

        Map<Integer, List<MoveEntry>> result = gameService.findGames();

        assertEquals(result.get(0), game.get(0));
    }

    @DisplayName("Testando a busca de jogos retornando uma lista vazia")
    @Test
    void whenListGamesIsCalledThenReturnAEmptyListOfGames() {
        Map<Integer, List<MoveEntry>> games = new HashMap<>();

        when(gameRepository.findGames()).thenReturn(games);

        Map<Integer, List<MoveEntry>> result = gameService.findGames();

        assertEquals(result, games);
    }

    @DisplayName("Testando a busca de jogo por id")
    @Test
    void whenGameIdIsGivenThenReturnAGame() throws DataNotFoundException {
        Integer id = 0;
        List<MoveEntry> entries = new ArrayList<>();
        entries.add(new MoveEntry("Jogador 1", "Jogada Pedra"));
        entries.add(new MoveEntry("Jogador 2", "Jogada Tesoura"));
        Map<Integer, List<MoveEntry>> game = new HashMap<>();
        game.put(id, entries);

        when(gameRepository.findGameById(id)).thenReturn(game.entrySet().stream().findAny());

        List<MoveEntry> result = gameService.findGameById(id);

        assertEquals(result, game.get(0));
    }

    @DisplayName("Testando a busca de jogo por id nao cadastrado")
    @Test
    void whenNotRegisteredGameIdIsGivenThenThrownAnException() {
        Integer id = 0;

        when(gameRepository.findGameById(id)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> gameService.findGameById(id));
    }

    @DisplayName("Testando a exclusao de um jogo")
    @Test
    void whenExclusionIsCalledThenAGameShouldBeDeleted() throws DataNotFoundException {
        Integer id = 0;
        List<MoveEntry> entries = new ArrayList<>();
        entries.add(new MoveEntry("Jogador 1", "Jogada Pedra"));
        entries.add(new MoveEntry("Jogador 2", "Jogada Tesoura"));
        Map<Integer, List<MoveEntry>> game = new HashMap<>();
        game.put(id, entries);

        when(gameRepository.findGameById(id)).thenReturn(game.entrySet().stream().findAny());
        doNothing().when(gameRepository).deleteGame(id);

        gameService.deleteGame(id);

        verify(gameRepository, times(1)).findGameById(id);
        verify(gameRepository, times(1)).deleteGame(id);
    }

    @DisplayName("Testando a exclusao de um jogo nao cadastrado")
    @Test
    void whenNotRegisteredGameExclusionIsCalledThenAnExceptionShouldBeThrown() {
        Integer id = 0;

        when(gameRepository.findGameById(id)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> gameService.deleteGame(id));
    }

}
