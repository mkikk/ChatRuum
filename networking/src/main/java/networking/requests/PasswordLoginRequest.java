package networking.requests;

import networking.RequestData;
import networking.responses.GenericResponse;

public class PasswordLoginRequest implements RequestData<GenericResponse> {
    public final String username;
    public final String password;

    public PasswordLoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
