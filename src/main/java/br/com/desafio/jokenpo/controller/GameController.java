package br.com.desafio.jokenpo.controller;

import br.com.desafio.jokenpo.entity.Move;
import br.com.desafio.jokenpo.entity.MoveEntry;
import br.com.desafio.jokenpo.entity.Player;
import br.com.desafio.jokenpo.exception.BadRequestException;
import br.com.desafio.jokenpo.exception.DataConflictException;
import br.com.desafio.jokenpo.exception.DataNotFoundException;
import br.com.desafio.jokenpo.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/jokenpo")
public class GameController implements GameControllerDocs {

    @Autowired
    private GameService gameService;

    @PostMapping("/player")
    public ResponseEntity<Void> createPlayer(@RequestBody String player) throws DataConflictException {
        gameService.createPlayer(player);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/player")
    public ResponseEntity<List<Player>> findPlayers() throws DataNotFoundException {
        return new ResponseEntity<>(gameService.findPlayers(), HttpStatus.OK);
    }

    @GetMapping("/player/{player}")
    public ResponseEntity<Player> findPlayer(@PathVariable String player) throws DataNotFoundException {
        return new ResponseEntity<>(gameService.findByPlayer(player), HttpStatus.OK);
    }

    @DeleteMapping("/player/{player}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlayer(@PathVariable String player) throws DataConflictException, DataNotFoundException {
        gameService.deletePlayer(player);
    }

    @PostMapping("/move")
    public ResponseEntity<Void> createMove(@RequestBody String move) throws DataConflictException, BadRequestException {
        gameService.createMove(move);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/move")
    public ResponseEntity<List<Move>> findMoves() throws DataNotFoundException {
        return new ResponseEntity<>(gameService.findMoves(), HttpStatus.OK);
    }

    @GetMapping("/move/{move}")
    public ResponseEntity<Move> findMove(@PathVariable String move) throws DataNotFoundException {
        return new ResponseEntity<>(gameService.findByMove(move), HttpStatus.OK);
    }

    @DeleteMapping("/move/{move}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMove(@PathVariable String move) throws DataConflictException, DataNotFoundException {
        gameService.deleteMove(move);
    }

    @PostMapping("/play")
    public ResponseEntity<String> insertMoveEntry(@RequestBody String moveEntry) throws BadRequestException,
            DataNotFoundException, DataConflictException {
        String result = gameService.insertMoveEntry(moveEntry);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/play")
    public ResponseEntity<Map<Integer, List<MoveEntry>>> findGames() {
        return new ResponseEntity<>(gameService.findGames(), HttpStatus.OK);
    }

    @GetMapping("/play/{id}")
    public ResponseEntity<List<MoveEntry>> findGame(@PathVariable Integer id) throws DataNotFoundException {
        return new ResponseEntity<>(gameService.findGameById(id), HttpStatus.OK);
    }

    @DeleteMapping("/play/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGame(@PathVariable Integer id) throws DataNotFoundException {
        gameService.deleteGame(id);
    }

}
