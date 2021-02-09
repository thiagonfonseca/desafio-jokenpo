package br.com.desafio.jokenpo.controller;

import br.com.desafio.jokenpo.entity.Move;
import br.com.desafio.jokenpo.entity.MoveEntry;
import br.com.desafio.jokenpo.entity.Player;
import br.com.desafio.jokenpo.exception.BadRequestException;
import br.com.desafio.jokenpo.exception.DataConflictException;
import br.com.desafio.jokenpo.exception.DataNotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api("Gerenciamento do Jogo JOKENPO")
public interface GameControllerDocs {

    @ApiOperation(value = "Registro de um novo jogador")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Jogador criado com sucesso"),
            @ApiResponse(code = 409, message = "O Jogador já está cadastrado")
    })
    ResponseEntity<Void> createPlayer(@RequestBody String player) throws DataConflictException;

    @ApiOperation(value = "Retorna uma lista de jogadores cadastrados")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Lista de jogadores cadastrados"),
            @ApiResponse(code = 404, message = "Não há jogadores cadastrados")
    })
    ResponseEntity<List<Player>> findPlayers() throws DataNotFoundException;

    @ApiOperation(value = "Retorna um jogador registrado pelo nome")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Jogador encontrado com sucesso"),
            @ApiResponse(code = 404, message = "O jogador não está cadastrado")
    })
    ResponseEntity<Player> findPlayer(@PathVariable String player) throws DataNotFoundException;

    @ApiOperation(value = "Exclui um jogador pelo seu nome")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Jogador excluído com sucesso"),
            @ApiResponse(code = 404, message = "O jogador não está cadastrado"),
            @ApiResponse(code = 409, message = "O jogador está registrado no jogo atual")
    })
    void deletePlayer(@PathVariable String player) throws DataConflictException, DataNotFoundException;

    @ApiOperation(value = "Registro de uma nova jogada")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Jogada criada com sucesso"),
            @ApiResponse(code = 400, message = "Você pode cadastrar apenas os movimentos Spock, Tesoura, Papel, Pedra e Lagarto"),
            @ApiResponse(code = 409, message = "A jogada já está cadastrada")
    })
    ResponseEntity<Void> createMove(@RequestBody String move) throws DataConflictException, BadRequestException;

    @ApiOperation(value = "Retorna uma lista de jogadas cadastradas")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Lista de jogadas cadastradas"),
            @ApiResponse(code = 404, message = "Não há jogadas cadastradas")
    })
    ResponseEntity<List<Move>> findMoves() throws DataNotFoundException;

    @ApiOperation(value = "Retorna uma jogada registrada pelo nome")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Jogada encontrada com sucesso"),
            @ApiResponse(code = 404, message = "A jogada não está cadastrada")
    })
    ResponseEntity<Move> findMove(@PathVariable String move) throws DataNotFoundException;

    @ApiOperation(value = "Exclui um jogada pelo seu nome")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Jogada excluída com sucesso"),
            @ApiResponse(code = 404, message = "A jogada não está cadastrada"),
            @ApiResponse(code = 409, message = "Esta jogada está registrada no jogo atual")
    })
    void deleteMove(@PathVariable String move) throws DataConflictException, DataNotFoundException;

    @ApiOperation(value = "Registro de uma nova entrada de jogo (Utilizar <<Nome do Jogador e Jogada valida>> ou <<Jogar>> para realizar o jogo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Movimento executado ou resultado do jogo"),
            @ApiResponse(code = 400, message = "Jogada invalida ou o jogo possui menos que dois jogadores"),
            @ApiResponse(code = 404, message = "O jogador ou a jogada não está cadastrado"),
            @ApiResponse(code = 409, message = "O jogador já fez sua jogada")
    })
    ResponseEntity<String> insertMoveEntry(@RequestBody String moveEntry) throws BadRequestException,
            DataNotFoundException, DataConflictException;

    @ApiOperation(value = "Retorna uma lista de jogos cadastrados")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Lista de jogos cadastrados")
    })
    ResponseEntity<Map<Integer, List<MoveEntry>>> findGames();

    @ApiOperation(value = "Retorna um jogo registrada pelo id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Jogo encontrado com sucesso"),
            @ApiResponse(code = 404, message = "Este jogo não está cadastrado")
    })
    ResponseEntity<List<MoveEntry>> findGame(@PathVariable Integer id) throws DataNotFoundException;

    @ApiOperation(value = "Exclui um jogo pelo seu id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Jogo excluído com sucesso"),
            @ApiResponse(code = 404, message = "Este jogo não está cadastrado")
    })
    void deleteGame(@PathVariable Integer id) throws DataNotFoundException;

}
