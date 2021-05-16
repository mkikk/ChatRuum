package server;

import com.fasterxml.jackson.annotation.*;
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

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Channel {
    private final String name;
    @Nullable private final Password password;

    private final List<Message> messages;
    private final Set<User> users;

    @JsonIgnore private final List<PersistentRequest<?>> viewingRequests;

    public Channel(String name, @Nullable String password) {
        this(name, password == null ? null : new Password(password), new ArrayList<>(), new HashSet<>());
    }

    @JsonCreator
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

    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }

    public List<MessageData> convertToMessageData() {
        return messages.stream().map(Message::convertAsData).collect(Collectors.toList());
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
        for (int i = messages.size()-1; i >= 0; i--) {
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
