package GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.InputMethodEvent;
import networking.persistentrequests.ViewChannelRequest;
import networking.requests.SendMessageRequest;
import networking.responses.ViewChannelResponse;
import networking.responses.GenericResponse;
import networking.responses.Response;

public class ChatController {
    @FXML
    TextField newMessageText, channelNameText;

    public void setNewMessage(InputMethodEvent inputMethodEvent) {
    }

    public void sendMessage(ActionEvent actionEvent) {
        var req = Main.getSession().sendRequest(new SendMessageRequest(channelNameText.getText(), newMessageText.getText()));
        req.onResponse((s, r) -> {
            System.out.println(r.response.name());
            if (r.response == Response.OK) {
                System.out.println("Sent message");
            } else if (r.response == Response.FORBIDDEN) {
                System.out.println("Failed to send message");
            }
        });
    }

    public void viewChannel(ActionEvent actionEvent) {
        var view = Main.getSession().sendPersistentRequest(new ViewChannelRequest(channelNameText.getText()));
        view.onResponse(ViewChannelResponse.class, (s, r) -> {
            if (r.response == Response.OK) {
                System.out.println("Viewing channel");
                System.out.println("Channel messages: " + r.messages.toString());
            } else if (r.response == Response.FORBIDDEN) {
                System.out.println("Failed to view channel");
            }
            //Stop getting messages actively
            view.close();
        });
    }
}
