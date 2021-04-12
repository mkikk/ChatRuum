package networking.requests;

import networking.RequestData;
import networking.responses.CheckNameResponse;

public class CheckUsernameRequest implements RequestData<CheckNameResponse> {
    public final String username;

    public CheckUsernameRequest(String username) {
        this.username = username;
    }
}
