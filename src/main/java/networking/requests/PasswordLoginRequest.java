package networking.requests;

import networking.RequestData;

public class PasswordLoginRequest implements RequestData {
    public final String username;
    public final String password;

    public PasswordLoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
