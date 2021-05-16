package networking.requests;

import networking.RequestData;
import networking.responses.CheckNameResponse;

public class CheckUsernameRequest implements RequestData<CheckNameResponse> {
    private static final long serialVersionUID = 7668748146499715307L;

    public final String username;

    public CheckUsernameRequest(String username) {
        this.username = username;
    }
}
