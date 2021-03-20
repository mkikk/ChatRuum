package networking.messages.clientbound;

import networking.Message;
import networking.messages.Response;

public class ResponseMessage implements Message {
    public final Response response;

    public ResponseMessage(Response response) {
        this.response = response;
    }
}
