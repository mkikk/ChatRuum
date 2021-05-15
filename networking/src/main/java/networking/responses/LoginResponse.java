package networking.responses;

import networking.ResponseData;

import java.util.Map;

public class LoginResponse extends GenericResponse {
    public final Map<String, Integer> favoriteChannels;

    public LoginResponse(Response response, Map<String, Integer> favoriteChannels) {
        super(response);
        this.favoriteChannels = favoriteChannels;
    }
}
