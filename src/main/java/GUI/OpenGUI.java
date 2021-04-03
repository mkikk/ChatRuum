package GUI;

import client.networking.ClientNetworkingManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;


public class OpenGUI extends Application {
    private ClientNetworkingManager UserId;

    public ClientNetworkingManager getUserId() {
        return UserId;
    }
    public void setUserId(ClientNetworkingManager UserId) {
        this.UserId = UserId;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {


        URL url = new File("src/main/resources/Login2.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);

        primaryStage.setTitle("Chatruum");
        primaryStage.setScene(new Scene(root, 1080, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
