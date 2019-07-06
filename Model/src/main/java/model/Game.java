package model;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private int id;
    private String word;
    private String winner;
    private List<GameDetails> details;

    public Game() {
    }

    public Game(int id) {
        this.id = id;
        details = new ArrayList<>();
    }

    public Game(int id, String word) {
        this.id = id;
        this.word = word;
        details = new ArrayList<>();
    }

    public Game(int id, String word, String winner, List<GameDetails> details) {
        this.id = id;
        this.word = word;
        this.winner = winner;
        this.details = details;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public List<GameDetails> getDetails() {
        return details;
    }

    public void setDetails(List<GameDetails> details) {
        this.details = details;
    }

    public void addDetail(GameDetails det) {
        details.add(det);
    }
}
