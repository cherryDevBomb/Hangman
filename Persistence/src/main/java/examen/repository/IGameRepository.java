package examen.repository;

import model.Game;
import model.GameDetails;
import model.GamePlayerWrapper;

import java.util.List;

public interface IGameRepository {

    void saveGame(Game game);
    void updateGame(Game game);
    Game findGame(int id);
    List<Game> getAllGames();
    GameDetails getDetailsOfPlayer(Game game, String playerId);
    GamePlayerWrapper getWrappedDetailsOfPlayer(Game game, String playerId);
}
