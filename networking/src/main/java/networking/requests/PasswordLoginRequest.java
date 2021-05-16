package networking.requests;

import networking.RequestData;
import networking.responses.GenericResponse;

public class PasswordLoginRequest implements RequestData<GenericResponse> {
    private static final long serialVersionUID = 2933609743596840852L;

    public final String username;
    public final String password;

    public PasswordLoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
