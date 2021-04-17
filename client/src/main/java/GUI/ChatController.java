package GUI;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import networking.data.MessageData;
import networking.persistentrequests.ViewChannelRequest;
import networking.requests.SendMessageRequest;
import networking.responses.Response;
import networking.responses.ViewChannelResponse;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;

public class ChatController {
    @FXML
    TextField newRoomText;
    @FXML
    TextArea inputText;
    @FXML
    MenuItem menuItemExit, menuItemLeave;
    @FXML
    Label roomName;

    @FXML
    Button sendButton, joinNewRoom;

    @FXML
    ScrollPane Messages;

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            roomName.setText(OpenGUI.getCurrentChatroom());
            viewChannel();
        });
    }

    private Pane roomMessages = new Pane();

    public void sendMessage(ActionEvent actionEvent) {
        if (inputText.getText() != null) {
            var req = OpenGUI.getSession().sendRequest(new SendMessageRequest(OpenGUI.getCurrentChatroom(), inputText.getText()));
            req.onResponse((s, r) -> {

                System.out.println(r.response.name());
                if (r.response == Response.OK) {
                    System.out.println("Sent message");

                    openMessage(new MessageData(inputText.getText(), OpenGUI.getUsername(), LocalDate.now()));
                    Platform.runLater(() -> inputText.setText(""));
                } else if (r.response == Response.FORBIDDEN) {
                    System.out.println("Failed to send message");
                }

            });
        }
    }

    public void viewChannel() {
        var view = OpenGUI.getSession().sendPersistentRequest(new ViewChannelRequest(roomName.getText()));
        view.onResponse(ViewChannelResponse.class, (s, r) -> {
            if (r.response == Response.OK) {
                System.out.println("Viewing channel");
                System.out.println("Channel messages: " + r.messages.toString());
                // Display up to last 50 messages to user
                displayLatestMessages(r.messages);

            } else if (r.response == Response.FORBIDDEN) {
                System.out.println("Failed to view channel");
            }
            //Stop getting messages actively
            view.close();
        });
    }

    public void joinNewRoom(ActionEvent actionEvent) {
        // after clicking on button 'Join' end this session and start new session
    }

    public void leaveCurrentRoom(ActionEvent actionEvent) {
        // end this room session and switch to main menu screen
    }

    public void exitChatruum(ActionEvent actionEvent) {
        // close application
        OpenGUI.stopSession();
        ((Stage) ((roomName.getScene().getWindow()))).close();
    }

    // Opens messages sent by user
    public void openMessage(MessageData messageData) {
        // Create new message field
        AnchorPane field = new AnchorPane();
        field.minHeight(60);
        field.minWidth(240);
        field.maxWidth(500);
        field.setStyle("-fx-background-color: #236fc3;");

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
        messageSender.setTextFill(Color.WHITE);

        info.getChildren().add(messageSender);

        // Label for time, when message was sent

        Label messageTime = new Label(messageData.sendTime.toString());
        messageTime.setAlignment(Pos.BOTTOM_LEFT);
        messageTime.maxHeight(30);
        messageTime.minHeight(30);
        messageTime.setFont(Font.font("Segoe UI", 14));
        messageTime.setTextFill(Color.valueOf("#aeaeae"));

        info.getChildren().add(messageTime);

        // Text area for message text
        Text messageText = new Text(messageData.text);
        messageText.setWrappingWidth(480);
        messageText.setFill(Color.valueOf("#aaaaaa"));
        messageText.setFont(Font.font("Segoe  UI", 16));

        field.getChildren().add(messageText);

        AnchorPane.setBottomAnchor(messageText, 10.0);
        AnchorPane.setRightAnchor(messageText, 10.0);
        AnchorPane.setLeftAnchor(messageText, 10.0);
        AnchorPane.setTopAnchor(messageText, 35.0);

        // add to pane with other messages
        Platform.runLater(() -> this.roomMessages.getChildren().add(field));
    }

    public void displayLatestMessages(List<MessageData> messageData) {
        Platform.runLater(() -> {
            // create messageboxes
            if (messageData.size() > 50) {
                for (int i = messageData.size() - 1; i > messageData.size() - 50; i--) {
                    openMessage(messageData.get(i));
                }
            } else if (messageData.size() > 0) {
                for (int i = messageData.size() - 1; i > 0; i--) {
                    openMessage(messageData.get(i));
                }
            }
            // Display messages
            Messages.setContent(roomMessages);
        });
    }

    public void joinNextRoom(ActionEvent actionevent) {

    }
}
