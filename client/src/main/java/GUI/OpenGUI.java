package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Labeled;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import networking.client.ClientNetworkingManager;
import networking.client.ClientSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class OpenGUI extends Application {
    private static final Logger logger = LogManager.getLogger();

    private static final int DEFAULT_PORT = 5050;

    private static ClientNetworkingManager client;
    private static ClientSession session;
    private static String username;
    private static String currentChatroom;

    public static String getCurrentChatroom() {
        return currentChatroom;
    }

    public static void setCurrentChatroom(String currentChatroom) {
        OpenGUI.currentChatroom = currentChatroom;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        OpenGUI.username = username;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/EnterServerIP.fxml"));
        primaryStage.setTitle("Chatruum");
        primaryStage.setScene(new Scene(root, 900, 400));
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
        primaryStage.show();
    }

    public static ClientSession connectClient(String address) {
        if (client == null) {
            client = new ClientNetworkingManager(DEFAULT_PORT);
        }

        session = client.connect(address);
        return session;

    }

    public static void stopSession() {
        if (session == null) return;
        session.close();
    }

    public static ClientSession getSession() {
        return session;
    }

    public static void switchSceneTo(String fxmlName, Labeled referableComponent, int width, int height) {
        Stage window = (Stage) referableComponent.getScene().getWindow();
        if (window == null) {
            // If scene is not active, do not change scene, probably a duplicate result due to networking delays
            logger.warn("Attempt to change to scene " + fxmlName + " from inactive scene");
            return;
        }
        try {
            Parent root = FXMLLoader.load(OpenGUI.class.getResource("/" + fxmlName + ".fxml"));
            window.setScene(new Scene(root, width, height));
        } catch (IOException e) {
            throw new RuntimeException("Loading scene failed", e);
        }

    }

    public static void main(String[] args) {
        //connectClient("localhost");
        launch();
        stopSession();
    }

}
