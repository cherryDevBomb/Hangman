package model;

import java.util.List;

public class GamePlayerWrapper {

    private String username;
    private int gameId;
    private List<Character> triedLetters;
    private List<Character> guessedLetters;
    private boolean won;

    public GamePlayerWrapper() {
    }

    public GamePlayerWrapper(String username, int gameId, List<Character> triedLetters, List<Character> guessedLetters, boolean won) {
        this.username = username;
        this.gameId = gameId;
        this.triedLetters = triedLetters;
        this.guessedLetters = guessedLetters;
        this.won = won;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public List<Character> getTriedLetters() {
        return triedLetters;
    }

    public void setTriedLetters(List<Character> triedLetters) {
        this.triedLetters = triedLetters;
    }

    public List<Character> getGuessedLetters() {
        return guessedLetters;
    }

    public void setGuessedLetters(List<Character> guessedLetters) {
        this.guessedLetters = guessedLetters;
    }

    public boolean isWon() {
        return won;
    }

    public void setWon(boolean won) {
        this.won = won;
    }
}
