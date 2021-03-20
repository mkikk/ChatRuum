package server;

import networking.Session;
import networking.messages.serverbound.SendMessageMessage;

import java.util.ArrayList;
import java.util.List;

public class Channel implements PasswordProtected{
    private final String name;
    private final String password;
    private final List<Message> messages;
    private final List<Session> viewingUsers;

    public Channel(String name, String password) {
        this.name = name;
        this.password = password;
        messages = new ArrayList<>(); // Todo: Load messages from file
        viewingUsers = new ArrayList<>();
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void sendMessage(Message message) {
        messages.add(message);

    }

    public String getName() {
        return name;
    }

    @Override
    public boolean checkPassword(String givenPassword) {
        return givenPassword.equals(password);
    }
}
