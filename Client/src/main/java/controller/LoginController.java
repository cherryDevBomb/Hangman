package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import model.Player;
import services.GameException;
import services.IGameServices;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class LoginController extends UnicastRemoteObject implements Serializable {

    private IGameServices server;

    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private Button loginBtn;

    public LoginController() throws RemoteException {
    }

    public void setServer(IGameServices server) {
        this.server = server;
    }

    @FXML
    private void login() {

        String enteredUsername = usernameField.getText();
        String enteredPassword = passwordField.getText();
        Player player = new Player(enteredUsername, enteredPassword);

        try {
            GameController gameCtrl = new GameController();
            gameCtrl.setServer(this.server);
            gameCtrl.setPlayer(player);
            server.login(player, gameCtrl);
            loginBtn.getScene().getWindow().hide();
            gameCtrl.loggedIn();
        }
        catch (GameException e) {
            usernameField.getStyleClass().add("wrongInput");
            passwordField.getStyleClass().add("wrongInput");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
