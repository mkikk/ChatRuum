package networking.requests;

import networking.RequestData;

public class RegisterRequest implements RequestData {
    public final String username;
    public final String password;

    public RegisterRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
