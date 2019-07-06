package examen.services.rest;


import model.Game;
import examen.repository.IGameRepository;
import model.GamePlayerWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("examen")
public class GameController {

    @Autowired
    private IGameRepository gameRepo;

    @RequestMapping(value = "/games/{gameID}/players/{playerUsername}", method = RequestMethod.GET)
    public ResponseEntity<?> getGameAndPlayerDetails(@PathVariable("gameID") int gameID, @PathVariable("playerUsername") String playerUsername) {
        Game game = gameRepo.findGame(gameID);
        if (game == null)
            return new ResponseEntity<String>("Game not found", HttpStatus.NOT_FOUND);
        else {
            GamePlayerWrapper response = gameRepo.getWrappedDetailsOfPlayer(game, playerUsername);
            if (response == null)
                return new ResponseEntity<String>("Information for this player not found", HttpStatus.NOT_FOUND);
            else
                return new ResponseEntity<GamePlayerWrapper>(response, HttpStatus.OK);
        }
    }
}
