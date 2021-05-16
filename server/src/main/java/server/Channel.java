package server;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import networking.ResponseData;
import networking.data.MessageData;
import networking.responses.NewMessageResponse;
import networking.responses.Response;
import networking.responses.ViewChannelResponse;
import networking.server.PersistentRequest;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class Channel {
    private final String name;
    @Nullable private final Password password;
    private final List<Message> messages;
    private final Set<User> users;
    @JsonIgnore
    private final List<PersistentRequest<?>> viewingRequests;

    public Channel(String name, @Nullable String password) {
        this.name = name;
        this.password = password == null ? null : new Password(password);
        messages = new ArrayList<>();
        users = new HashSet<>();
        viewingRequests = new ArrayList<>();
    }

    public Channel(@JsonProperty(value = "name") String name,
                   @JsonProperty(value = "password") @Nullable Password password,
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

    public @Nullable Password getPassword() {
        return password;
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

    protected void sendToViewers(ResponseData response) {
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

    public boolean editMessage(String textAfter, Instant time, User requestSender) {
        for (int i = messages.size()-1; i > 0; i--) {
            final Message message = messages.get(i);
            if(message.getTime().equals(time)) {
                if(requestSender.equals(message.getSender())) {
                    message.setText(textAfter);
                    sendToViewers(new ViewChannelResponse(Response.OK, convertToMessageData()));
                    return true;
                }
                return false;
            }
        }
        return false;

    }
}
