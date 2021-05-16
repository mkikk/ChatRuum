package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Labeled;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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

    private static Controller controller;

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

    // Used sources for keyevent implementation:
    // https://stackoverflow.com/questions/42512513/adding-event-listener-to-mainscene-in-javafx-using-fxml
    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/EnterServerIP.fxml"));
        Parent root = loader.load();
        Scene mainScene = new Scene(root, 900, 400);
        primaryStage.setTitle("Chatruum");

        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));

        Scene scene = new Scene(new Group(), 1080, 720);
        primaryStage.setScene(scene);

        switchSceneTo("EnterServerIP", scene);
        scene.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if(event.getCode() == KeyCode.ENTER)
                controller.primaryAction();
            else if(event.getCode() == KeyCode.DOWN)
                controller.selectLowerField();
            else if(event.getCode() == KeyCode.UP)
                controller.selectUpperField();
        });

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


    public static void switchSceneTo(String fxmlName, Labeled referableComponent) {
        Scene scene = referableComponent.getScene();
        if (scene == null) {
            // If scene is not active, do not change scene, probably a duplicate scene change due to networking delays
            logger.warn("Attempt to change to scene " + fxmlName + " from inactive scene");
            return;
        }

        switchSceneTo(fxmlName, scene);
    }

    public static void switchSceneTo(String fxmlName, Scene scene) {
        FXMLLoader loader;
        Parent root;

        try {
            loader = new FXMLLoader(OpenGUI.class.getResource("/" + fxmlName + ".fxml"));
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Loading scene failed", e);
        }

        controller = loader.getController();
        scene.setRoot(root);
    }

    public static void main(String[] args) {
        //connectClient("localhost");
        launch();
        stopSession();
    }

}
