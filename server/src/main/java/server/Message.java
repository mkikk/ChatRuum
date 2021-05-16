package server;

import com.fasterxml.jackson.annotation.JsonProperty;
import networking.data.MessageData;

import java.time.Instant;

public class Message{
    private String text;
    private final User sender;
    private final Instant time;

    public Message(String text, User sender) {
        this(text, sender, Instant.now());
    }

    public Message(@JsonProperty(value = "text") String text, @JsonProperty(value = "sender") User sender,
                   @JsonProperty(value = "time") Instant time) {
        this.text = text;
        this.sender = sender;
        this.time = time;
    }

    public User getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

    public Instant getTime() {
        return time;
    }
    public void setText(String text) {
        this.text = text;
    }

    public MessageData convertAsData() {
        return new MessageData(text, sender.getName(), time);
    }
}
