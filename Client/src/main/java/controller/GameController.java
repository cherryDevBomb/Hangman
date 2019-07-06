package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Player;
import services.GameException;
import services.IGameServices;
import services.IObserver;
import view.LoginView;
import view.MainView;
import view.StartGameView;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class GameController extends UnicastRemoteObject implements IObserver, Serializable {

    private IGameServices server;
    private Stage primaryStage;
    private Player player;

    @FXML
    private Label waitingLbl;
    @FXML
    private Label youLbl;
    @FXML
    private ListView<String> allPlayersListView;
    @FXML
    private Label wordLbl;
    @FXML
    private Label wrongLettersLbl;
    @FXML
    private Label messageLbl;
    @FXML
    private TextField guessTextField;
    @FXML
    private Button doTurnBtn;
    @FXML
    private Button gameOverBtn;
    @FXML
    private Label winnerLbl;
    @FXML
    private Button logoutBtn;

    private String word;
    private Set<Character> correctlyGuessed;
    private Set<Character> wronglyGuessed;

    GameController() throws RemoteException {
        primaryStage = new Stage();
        primaryStage.setTitle("Game");
    }

    void setServer(IGameServices server) {
        this.server = server;
    }

    void setPlayer(Player player) {
        this.player = player;
    }

    void loggedIn() {
        try {
            StartGameView startView = new StartGameView(this);
            primaryStage.setScene(startView.getStartGameScene());
        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage.show();
    }


    /**
     *button click handler
     */
    public void gameStartPressed() throws GameException, RemoteException {
        System.out.println("Start game pressed");
        server.gameStartPressed(player, this);
    }


    /**
     * load gameView callback
     */
    @Override
    public void gameOn(String word) {
        Platform.runLater(() -> {
            try {
                MainView gameView = new MainView(this);
                primaryStage.setScene(gameView.getGameScene());

                correctlyGuessed = new HashSet<>();
                wronglyGuessed = new HashSet<>();

                //set word
                this.word = word;
                wordLbl.setText(getWordRepresentation());

                wrongLettersLbl.setText("");
                logoutBtn.setDisable(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * load all players callback
     */
    @Override
    public void loadAllPlayers() {
        Platform.runLater(() -> {
            try {
                youLbl.setText("Logged in as " + player.getUsername());
                List<String> allPlayers = server.getPlayersOfCurrentGame();
                ObservableList<String> obsList = FXCollections.observableList(allPlayers);
                allPlayersListView.setItems(obsList);
            } catch (GameException e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * pending state notification callback
     */
    @Override
    public void waitForGame() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "You have to wait until the current game is completed or enough users are logged in", ButtonType.OK);
            alert.setHeaderText("Can't start game");
            alert.show();
        });
    }


    /**
     * refresh view callback
     */
    @Override
    public void refresh(String thisTurnPlayer, boolean guessed, String input) {
        Platform.runLater(() -> {
            guessTextField.setText("");
            if (!guessed) {
                if (input.length() > 0) {
                    wronglyGuessed.add(input.charAt(0));
                    String newWrong = wrongLettersLbl.getText() + " " + input;
                    wrongLettersLbl.setText(newWrong);
                    messageLbl.setText("Wrong!");
                }
            }
            else {
                correctlyGuessed.add(input.charAt(0));
                String newWordRepresentation = getWordRepresentation();
                wordLbl.setText(newWordRepresentation);
                messageLbl.setText("Right!");
            }
            //check if player has next turn
            if (thisTurnPlayer.equals(player.getUsername())) {
                allPlayersListView.getSelectionModel().select(player.getUsername());
                doTurnBtn.setDisable(false);
                guessTextField.setDisable(false);
            } else {
                System.out.println(allPlayersListView);
                allPlayersListView.getSelectionModel().select(thisTurnPlayer);
                doTurnBtn.setDisable(true);
                guessTextField.setDisable(true);
            }
        });
    }


    /**
     * game logic of a turn
     */
    public void doTurn() {
        //limit textfield length and allow only letters
        String input = guessTextField.getText();
        if (input.matches("[a-z]")
                && input.length() == 1
                && !correctlyGuessed.contains(input.charAt(0))
                && !wronglyGuessed.contains(input.charAt(0))) {
            System.out.println(player + " did turn");
            try {
                server.verifyTurn(player, input);
            } catch (GameException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * get word representation
     */
    private String getWordRepresentation() {
        int nr = word.length();
        StringBuilder w = new StringBuilder();
        for (int i = 0; i < nr; i++) {
            if (correctlyGuessed.contains(word.charAt(i)))
                w.append(word.charAt(i)).append(" ");
            else
                w.append("_ ");
        }
        return w.toString();
    }


    /**
     * game over callback
     */
    @Override
    public void gameOver(String winner) {
        Platform.runLater(() -> {
            try {
                winnerLbl.setText(winner + " won");
                winnerLbl.setVisible(true);
                gameOverBtn.setVisible(true);
                doTurnBtn.setDisable(true);
                logoutBtn.setDisable(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * event handler for button click
     */
    public void reloadStartPage() {
        try {
            server.gameEnded(player);
            StartGameView startGameView = new StartGameView(this);
            primaryStage.setScene(startGameView.getStartGameScene());
        } catch (IOException | GameException e) {
            e.printStackTrace();
        }
    }


    /**
     * event handler for button click
     */
    public void logout() {
        try {
            server.logout(player);
            LoginView login = new LoginView(server);
            Stage stage = new Stage();
            stage.setScene(login.getLoginScene());
            stage.setTitle("Game");
            stage.show();
            allPlayersListView.getScene().getWindow().hide();
        } catch (IOException | GameException e) {
            e.printStackTrace();
        }
    }
}
