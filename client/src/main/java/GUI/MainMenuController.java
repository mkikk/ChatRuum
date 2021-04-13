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
    Button JoinRoomButton, SettingsButton;

    @FXML
    public void initialize() {
        Platform.runLater(() -> UserWelcome.setText("Hello " + OpenGUI.getUsername()));
    }

    public void goToSettings(ActionEvent actionEvent) {
    }

    public void joinRoom(ActionEvent actionEvent) {
        JoinRoomButton.setDisable(true);
        var req = OpenGUI.getSession().sendRequest(new JoinChannelRequest(channelNameText.getText(), channelPasswordText.getText()));
        req.onResponse((s, r) -> {
            System.out.println(r.response.name());
            if (r.response == Response.OK) {
                System.out.println("Joined channel:");
                Platform.runLater(() -> switchToChatRoom());
            } else if (r.response == Response.FORBIDDEN) {
                System.out.println("Failed to join channel");

                Platform.runLater(() -> {
                    JoinRoomButton.setDisable(false);
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
            } else if (r.result == Result.NAME_IN_USE) {
                System.out.println("Room name exists");
                Platform.runLater(() -> ClientMessage.setText("Room name taken. Enter correct password."));
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
            } else if (r.response == Response.FORBIDDEN) {
                System.out.println("Failed to create channel");
            }
        });
    }

    private void switchToChatRoom() {
        if (JoinRoomButton.getScene().getWindow() == null) return;
        try {
            OpenGUI.switchSceneTo("Chat", JoinRoomButton, 1080, 800);
        } catch (IOException e) {
            System.out.println("error opening chat.fxml");
            return;
        }
    }
}
