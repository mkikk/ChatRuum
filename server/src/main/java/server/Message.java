package server;

import com.fasterxml.jackson.annotation.JsonProperty;
import networking.data.MessageData;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Message{
    private String text;
    private final User sender;
    private final String time;


    public Message(String text, User sender) {
        this.text = text;
        this.sender = sender;
        this.time = LocalDateTime.now().toString();
        System.out.println(this.time);
    }

    public Message(@JsonProperty(value = "text") String text, @JsonProperty(value = "sender") User sender,
                   @JsonProperty(value = "time") String time) {
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

    public String getTime() {
        return time;
    }
    public void setText(String text) {
        this.text = text;
    }

    public MessageData convertAsData() {
        return new MessageData(text, sender.getName(), time);
    }
}
