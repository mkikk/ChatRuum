package networking.messages.clientbound;

import networking.messages.Response;

public class LoginResponseMessage extends ResponseMessage {
    public LoginResponseMessage(Response response) {
        super(response);
    }
}
