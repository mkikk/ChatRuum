package networking.requests;

import networking.RequestData;

public class CheckUsernameRequest implements RequestData {
    public final String username;

    public CheckUsernameRequest(String username) {
        this.username = username;
    }
}
