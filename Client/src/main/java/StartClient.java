import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import services.IGameServices;
import view.LoginView;


public class StartClient extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        ApplicationContext factory = new ClassPathXmlApplicationContext("spring-client.xml");
        IGameServices server = (IGameServices)factory.getBean("gameService");
        System.out.println("Obtained a reference to remote chat server");

        LoginView login = new LoginView(server);
        primaryStage.setScene(login.getLoginScene());
        primaryStage.setTitle("Game");
        primaryStage.show();
    }

    public static void main(String[] args)  {
        launch(args);
    }
}

