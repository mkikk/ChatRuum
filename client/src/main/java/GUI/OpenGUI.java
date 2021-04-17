package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Labeled;
import javafx.stage.Stage;
import networking.client.ClientNetworkingManager;
import networking.client.ClientSession;

import java.io.IOException;


public class OpenGUI extends Application {
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
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/EnterServerIP.fxml"));
        primaryStage.setTitle("Chatruum");
        primaryStage.setScene(new Scene(root, 900, 400));
        primaryStage.setResizable(false);
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

    public static void switchSceneTo(String fxmlName, Labeled referableComponent, int width, int height) throws IOException {
        Parent root = FXMLLoader.load(OpenGUI.class.getResource("/" + fxmlName + ".fxml"));
        Stage window = (Stage) referableComponent.getScene().getWindow();
        window.setScene(new Scene(root, width, height));
    }

    public static void main(String[] args) throws Exception {
        //connectClient("localhost");
        launch();
        stopSession();
    }

}
