package GUI;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import networking.events.ConnectedEvent;
import networking.events.ErrorEvent;
import networking.events.NotConnectedEvent;

public class EnterServerIPController {
    @FXML
    TextField ServerIPTextField;
    @FXML
    Button ConnectButton, CloseButton;

    public void closeClient(ActionEvent actionEvent) {
        OpenGUI.stopSession();
    }

    public void connectToServer(ActionEvent actionEvent) {

        var session = OpenGUI.connectClient(ServerIPTextField.getText());
        session.onEvent(ConnectedEvent.class, (s, e) -> {
            Platform.runLater(() -> OpenGUI.switchSceneTo("login2", ConnectButton, 900, 400));
        });
        session.onEvent(NotConnectedEvent.class, (s, e) -> {
            Platform.runLater(() -> {
                Alert noConnection = new Alert(Alert.AlertType.ERROR);
                noConnection.setTitle("No connection");
                noConnection.setContentText("Connecting to server '" + ServerIPTextField.getText() + "' failed.");

                noConnection.showAndWait();
            });
        });
        session.onEvent(ErrorEvent.class, (s, e) -> {
            Platform.runLater(() -> {
                Alert networkingError = new Alert(Alert.AlertType.ERROR);
                networkingError.setTitle("Connection to server has been lost");
                networkingError.setContentText("Error while networking with server:\n" + e.error);

                networkingError.showAndWait();
            });
        });

    }

}
