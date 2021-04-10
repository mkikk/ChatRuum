package server;

import com.fasterxml.jackson.annotation.JsonProperty;
import networking.data.MessageData;

public class Message{
    @JsonProperty(value = "text")
    private final String text;
    @JsonProperty(value = "sender")
    private final User sender;

    public Message(String text, User sender) {
        this.text = text;
        this.sender = sender;
    }
    public Message() {
        this.text = getText();
        this.sender = getSender();
    }
    public User getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

    public MessageData convertAsData() {
        return new MessageData(text, sender.getName(), null);
    }
}
