package GUI;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Labeled;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import networking.client.ClientNetworkingManager;
import networking.client.ClientSession;

import java.io.IOException;
import java.util.logging.Handler;

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

    public static void switchSceneTo(String fxmlName, Labeled referableComponent, int width, int height) throws IOException {
        FXMLLoader loader = new FXMLLoader(OpenGUI.class.getResource("/" + fxmlName + ".fxml"));
        Parent root = loader.load();
        Stage window = (Stage) referableComponent.getScene().getWindow();
        final Scene newScene = new Scene(root, width, height);
        Contoller controller = loader.getController();
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

    public static void main(String[] args) throws Exception {
        //connectClient("localhost");
        launch();
        stopSession();
    }

}
