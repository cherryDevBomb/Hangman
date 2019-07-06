package services;

import model.Player;

import java.util.List;

public interface IGameServices {

    void login(Player player, IObserver client) throws GameException;
    void gameStartPressed(Player player, IObserver client) throws GameException;
    List<String> getPlayersOfCurrentGame() throws GameException;
    void verifyTurn(Player player, String input) throws GameException;
    void gameEnded(Player player) throws GameException;
    void gameWon(Player player);
    void logout(Player player) throws GameException;
}
