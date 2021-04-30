package GUI;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import networking.requests.CheckChannelNameRequest;
import networking.requests.CreateChannelRequest;
import networking.requests.JoinChannelRequest;
import networking.responses.Response;
import networking.responses.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainMenuController {
    private static final Logger logger = LogManager.getLogger();

    @FXML
    Label UserWelcome, ClientMessage, LatestMessages;
    @FXML
    TextField channelNameText;
    @FXML
    PasswordField channelPasswordText;
    @FXML
    Button joinRoomButton;

    @FXML
    public void initialize() {
        Platform.runLater(() -> UserWelcome.setText("Hey, " + OpenGUI.getUsername()));
    }

    public void joinButtonClicked(ActionEvent actionEvent) {
        checkRoomName(actionEvent);
    }

    public void joinRoom(ActionEvent actionEvent) {
        joinRoomButton.setDisable(true);
        var req = OpenGUI.getSession().sendRequest(new JoinChannelRequest(channelNameText.getText(), channelPasswordText.getText()));
        req.onResponse((s, r) -> {
            if (r.response == Response.OK) {
                logger.debug("Joined channel:");
                OpenGUI.setCurrentChatroom(channelNameText.getText());
                Platform.runLater(() -> OpenGUI.switchSceneTo("Chat", joinRoomButton, 1080, 800));
            } else if (r.response == Response.FORBIDDEN) {
                logger.debug("Failed to join channel");
                Platform.runLater(() -> {
                    joinRoomButton.setDisable(false);
                    ClientMessage.setText("Couldn't join to channel.");
                });
            }
        });
    }

    public void checkRoomName(ActionEvent actionEvent) {

        var req = OpenGUI.getSession().sendRequest(new CheckChannelNameRequest(channelNameText.getText()));
        req.onResponse((s, r) -> {
            if (r.result == Result.NAME_FREE) {
                logger.debug("Room name free");
                createRoom(actionEvent);
            } else if (r.result == Result.NAME_IN_USE) {
                logger.debug("Room name exists");
                joinRoom(actionEvent);
            } else if (r.result == Result.NAME_INVALID) {
                logger.debug("Room name not allowed");
                Platform.runLater(() -> ClientMessage.setText("Invalid room name"));
            }
        });
    }

    public void createRoom(ActionEvent actionEvent) {
        var req = OpenGUI.getSession().sendRequest(
                new CreateChannelRequest(
                        channelNameText.getText(),
                        channelPasswordText.getText().isEmpty() ? null : channelPasswordText.getText()
                )
        );
        req.onResponse((s, r) -> {
            if (r.response == Response.OK) {
                logger.debug("Created channel");
                joinRoom(actionEvent);
            } else if (r.response == Response.FORBIDDEN) {
                logger.debug("Failed to create channel");
                Platform.runLater(() -> ClientMessage.setText("Couldn't create channel. Try again!"));
            }
        });
    }
}
