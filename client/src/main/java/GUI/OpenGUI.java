package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
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
        primaryStage.setScene(mainScene);
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
        EnterServerIPController controller = loader.getController();
        mainScene.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if(event.getCode() == KeyCode.ENTER)
                controller.connectToServer();

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

    public static void switchSceneTo(String fxmlName, Labeled referableComponent, int width, int height) {
        FXMLLoader loader;
        Parent root;
        Stage window = (Stage) referableComponent.getScene().getWindow();
        if (window == null) {
            // If scene is not active, do not change scene, probably a duplicate result due to networking delays
            logger.warn("Attempt to change to scene " + fxmlName + " from inactive scene");
            return;
        }
        try {
            loader = new FXMLLoader(OpenGUI.class.getResource("/" + fxmlName + ".fxml"));
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Loading scene failed", e);
        }

        final Scene newScene = new Scene(root, width, height);
        Controller controller = loader.getController();
        newScene.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if(event.getCode() == KeyCode.ENTER)
                controller.PrimaryAction();
            else if(event.getCode() == KeyCode.DOWN)
                controller.selectLowerField();
             else if(event.getCode() == KeyCode.UP)
                controller.selectUpperField();
        });
        window.setScene(newScene);
    }

    public static void main(String[] args) {
        //connectClient("localhost");
        launch();
        stopSession();
    }

}
