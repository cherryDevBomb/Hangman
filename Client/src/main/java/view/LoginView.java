package view;

import controller.LoginController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import services.IGameServices;

import java.io.IOException;

public class LoginView {

    private Scene loginScene;

    public LoginView(IGameServices server) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
        AnchorPane root = loader.load();

        LoginController loginCtrl = loader.getController();
        loginCtrl.setServer(server);
        loginScene = new Scene(root);
    }

    public Scene getLoginScene() {
        return loginScene;
    }
}

