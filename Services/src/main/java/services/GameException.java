package services;

public class GameException extends Exception {

    public GameException() {}

    public GameException(String message) {
        super(message);
    }

    public GameException(String message, Throwable cause) {
        super(message, cause);
    }
}
