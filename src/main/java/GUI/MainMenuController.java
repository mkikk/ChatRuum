package GUI;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import networking.requests.CreateChannelRequest;
import networking.requests.JoinChannelRequest;
import networking.responses.GenericResponse;
import networking.responses.Response;

public class MainMenuController {
    @FXML
    Label UserWelcome, LatestMessages;
    @FXML
    TextField enterRoomText, channelNameText;
    @FXML
    PasswordField channelPasswordText;
    @FXML
    Button JoinRoomButton, SettingsButton;

    @FXML
    public void initialize() {
        Platform.runLater(() -> UserWelcome.setText("Hello " + Main.getUsername()));
    }

    public void goToSettings(ActionEvent actionEvent) {
    }

    public void joinRoom(ActionEvent actionEvent) {
        var req = Main.getSession().sendRequest(new JoinChannelRequest(channelNameText.getText(), channelPasswordText.getText()));
        req.onResponse(GenericResponse.class, (s, r) -> {
            System.out.println(r.response.name());
            if (r.response == Response.OK) {
                System.out.println("Joined channel:");
            } else if (r.response == Response.FORBIDDEN){
                System.out.println("Failed to join channel");
            }
        });
    }

    public void createRoom(ActionEvent actionEvent) {
        var req = Main.getSession().sendRequest(
                new CreateChannelRequest(
                        channelNameText.getText(),
                        channelPasswordText.getText().isEmpty() ? null: channelPasswordText.getText()
                )
        );
        req.onResponse(GenericResponse.class, (s, r) -> {
            System.out.println(r.response.name());
            if (r.response == Response.OK) {
                System.out.println("Created channel");
            } else if (r.response == Response.FORBIDDEN){
                System.out.println("Failed to create channel");
            }
        });
    }
}
