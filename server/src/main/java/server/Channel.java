package server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import networking.data.MessageData;
import networking.responses.NewMessageResponse;
import networking.server.PersistentRequest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Channel implements PasswordProtected{
    @JsonProperty(value = "name")
    private final String name;
    @JsonProperty(value = "password")
    private final String password;
    @JsonProperty(value = "messages")
    private final List<Message> messages;
    @JsonProperty(value = "users")
    private final Set<User> users;
    @JsonIgnore
    private final List<PersistentRequest<?>> viewingRequests;

    public Channel(String name, String password) {
        this.name = name;
        this.password = password;
        messages = new ArrayList<>(); // Todo: Load messages from file
        users = new HashSet<>();
        viewingRequests = new ArrayList<>();
    }

    public Channel(String name, String password, List<Message> messages, Set<User> users) {
        this.name = name;
        this.password = password;
        this.messages = messages;
        this.users = users;
        viewingRequests = new ArrayList<>();
    }
    public Channel(){
        name = getName();
        password = getPassword();
        users = getUsers();
        messages = getMessages();
        viewingRequests = new ArrayList<>();
    }

    public String getPassword() {
        return password;
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
        sendToViewers(new NewMessageResponse(message.getText()));
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

    @Override
    public boolean checkPassword(String givenPassword) {
        return password == null || password.equals(givenPassword);
    }
}
