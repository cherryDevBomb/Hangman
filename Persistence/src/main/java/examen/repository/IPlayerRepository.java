package examen.repository;

import model.Player;

public interface IPlayerRepository {

    Player findPlayer(String username);
    boolean findByUsernameAndPassword(String username, String password);
}
