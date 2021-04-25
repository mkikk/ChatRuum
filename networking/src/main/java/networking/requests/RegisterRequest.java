package networking.requests;

import networking.RequestData;
import networking.responses.GenericResponse;
import org.jetbrains.annotations.NotNull;

public class RegisterRequest implements RequestData<GenericResponse> {
    public final String username;
    @NotNull public final String password;

    public RegisterRequest(String username, @NotNull String password) {
        this.username = username;
        this.password = password;
    }
}
