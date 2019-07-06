package view;

import controller.GameController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class MainView {

    private Scene gameScene;

    public MainView(GameController ctrl) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
        loader.setController(ctrl);
        AnchorPane root = loader.load();
        gameScene = new Scene(root);
    }

    public Scene getGameScene() {
        return gameScene;
    }
}
