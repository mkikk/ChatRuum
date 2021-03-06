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
import networking.requests.*;
import networking.responses.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChatController extends Controller {
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
    Button sendButton;
    @FXML
    ScrollPane messages;
    @FXML
    VBox messageField;

    Instant editMessageTime;

    @FXML
    public void initialize() {
        roomName.setText(OpenGUI.getCurrentChatroom());
        viewChannel();
        messages.setStyle("-fx-font-size: 16px");
    }

    private PersistentRequest view;

    public void sendMessage() {
        if (!inputText.getText().isEmpty()) {
            if (sendButton.getText().equals("EDIT")) {
                var req = OpenGUI.getSession().sendRequest(new EditMessageRequest(OpenGUI.getCurrentChatroom(), inputText.getText(), editMessageTime));
                req.onResponse((s, r) -> {
                    if (r.response == Response.OK) {
                        logger.debug("Message edited");
                        Platform.runLater(() -> inputText.setText(""));
                    } else if (r.response == Response.FORBIDDEN) {
                        logger.warn("Failed to edit message");
                    }
                    Platform.runLater(() -> sendButton.setText("SEND"));
                });
            } else {
                var req = OpenGUI.getSession().sendRequest(new SendMessageRequest(OpenGUI.getCurrentChatroom(), inputText.getText()));
                req.onResponse((s, r) -> {
                    if (r.response == Response.OK) {
                        logger.debug("Sent message");
                        Platform.runLater(() -> inputText.setText(""));
                    } else if (r.response == Response.FORBIDDEN) {
                        logger.warn("Failed to send message");
                    }
                });
            }
        }
    }

    public void viewChannel() {
        view = OpenGUI.getSession().sendPersistentRequest(new ViewChannelRequest(roomName.getText()));
        view.onResponse(ViewChannelResponse.class, (s, r) -> {
            if (r.response == Response.OK) {
                logger.debug("Viewing channel");
                // Display up to last 50 messages to user
                Platform.runLater(() -> {
                    messageField.getChildren().clear();
                    displayLatestMessages(r.messages);
                    sendButton.setDisable(false);
                });
            } else if (r.response == Response.FORBIDDEN) {
                logger.warn("Failed to view channel");
                Platform.runLater(this::leaveCurrentRoom);
            }

        });
        view.onResponse(NewMessageResponse.class, (s, r) -> {
            logger.debug("Received new message");
            Platform.runLater(() -> openMessage(r.data));
        });
    }

    public void leaveCurrentRoom() {
        // stop recieving new messages, switch scene
        view.close();
        OpenGUI.switchSceneTo("MainMenu", sendButton);
    }

    public void exitChatruum() {
        // close application
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
        final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").withZone(ZoneId.systemDefault());
        Label messageTime = new Label(timeFormatter.format(messageData.sendTime));
        messageTime.setAlignment(Pos.CENTER_LEFT);
        messageTime.maxHeight(30);
        messageTime.minHeight(30);
        messageTime.setPadding(new Insets(3, 0, 0, 15));
        messageTime.setFont(Font.font("Segoe UI", 14));
        messageTime.setTextFill(Color.valueOf("#141b2b"));

        info.getChildren().add(messageTime);

        // Text area for message text
        Text messageText = new Text(messageData.text);
        messageText.setWrappingWidth(messages.getWidth() - 50);
        messageText.setFill(Color.valueOf("#141b2b"));
        messageText.setFont(Font.font("Segoe  UI", 16));

        field.getChildren().add(messageText);

        // Edit button
        if (OpenGUI.getUsername().equals(messageData.senderName)) {
            final Button edit = new Button("Edit");
            edit.setAlignment(Pos.BASELINE_RIGHT);
            edit.maxHeight(30);
            edit.minHeight(30);
            edit.setFont(Font.font("Segoe UI", 14));
            edit.setOnAction(event -> {
                inputText.setText(messageText.getText());
                sendButton.setText("EDIT");
                editMessageTime = messageData.sendTime;
            });

            info.getChildren().add(edit);
        }

        AnchorPane.setBottomAnchor(messageText, 10.0);
        AnchorPane.setRightAnchor(messageText, 10.0);
        AnchorPane.setLeftAnchor(messageText, 10.0);
        AnchorPane.setTopAnchor(messageText, 35.0);

        // add to Vbox with other messages and scroll to message
        this.messageField.getChildren().add(field);
        this.messages.layout();
        this.messages.setVvalue(1.0);
    }

    public void displayLatestMessages(List<MessageData> messageData) {
        // create messageboxes
        if (messageData.size() >= 50) {
            for (int i = messageData.size() - 50; i < messageData.size(); i++) {
                openMessage(messageData.get(i));
            }
        } else {
            for (MessageData message : messageData) {
                openMessage(message);
            }
        }
    }

    @Override
    public void primaryAction() {
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
