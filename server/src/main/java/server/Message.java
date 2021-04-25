package server;

import com.fasterxml.jackson.annotation.JsonProperty;
import networking.data.MessageData;

import java.time.LocalDate;

public class Message{
    private final String text;
    private final User sender;
    private final LocalDate time;


    public Message(@JsonProperty(value = "text") String text, @JsonProperty(value = "sender")User sender) {
        this.text = text;
        this.sender = sender;
        this.time = LocalDate.now();
    }

    public User getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

    public MessageData convertAsData() {
        return new MessageData(text, sender.getName(), time);
    }
}
