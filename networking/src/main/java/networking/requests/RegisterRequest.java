package networking.requests;

import networking.RequestData;
import networking.responses.GenericResponse;
import org.jetbrains.annotations.NotNull;

public class RegisterRequest implements RequestData<GenericResponse> {
    private static final long serialVersionUID = 4461166899984801864L;

    public final String username;
    @NotNull public final String password;

    public RegisterRequest(String username, @NotNull String password) {
        this.username = username;
        this.password = password;
    }
}
