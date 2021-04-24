package GUI;

import javafx.application.Platform;
import javafx.event.ActionEvent;
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
import networking.data.MessageData;
import networking.persistentrequests.ViewChannelRequest;
import networking.requests.CheckChannelNameRequest;
import networking.requests.CreateChannelRequest;
import networking.requests.JoinChannelRequest;
import networking.requests.SendMessageRequest;
import networking.responses.Response;
import networking.responses.Result;
import networking.responses.ViewChannelResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class ChatController {
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
            viewChannel();
        });
    }
    // TODO: listener to update MessageField when new message is recieved
    public void sendMessage(ActionEvent actionEvent) {
        if (inputText.getText() != null) {
            var req = OpenGUI.getSession().sendRequest(new SendMessageRequest(OpenGUI.getCurrentChatroom(), inputText.getText()));
            req.onResponse((s, r) -> {

                System.out.println(r.response.name());
                if (r.response == Response.OK) {
                    System.out.println("Sent message");
                    // TODO: send request for other users to recieve new message
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

    public void joinNextRoom(ActionEvent actionEvent) {
        // after clicking on button 'Join' end this session and start new session
        // TODO: end session in current channel
        checkRoomName(actionEvent);
    }

    public void leaveCurrentRoom(ActionEvent actionEvent) throws IOException {
        // end this room session and switch to main menu screen
        // TODO: end session in current channel
        OpenGUI.switchSceneTo("MainMenu", joinNewRoom, 900, 600);
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
        field.maxWidth(messages.getWidth());
        field.setStyle("-fx-background-color: #b9c9ee;");

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

        // TODO: display message send time. Currently clash between MessageData and Message classes
//        // Label for time, when message was sent
//
//        Label messageTime = new Label(messageData.sendTime.toString());
//        messageTime.setAlignment(Pos.BOTTOM_LEFT);
//        messageTime.maxHeight(30);
//        messageTime.minHeight(30);
//        messageTime.setPadding(new Insets(0,0,0,15));
//        messageTime.setFont(Font.font("Segoe UI", 14));
//        messageTime.setTextFill(Color.valueOf("#141b2b"));
//
//        info.getChildren().add(messageTime);

        // Text area for message text
        Text messageText = new Text(messageData.text);
        messageText.setWrappingWidth(messages.getWidth());
        messageText.setFill(Color.valueOf("#141b2b"));
        messageText.setFont(Font.font("Segoe  UI", 16));

        field.getChildren().add(messageText);

        AnchorPane.setBottomAnchor(messageText, 10.0);
        AnchorPane.setRightAnchor(messageText, 10.0);
        AnchorPane.setLeftAnchor(messageText, 10.0);
        AnchorPane.setTopAnchor(messageText, 35.0);

        // add to pane with other messages
        Platform.runLater(() -> this.messageField.getChildren().add(field));
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
            messages.setVvalue(1.0);
        });
    }

    public void joinRoom(ActionEvent actionEvent) {
        joinNewRoom.setDisable(true);
        var req = OpenGUI.getSession().sendRequest(new JoinChannelRequest(newChannelText.getText(), newChannelPassword.getText()));
        req.onResponse((s, r) -> {
            System.out.println(r.response.name());
            if (r.response == Response.OK) {
                System.out.println("Joined channel:");
                OpenGUI.setCurrentChatroom(newChannelText.getText());
                Platform.runLater(() -> {
                    try {
                        OpenGUI.switchSceneTo("Chat", joinNewRoom, 1080, 800);
                    } catch (IOException e) {
                        System.out.println("error opening Chat.fxml");
                    }
                });
            } else if (r.response == Response.FORBIDDEN) {
                System.out.println("Failed to join channel");
                Platform.runLater(() -> {
                    joinNewRoom.setDisable(false);
                    errorMessage.setText("Couldn't join to channel.");
                });
            }
        });
    }

    public void checkRoomName(ActionEvent actionEvent) {

        var req = OpenGUI.getSession().sendRequest(new CheckChannelNameRequest(newChannelText.getText()));
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
                Platform.runLater(() -> errorMessage.setText("Invalid room name"));
            }
        });
    }

    public void createRoom(ActionEvent actionEvent) {
        var req = OpenGUI.getSession().sendRequest(
                new CreateChannelRequest(
                        newChannelText.getText(),
                        newChannelPassword.getText().isEmpty() ? null : newChannelPassword.getText()
                )
        );
        req.onResponse((s, r) -> {
            System.out.println(r.response.name());
            if (r.response == Response.OK) {
                System.out.println("Created channel");
                joinRoom(actionEvent);
            } else if (r.response == Response.FORBIDDEN) {
                System.out.println("Failed to create channel");
                Platform.runLater(() -> errorMessage.setText("Couldn't create channel. Try again!"));
            }
        });
    }
}
