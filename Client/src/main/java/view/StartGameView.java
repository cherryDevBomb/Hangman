package view;

import controller.GameController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class StartGameView {

    private Scene startGameScene;

    public StartGameView(GameController ctrl) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/startGame.fxml"));
        loader.setController(ctrl);
        AnchorPane root = loader.load();
        startGameScene = new Scene(root);
    }

    public Scene getStartGameScene() {
        return startGameScene;
    }
}
