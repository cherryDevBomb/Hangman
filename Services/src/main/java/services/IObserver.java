package services;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IObserver extends Remote{

    void gameOn(String word) throws GameException, RemoteException;
    void loadAllPlayers() throws GameException, RemoteException;
    void waitForGame() throws GameException, RemoteException;
    void refresh(String thisTurnPlayer, boolean guessed, String input) throws GameException, RemoteException;
    void gameOver(String winner) throws GameException, RemoteException;
}
