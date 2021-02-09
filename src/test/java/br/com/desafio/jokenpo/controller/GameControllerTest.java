package br.com.desafio.jokenpo.controller;

import br.com.desafio.jokenpo.entity.Move;
import br.com.desafio.jokenpo.entity.MoveEntry;
import br.com.desafio.jokenpo.entity.Player;
import br.com.desafio.jokenpo.exception.BadRequestException;
import br.com.desafio.jokenpo.exception.DataConflictException;
import br.com.desafio.jokenpo.exception.DataNotFoundException;
import br.com.desafio.jokenpo.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameControllerTest {

    private static final String API_URL_PATH_PLAYER = "/api/v1/jokenpo/player";
    private static final String API_URL_PATH_MOVE = "/api/v1/jokenpo/move";
    private static final String API_URL_PATH_PLAY = "/api/v1/jokenpo/play";

    private MockMvc mockMvc;

    @Mock
    private GameService gameService;

    @InjectMocks
    private GameController gameController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(gameController)
                .setViewResolvers((s, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @DisplayName("Testando a criação de um jogador")
    @Test
    void whenPOSTPlayerIsCalledThenAPlayerIsCreated() throws Exception {

        doNothing().when(gameService).createPlayer("Jogador 1");

        mockMvc.perform(post(API_URL_PATH_PLAYER)
                .contentType(MediaType.TEXT_PLAIN)
                .content("Jogador 1"))
                .andExpect(status().isCreated());
    }

    @DisplayName("Testando a criaçao de um jogador ja cadastrado")
    @Test
    void whenPOSTPlayerIsCalledThenAnErrorIsReturned() throws Exception {

        doThrow(DataConflictException.class).when(gameService).createPlayer("Jogador 1");

        mockMvc.perform(post(API_URL_PATH_PLAYER)
                .contentType(MediaType.TEXT_PLAIN)
                .content("Jogador 1"))
                .andExpect(status().isConflict());
    }

    @DisplayName("Testando a busca de jogadores")
    @Test
    void whenGETListWithPlayersIsCalledThenOkStatusIsReturned() throws Exception {
        Player player = new Player("Jogador 1");

        when(gameService.findPlayers()).thenReturn(Collections.singletonList(player));

        mockMvc.perform(get(API_URL_PATH_PLAYER)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].player", is(player.getPlayer())));
    }

    @DisplayName("Testando a busca de jogadores retornando uma lista vazia")
    @Test
    void whenGETListWithoutPlayersIsCalledThenOkStatusIsReturned() throws Exception {
        when(gameService.findPlayers()).thenReturn(Collections.EMPTY_LIST);

        mockMvc.perform(get(API_URL_PATH_PLAYER)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("Testando a busca de jogador por nome")
    @Test
    void whenGETIsCalledWithPlayerNameThenOkStatusIsReturned() throws Exception {
        Player player = new Player("Jogador 1");

        when(gameService.findByPlayer(player.getPlayer())).thenReturn(player);

        mockMvc.perform(get(API_URL_PATH_PLAYER + "/" + player.getPlayer())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.player", is(player.getPlayer())));
    }

    @DisplayName("Testando a busca de jogador por nome nao cadastrado")
    @Test
    void whenGETIsCalledWithoutPlayerNameThenNotFoundStatusIsReturned() throws Exception {
        when(gameService.findByPlayer("Jogador 1")).thenThrow(DataNotFoundException.class);

        mockMvc.perform(get(API_URL_PATH_PLAYER + "/Jogador 1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Testando a exclusao de um jogador")
    @Test
    void whenDELETEPlayerIsCalledWithRegisteredPlayerThenNoContentStatusIsReturned() throws Exception {
        doNothing().when(gameService).deletePlayer("Jogador 1");

        mockMvc.perform(delete(API_URL_PATH_PLAYER + "/Jogador 1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @DisplayName("Testando a exclusao de um jogador nao cadastrado")
    @Test
    void whenDELETEPlayerIsCalledWithoutRegisteredPlayerThenNotFoundStatusIsReturned() throws Exception {
        doThrow(DataNotFoundException.class).when(gameService).deletePlayer("Jogador 1");

        mockMvc.perform(delete(API_URL_PATH_PLAYER + "/Jogador 1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Testando a exclusao de um jogador que esteja registrado em um jogo atual")
    @Test
    void whenDELETEPlayerIsCalledWithRegisteredPlayerWithAnActualGameThenConflictStatusIsReturned() throws Exception {
        doThrow(DataConflictException.class).when(gameService).deletePlayer("Jogador 1");

        mockMvc.perform(delete(API_URL_PATH_PLAYER + "/Jogador 1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @DisplayName("Testando a criação de uma jogada")
    @Test
    void whenPOSTMoveIsCalledThenAMoveIsCreated() throws Exception {

        doNothing().when(gameService).createMove("Jogada Spock");

        mockMvc.perform(post(API_URL_PATH_MOVE)
                .contentType(MediaType.TEXT_PLAIN)
                .content("Jogada Spock"))
                .andExpect(status().isCreated());
    }

    @DisplayName("Testando a criaçao de uma jogada ja cadastrada")
    @Test
    void whenPOSTMoveIsCalledThenAnErrorIsReturned() throws Exception {

        doThrow(DataConflictException.class).when(gameService).createMove("Jogada Spock");

        mockMvc.perform(post(API_URL_PATH_MOVE)
                .contentType(MediaType.TEXT_PLAIN)
                .content("Jogada Spock"))
                .andExpect(status().isConflict());
    }

    @DisplayName("Testando a criaçao de uma jogada invalida")
    @Test
    void whenPOSTMoveWithInvalidMoveThenAnErrorIsReturned() throws Exception {

        doThrow(BadRequestException.class).when(gameService).createMove("Jogada Spock");

        mockMvc.perform(post(API_URL_PATH_MOVE)
                .contentType(MediaType.TEXT_PLAIN)
                .content("Jogada Spock"))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Testando a busca de jogadas")
    @Test
    void whenGETListWithMovesIsCalledThenOkStatusIsReturned() throws Exception {
        Move move = new Move("Jogada Spock");

        when(gameService.findMoves()).thenReturn(Collections.singletonList(move));

        mockMvc.perform(get(API_URL_PATH_MOVE)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].move", is(move.getMove())));
    }

    @DisplayName("Testando a busca de jogadas retornando uma lista vazia")
    @Test
    void whenGETListWithoutMovesIsCalledThenOkStatusIsReturned() throws Exception {
        when(gameService.findMoves()).thenReturn(Collections.EMPTY_LIST);

        mockMvc.perform(get(API_URL_PATH_MOVE)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("Testando a busca de jogada por nome")
    @Test
    void whenGETIsCalledWithMoveNameThenOkStatusIsReturned() throws Exception {
        Move move = new Move("Jogada Spock");

        when(gameService.findByMove(move.getMove())).thenReturn(move);

        mockMvc.perform(get(API_URL_PATH_MOVE + "/" + move.getMove())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.move", is(move.getMove())));
    }

    @DisplayName("Testando a busca de jogada por nome nao cadastrada")
    @Test
    void whenGETIsCalledWithoutMoveNameThenNotFoundStatusIsReturned() throws Exception {
        when(gameService.findByMove("Jogada Spock")).thenThrow(DataNotFoundException.class);

        mockMvc.perform(get(API_URL_PATH_MOVE + "/Jogada Spock")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Testando a exclusao de uma jogada")
    @Test
    void whenDELETEMoveIsCalledWithRegisteredMoveThenNoContentStatusIsReturned() throws Exception {
        doNothing().when(gameService).deleteMove("Jogada Spock");

        mockMvc.perform(delete(API_URL_PATH_MOVE + "/Jogada Spock")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @DisplayName("Testando a exclusao de uma jogada nao cadastrada")
    @Test
    void whenDELETEMoveIsCalledWithoutRegisteredMoveThenNotFoundStatusIsReturned() throws Exception {
        doThrow(DataNotFoundException.class).when(gameService).deleteMove("Jogada Spock");

        mockMvc.perform(delete(API_URL_PATH_MOVE + "/Jogada Spock")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Testando a exclusao de uma jogada que esteja registrada em um jogo atual")
    @Test
    void whenDELETEMoveIsCalledWithRegisteredMoveWithAnActualGameThenConflictStatusIsReturned() throws Exception {
        doThrow(DataConflictException.class).when(gameService).deleteMove("Jogada Spock");

        mockMvc.perform(delete(API_URL_PATH_MOVE + "/Jogada Spock")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @DisplayName("Testando a inserção de uma entrada de jogo")
    @Test
    void whenPOSTPlayIsCalledThenAMoveEntryIsCreated() throws Exception {

        when(gameService.insertMoveEntry("Jogador 1 e Jogada Pedra")).thenReturn("");

        mockMvc.perform(post(API_URL_PATH_PLAY)
                .contentType(MediaType.TEXT_PLAIN)
                .content("Jogador 1 e Jogada Pedra"))
                .andExpect(status().isOk());
    }

    @DisplayName("Testando uma entrada invalida")
    @Test
    void whenPOSTPlayWithInvalidMoveEntryThenAnErrorIsReturned() throws Exception {

        doThrow(BadRequestException.class).when(gameService).insertMoveEntry("Teste");

        mockMvc.perform(post(API_URL_PATH_PLAY)
                .contentType(MediaType.TEXT_PLAIN)
                .content("Teste"))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Testando uma entrada de jogo com jogador nao cadastrado")
    @Test
    void whenPOSTPlayWithoutRegisteredPlayerThenAnErrorIsReturned() throws Exception {

        doThrow(DataNotFoundException.class).when(gameService).insertMoveEntry("Jogador 5 e Jogada Spock");

        mockMvc.perform(post(API_URL_PATH_PLAY)
                .contentType(MediaType.TEXT_PLAIN)
                .content("Jogador 5 e Jogada Spock"))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Testando uma entrada de jogo com jogador que ja fez sua jogada")
    @Test
    void whenPOSTPlayWithPlayerAlreadyPlayedIsInformedThenAnErrorIsReturned() throws Exception {

        doThrow(DataConflictException.class).when(gameService).insertMoveEntry("Jogador 1 e Jogada Spock");

        mockMvc.perform(post(API_URL_PATH_PLAY)
                .contentType(MediaType.TEXT_PLAIN)
                .content("Jogador 1 e Jogada Spock"))
                .andExpect(status().isConflict());
    }

    @DisplayName("Testando uma entrada de jogo com jogada nao cadastrada")
    @Test
    void whenPOSTPlayWithoutRegisteredMoveThenAnErrorIsReturned() throws Exception {

        doThrow(DataNotFoundException.class).when(gameService).insertMoveEntry("Jogador 1 e Jogada Melancia");

        mockMvc.perform(post(API_URL_PATH_PLAY)
                .contentType(MediaType.TEXT_PLAIN)
                .content("Jogador 1 e Jogada Melancia"))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Testando uma entrada Jogar para que o jogo seja realizado")
    @Test
    void whenPOSTPlayWithMoveEntryIsPlayThenReturnAGameResult() throws Exception {
        when(gameService.insertMoveEntry("Jogar")).thenReturn("Resultado Jogador 1 Vitória");

        mockMvc.perform(post(API_URL_PATH_PLAY)
                .contentType(MediaType.TEXT_PLAIN)
                .content("Jogar"))
                .andExpect(status().isOk());
    }

    @DisplayName("Testando uma entrada Jogar com apenas 1 jogador participando")
    @Test
    void whenPOSTPlayWithOnePlayerThenAnErrorIsReturned() throws Exception {

        doThrow(BadRequestException.class).when(gameService).insertMoveEntry("Jogar");

        mockMvc.perform(post(API_URL_PATH_PLAY)
                .contentType(MediaType.TEXT_PLAIN)
                .content("Jogar"))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Testando uma entrada Jogar com jogo nao ativo")
    @Test
    void whenPOSTPlayWithoutGameThenAnErrorIsReturned() throws Exception {
        doThrow(DataNotFoundException.class).when(gameService).insertMoveEntry("Jogar");

        mockMvc.perform(post(API_URL_PATH_PLAY)
                .contentType(MediaType.TEXT_PLAIN)
                .content("Jogar"))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Testando a busca de jogos")
    @Test
    void whenGETListWithGamesIsCalledThenOkStatusIsReturned() throws Exception {
        List<MoveEntry> entries = new ArrayList<>();
        entries.add(new MoveEntry("Jogador 1", "Jogada Pedra"));
        entries.add(new MoveEntry("Jogador 2", "Jogada Tesoura"));
        Map<Integer, List<MoveEntry>> game = new HashMap<>();
        game.put(0, entries);

        when(gameService.findGames()).thenReturn(game);

        mockMvc.perform(get(API_URL_PATH_PLAY)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.0[0].player", is(game.get(0).get(0).getPlayer())))
                .andExpect(jsonPath("$.0[0].move", is(game.get(0).get(0).getMove())))
                .andExpect(jsonPath("$.0[1].player", is(game.get(0).get(1).getPlayer())))
                .andExpect(jsonPath("$.0[1].move", is(game.get(0).get(1).getMove())));
    }

    @DisplayName("Testando a busca de jogos retornando uma lista vazia")
    @Test
    void whenGETListWithoutGamesIsCalledThenOkStatusIsReturned() throws Exception {
        when(gameService.findGames()).thenReturn(new HashMap<>());

        mockMvc.perform(get(API_URL_PATH_PLAY)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("Testando a busca de jogo por id")
    @Test
    void whenGETIsCalledWithGameIdThenOkStatusIsReturned() throws Exception {
        Integer id = 0;
        List<MoveEntry> entries = new ArrayList<>();
        entries.add(new MoveEntry("Jogador 1", "Jogada Pedra"));
        entries.add(new MoveEntry("Jogador 2", "Jogada Tesoura"));

        when(gameService.findGameById(id)).thenReturn(entries);

        mockMvc.perform(get(API_URL_PATH_PLAY + "/" + id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].player", is("Jogador 1")))
                .andExpect(jsonPath("$[0].move", is("Jogada Pedra")))
                .andExpect(jsonPath("$[1].player", is("Jogador 2")))
                .andExpect(jsonPath("$[1].move", is("Jogada Tesoura")));
    }

    @DisplayName("Testando a busca de jogo por id nao cadastrado")
    @Test
    void whenGETIsCalledWithoutGameIdThenNotFoundStatusIsReturned() throws Exception {
        when(gameService.findGameById(0)).thenThrow(DataNotFoundException.class);

        mockMvc.perform(get(API_URL_PATH_PLAY + "/0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Testando a exclusao de um jogo")
    @Test
    void whenDELETEGameIsCalledWithRegisteredGameThenNoContentStatusIsReturned() throws Exception {
        doNothing().when(gameService).deleteGame(0);

        mockMvc.perform(delete(API_URL_PATH_PLAY + "/0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @DisplayName("Testando a exclusao de um jogo nao cadastrado")
    @Test
    void whenDELETEGameIsCalledWithoutRegisteredGameThenNotFoundStatusIsReturned() throws Exception {
        doThrow(DataNotFoundException.class).when(gameService).deleteGame(0);

        mockMvc.perform(delete(API_URL_PATH_PLAY + "/0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
