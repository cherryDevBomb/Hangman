package model;

public class GameDetails {

    private int id;
    private String player;
    private int result;
    private int attempts;
    private String triedLetters;
    private String guessedLetters;

    public GameDetails() {
    }

    public GameDetails(String player) {
        this.player = player;
    }

    public GameDetails(String player, int result) {
        this.player = player;
        this.result = result;
        this.attempts = 0;
    }

    public GameDetails(int id, String player) {
        this.id = id;
        this.player = player;
    }

    public GameDetails(int id, String player, int result, int attempts, String triedLetters, String guessedLetters) {
        this.id = id;
        this.player = player;
        this.result = result;
        this.attempts = attempts;
        this.triedLetters = triedLetters;
        this.guessedLetters = guessedLetters;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public String getTriedLetters() {
        return triedLetters;
    }

    public void setTriedLetters(String triedLetters) {
        this.triedLetters = triedLetters;
    }

    public String getGuessedLetters() {
        return guessedLetters;
    }

    public void setGuessedLetters(String guessedLetters) {
        this.guessedLetters = guessedLetters;
    }
}
