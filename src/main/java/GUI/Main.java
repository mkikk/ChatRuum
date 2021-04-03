package GUI;

import GUI.networking.ClientNetworkingManager;
import GUI.networking.ClientSession;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Labeled;
import javafx.stage.Stage;
import networking.events.ConnectedEvent;
import networking.events.DisconnectedEvent;
import networking.events.ErrorEvent;
import networking.events.NotConnectedEvent;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;


public class Main extends Application {
    private static final int DEFAULT_PORT = 5050;

    private static ClientNetworkingManager client;
    private static ClientSession session;
    private static String username;

    public static String getUsername(){
        return username;
    }
    public static void setUsername(String username) {
        Main.username = username;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL url = new File("src/main/resources/EnterServerIP.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);
        primaryStage.setTitle("Chatruum");
        primaryStage.setScene(new Scene(root, 900, 400));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static ClientSession connectClient(String address) {
        if (client == null) {
            client = new ClientNetworkingManager();
        }

        session = client.connect(address, DEFAULT_PORT);
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
        URL url = null;
        Parent root = null;
        try {
            url = new File("src/main/resources/" + fxmlName + ".fxml").toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            root = FXMLLoader.load(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage window = (Stage) referableComponent.getScene().getWindow();
        window.setScene(new Scene(root, width, height));
    }

    public static void main(String[] args) throws Exception {
        //connectClient("localhost");
        launch();
        stopSession();
    }

}
