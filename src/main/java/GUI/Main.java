package GUI;

import GUI.networking.ClientNetworkingManager;
import GUI.networking.ClientSession;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import networking.events.ConnectedEvent;

import java.io.File;
import java.net.URL;


public class Main extends Application {
    private static final int DEFAULT_PORT = 5050;

    private static ClientNetworkingManager client;
    private static ClientSession session;
    private static String username;

    public static String getUsername(){
        return username;
    }
    public static void setUsername(String username){
        Main.username = username;
    }

    Stage window;
    Scene login, mainMenu, chatRoom;

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL url = new File("src/main/resources/Login2.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);

        primaryStage.setTitle("Chatruum");
        primaryStage.setScene(new Scene(root, 1080, 600));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void startClient(String address) {
        if (client != null) return;
        client = new ClientNetworkingManager(address, DEFAULT_PORT);
        client.onEvent(ConnectedEvent.class, (s, e) -> session = s);
        client.start();
    }

    public static void stopClient() throws InterruptedException {
        if (client == null) return;
        client.stop();
    }

    public static ClientNetworkingManager getClient() {
        return client;
    }

    public static ClientSession getSession() {
        return session;
    }

    public static void main(String[] args) throws InterruptedException {
        startClient("localhost");
        launch();
        stopClient();
    }
}
