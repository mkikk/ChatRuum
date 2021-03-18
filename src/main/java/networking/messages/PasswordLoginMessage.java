package networking.messages;

import networking.Message;

public class PasswordLoginMessage implements Message {
    public final String username;
    public final String password;

    public PasswordLoginMessage(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
