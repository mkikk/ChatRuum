package GUI;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import networking.client.PersistentRequest;
import networking.data.MessageData;
import networking.persistentrequests.ViewChannelRequest;
import networking.requests.CheckChannelNameRequest;
import networking.requests.CreateChannelRequest;
import networking.requests.JoinChannelRequest;
import networking.requests.SendMessageRequest;
import networking.responses.NewMessageResponse;
import networking.responses.Response;
import networking.responses.Result;
import networking.responses.ViewChannelResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class ChatController extends Contoller {
    private static final Logger logger = LogManager.getLogger();

    @FXML
    TextField newChannelText;
    @FXML
    TextArea inputText;
    @FXML
    PasswordField newChannelPassword;
    @FXML
    MenuItem menuItemExit, menuItemLeave;
    @FXML
    Label roomName, errorMessage;
    @FXML
    Button sendButton, joinNewRoom;
    @FXML
    ScrollPane messages;
    @FXML
    VBox messageField;

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            roomName.setText(OpenGUI.getCurrentChatroom());
            messages.setStyle("-fx-font-size: 16px;");
            viewChannel();
        });
    }

    private PersistentRequest view;

    public void sendMessage() {
        if (!inputText.getText().isEmpty()) {
            var req = OpenGUI.getSession().sendRequest(new SendMessageRequest(OpenGUI.getCurrentChatroom(), inputText.getText()));
            req.onResponse((s, r) -> {
                if (r.response == Response.OK) {
                    logger.debug("Sent message");
                    // TODO: send request for other users to recieve new message
                    new MessageData(inputText.getText(), OpenGUI.getUsername(), LocalDate.now());
                    Platform.runLater(() -> inputText.setText(""));
                } else if (r.response == Response.FORBIDDEN) {
                    logger.debug("Failed to send message");
                }
            });
        }
    }

    public void viewChannel() {
        view = OpenGUI.getSession().sendPersistentRequest(new ViewChannelRequest(roomName.getText()));
        view.onResponse(ViewChannelResponse.class, (s, r) -> {

            if (r.response == Response.OK) {
                logger.debug("Viewing channel");
                logger.debug("Channel messages: " + r.messages.toString());
                // Display up to last 50 messages to user
                displayLatestMessages(r.messages);

            } else if (r.response == Response.FORBIDDEN) {
                logger.debug("Failed to view channel");
            }

        });
        view.onResponse(NewMessageResponse.class, (s, r) -> {
            logger.debug("Received new message");
            Platform.runLater(() -> openMessage(r.data));
        });
    }

    public void joinNextRoom() {
        // after clicking on button 'Join', user joins new channel
        checkRoomName();
    }

    public void leaveCurrentRoom() throws IOException {
        // stop recieving new messages, switch scene
        view.close();
        OpenGUI.switchSceneTo("MainMenu", joinNewRoom, 900, 600);
    }

    public void exitChatruum() {
        // close application
        OpenGUI.stopSession();
        ((Stage) (roomName.getScene().getWindow())).close();
    }

    // Opens messages sent by user
    public void openMessage(MessageData messageData) {
        // Create new message field
        AnchorPane field = new AnchorPane();
        field.minHeight(60);
        field.minWidth(240);
        field.maxWidth(messages.getWidth());
        field.setStyle("-fx-background-color: #d5deef;");

        // Area for username and time, when message was sent
        HBox info = new HBox();
        info.maxHeight(34);
        info.minHeight(34);
        info.setStyle("-fx-border-style: hidden hidden solid hidden; -fx-border-width: 2px;");
        HBox.setMargin(info, new Insets(0, 0, 3, 10));

        field.getChildren().add(info);

        AnchorPane.setTopAnchor(info, 0.0);
        AnchorPane.setLeftAnchor(info, 0.0);
        AnchorPane.setRightAnchor(info, 0.0);

        // Message sender name label
        Label messageSender = new Label(messageData.senderName);
        messageSender.setAlignment(Pos.BOTTOM_LEFT);
        messageSender.maxHeight(30);
        messageSender.minHeight(30);
        messageSender.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        messageSender.setTextFill(Color.BLACK);

        info.getChildren().add(messageSender);

        // Label for time, when message was sent
        Label messageTime = new Label(messageData.sendTime.toString());
        messageTime.setAlignment(Pos.CENTER_LEFT);
        messageTime.maxHeight(30);
        messageTime.minHeight(30);
        messageTime.setPadding(new Insets(4, 0, 0, 15));
        messageTime.setFont(Font.font("Segoe UI", 14));
        messageTime.setTextFill(Color.valueOf("#141b2b"));

        info.getChildren().add(messageTime);

        // Text area for message text
        Text messageText = new Text(messageData.text);
        messageText.setWrappingWidth(messages.getWidth() - 50);
        messageText.setFill(Color.valueOf("#141b2b"));
        messageText.setFont(Font.font("Segoe  UI", 16));

        field.getChildren().add(messageText);

        AnchorPane.setBottomAnchor(messageText, 10.0);
        AnchorPane.setRightAnchor(messageText, 10.0);
        AnchorPane.setLeftAnchor(messageText, 10.0);
        AnchorPane.setTopAnchor(messageText, 35.0);

        // add to Vbox with other messages and scroll to message
        Platform.runLater(() -> {
            this.messageField.getChildren().add(field);
            this.messages.layout();
            this.messages.setVvalue(1.0);
        });
    }

    public void displayLatestMessages(List<MessageData> messageData) {
        Platform.runLater(() -> {
            // create messageboxes
            if (messageData.size() > 50) {
                for (int i = messageData.size() - 1; i > messageData.size() - 50; i--) {
                    openMessage(messageData.get(i));
                }
            } else if (messageData.size() > 0) {
                for (MessageData message : messageData) {
                    openMessage(message);
                }
            }
        });
    }

    public void joinRoom() {
        joinNewRoom.setDisable(true);
        var req = OpenGUI.getSession().sendRequest(new JoinChannelRequest(newChannelText.getText(), newChannelPassword.getText()));
        req.onResponse((s, r) -> {
            if (r.response == Response.OK) {
                // stop receiving messages
                view.close();
                logger.debug("Joined channel:");
                OpenGUI.setCurrentChatroom(newChannelText.getText());
                Platform.runLater(() -> {
                    OpenGUI.switchSceneTo("Chat", joinNewRoom, 1080, 800);
                });
            } else if (r.response == Response.FORBIDDEN) {
                logger.debug("Failed to join channel");
                Platform.runLater(() -> {
                    joinNewRoom.setDisable(false);
                    errorMessage.setText("Couldn't join to channel.");
                });
            }
        });
    }

    public void checkRoomName() {

        var req = OpenGUI.getSession().sendRequest(new CheckChannelNameRequest(newChannelText.getText()));
        req.onResponse((s, r) -> {
            if (r.result == Result.NAME_FREE) {
                logger.debug("Room name free");
                createRoom();
            } else if (r.result == Result.NAME_IN_USE) {
                logger.debug("Room name exists");
                joinRoom();
            } else if (r.result == Result.NAME_INVALID) {
                logger.debug("Room name not allowed");
                Platform.runLater(() -> errorMessage.setText("Invalid room name"));
            }
        });
    }

    public void createRoom() {
        var req = OpenGUI.getSession().sendRequest(
                new CreateChannelRequest(
                        newChannelText.getText(),
                        newChannelPassword.getText().isEmpty() ? null : newChannelPassword.getText()
                )
        );
        req.onResponse((s, r) -> {
            if (r.response == Response.OK) {
                logger.debug("Created channel");
                joinRoom();
            } else if (r.response == Response.FORBIDDEN) {
                logger.debug("Failed to create channel");
                Platform.runLater(() -> errorMessage.setText("Couldn't create channel. Try again!"));
            }
        });
    }

    @Override
    public void PrimaryAction() {
        if (!inputText.isFocused())
            sendMessage();
    }

    @Override
    public void selectLowerField() {
        if (newChannelText.isFocused())
            selectField(newChannelPassword);

    }

    @Override
    public void selectUpperField() {
        if (newChannelPassword.isFocused())
            selectField(newChannelText);

    }
}
