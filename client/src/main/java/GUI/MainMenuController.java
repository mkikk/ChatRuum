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

import java.io.IOException;

public class MainMenuController {
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
        Platform.runLater(() -> UserWelcome.setText("Hello " + OpenGUI.getUsername()));
    }

    public void joinButtonClicked(ActionEvent actionEvent) {
        checkRoomName(actionEvent);

    }
    public void joinRoom(ActionEvent actionEvent) {
        joinRoomButton.setDisable(true);
        var req = OpenGUI.getSession().sendRequest(new JoinChannelRequest(channelNameText.getText(), channelPasswordText.getText()));
        req.onResponse((s, r) -> {
            System.out.println(r.response.name());
            if (r.response == Response.OK) {
                System.out.println("Joined channel:");
                OpenGUI.setCurrentChatroom(channelNameText.getText());
                Platform.runLater(() -> switchToChatRoom());
            } else if (r.response == Response.FORBIDDEN) {
                System.out.println("Failed to join channel");
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
            System.out.println(r.result.name());
            if (r.result == Result.NAME_FREE) {
                System.out.println("Room name free");
                createRoom(actionEvent);
            } else if (r.result == Result.NAME_IN_USE) {
                System.out.println("Room name exists");
                joinRoom(actionEvent);
            } else if (r.result == Result.NAME_INVALID) {
                System.out.println("Room name not allowed");
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
            System.out.println(r.response.name());
            if (r.response == Response.OK) {
                System.out.println("Created channel");
                joinRoom(actionEvent);
            } else if (r.response == Response.FORBIDDEN) {
                System.out.println("Failed to create channel");
                Platform.runLater(() -> ClientMessage.setText("Couldn't create channel. Try again!"));
            }
        });
    }

    private void switchToChatRoom() {
        if (joinRoomButton.getScene().getWindow() == null) return;
        try {
            OpenGUI.switchSceneTo("Chat", joinRoomButton, 1080, 800);
        } catch (IOException e) {
            System.out.println("error opening chat.fxml");
            return;
        }
    }
}
