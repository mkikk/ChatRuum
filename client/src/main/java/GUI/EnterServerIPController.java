package GUI;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import networking.events.ConnectedEvent;
import networking.events.ConnectionDroppedEvent;
import networking.events.NotConnectedEvent;

public class EnterServerIPController extends Controller {
    @FXML
    TextField ServerIPTextField;
    @FXML
    Button ConnectButton, CloseButton;

    public void closeClient(ActionEvent actionEvent) {
        // close window
        ((Stage) (((Button) actionEvent.getSource()).getScene().getWindow())).close();
    }

    public void connectToServer() {
        Platform.runLater(() -> ConnectButton.setDisable(true));
        var session = OpenGUI.connectClient(ServerIPTextField.getText());

        session.onEvent(ConnectedEvent.class, (s, e) -> {
            Platform.runLater(() -> {
                OpenGUI.switchSceneTo("Login", ConnectButton);
            });
        });

        session.onEvent(NotConnectedEvent.class, (s, e) -> {
            Platform.runLater(() -> {
                Alert noConnection = new Alert(Alert.AlertType.ERROR);
                noConnection.setTitle("No connection");
                noConnection.setContentText("Connecting to server '" + ServerIPTextField.getText() + "' failed.");
                ConnectButton.setDisable(false);
                noConnection.showAndWait();
            });
        });

        session.onEvent(ConnectionDroppedEvent.class, (s, e) -> {
            Platform.runLater(() -> {
                Alert networkingError = new Alert(Alert.AlertType.ERROR);
                networkingError.setTitle("Connection to server has been lost");
                networkingError.setContentText("Error while networking with server");
                networkingError.showAndWait();
            });
        });
    }

    @Override
    public void primaryAction() {
        connectToServer();
    }

    @Override
    public void selectLowerField() {
    }

    @Override
    public void selectUpperField() {
    }
}
