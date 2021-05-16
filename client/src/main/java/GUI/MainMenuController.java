package GUI;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import networking.requests.CheckChannelNameRequest;
import networking.requests.CreateChannelRequest;
import networking.requests.FavoriteChannelsRequest;
import networking.requests.JoinChannelRequest;
import networking.responses.Response;
import networking.responses.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class MainMenuController extends Controller {
    @FXML
    ListView<String> ClientChannels, ClientFavourites;
    @FXML
    Label UserWelcome, ClientMessage;
    @FXML
    TextField channelNameText;
    @FXML
    PasswordField channelPasswordText;
    @FXML
    Button joinRoomButton;
    private static final Logger logger = LogManager.getLogger();

    @FXML
    public void initialize() {
        UserWelcome.setText("Hey, " + OpenGUI.getUsername());

        // get users previously visited channels
        var channelsReq = OpenGUI.getSession().sendRequest(new FavoriteChannelsRequest());
        channelsReq.onResponse((s, r) -> {
            // Sort by number of visits in descending order and collect names into list
            List<String> channelNames = r.channelPopularity.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .map(Map.Entry::getKey).collect(Collectors.toCollection(ArrayList::new));
            ObservableList<String> listChannels = FXCollections.observableList(channelNames);
            Platform.runLater(() -> ClientChannels.setItems(listChannels));
        });
    }

    public void joinButtonClicked() {
        checkRoomName();
    }

    public void joinRoom() {
        joinRoomButton.setDisable(true);
        var req = OpenGUI.getSession().sendRequest(new JoinChannelRequest(channelNameText.getText(), channelPasswordText.getText()));
        req.onResponse((s, r) -> {
            if (r.response == Response.OK) {
                logger.debug("Joined channel:");
                OpenGUI.setCurrentChatroom(channelNameText.getText());
                Platform.runLater(() -> OpenGUI.switchSceneTo("Chat", joinRoomButton));
            } else if (r.response == Response.FORBIDDEN) {
                logger.debug("Failed to join channel");
                Platform.runLater(() -> {
                    joinRoomButton.setDisable(false);
                    ClientMessage.setText("Couldn't join to channel.");
                });
            }
        });
    }

    public void checkRoomName() {

        var req = OpenGUI.getSession().sendRequest(new CheckChannelNameRequest(channelNameText.getText()));
        req.onResponse((s, r) -> {
            if (r.result == Result.NAME_FREE) {
                logger.debug("Room name free");
                createRoom();
            } else if (r.result == Result.NAME_IN_USE) {
                logger.debug("Room name exists");
                joinRoom();
            } else if (r.result == Result.NAME_INVALID) {
                logger.debug("Room name not allowed");
                Platform.runLater(() -> ClientMessage.setText("Invalid room name"));
            }
        });
    }

    public void createRoom() {
        var req = OpenGUI.getSession().sendRequest(
                new CreateChannelRequest(
                        channelNameText.getText(),
                        channelPasswordText.getText().isEmpty() ? null : channelPasswordText.getText()
                )
        );
        req.onResponse((s, r) -> {
            if (r.response == Response.OK) {
                logger.debug("Created channel");
                joinRoom();
            } else if (r.response == Response.FORBIDDEN) {
                logger.debug("Failed to create channel");
                Platform.runLater(() -> ClientMessage.setText("Couldn't create channel. Try again!"));
            }
        });
    }

    @Override
    public void primaryAction() {
        joinButtonClicked();
    }

    @Override
    public void selectLowerField() {
        if (channelNameText.isFocused())
            selectField(channelPasswordText);

    }

    @Override
    public void selectUpperField() {
        if (channelPasswordText.isFocused())
            selectField(channelNameText);

    }

}
