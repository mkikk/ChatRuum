package GUI;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.input.InputMethodEvent;

public class ChatController {

    public void setNewMessage(InputMethodEvent inputMethodEvent) {
    }

    public void SendMessage(ActionEvent actionEvent) {
    }

    public void createUserMessageLabel(ActionEvent actionEvent, String content) {
        Label message = new Label(content);

    }
}
