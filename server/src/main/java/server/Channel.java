package server;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import networking.data.MessageData;
import networking.responses.NewMessageResponse;
import networking.server.PersistentRequest;

import java.util.*;
import java.util.stream.Collectors;

public class Channel {
    private final String name;
    private final Password password;
    private final List<Message> messages;
    private final Set<User> users;
    @JsonIgnore
    private final List<PersistentRequest<?>> viewingRequests;

    public Channel(String name, String password) {
        this.name = name;
        this.password = new Password(password);
        messages = new ArrayList<>(); // Todo: Load messages from file
        users = new HashSet<>();
        viewingRequests = new ArrayList<>();
    }

    public Channel(@JsonProperty(value = "name") String name,
                   @JsonProperty(value = "password") Password password,
                   @JsonProperty(value = "messages") List<Message> messages,
                   @JsonProperty(value = "users") Set<User> users) {
        this.name = name;
        this.password = password;
        this.messages = messages;
        this.users = users;
        viewingRequests = new ArrayList<>();
    }

    public Set<User> getUsers() {
        return users;
    }

    public List<MessageData> convertToMessageData() {
        return messages.stream().map(Message::convertAsData).collect(Collectors.toList());
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void sendMessage(Message message) {
        messages.add(message);
        sendToViewers(new NewMessageResponse(message.convertAsData()));
    }

    protected void sendToViewers(NewMessageResponse response) {
        for (PersistentRequest<?> listener : viewingRequests) {
            listener.sendResponse(response);
        }
    }

    public void addViewingRequest(PersistentRequest<?> request) {
        viewingRequests.add(request);
        request.onClose(viewingRequests::remove);
    }

    public void join(User user) {
        users.add(user);
    }

    public boolean isJoined(User user) {
        return users.contains(user);
    }

    public String getName() {
        return name;
    }

    public boolean checkPassword(String givenPassword) {
        return password == null || password.checkPassword(givenPassword);
    }
}
