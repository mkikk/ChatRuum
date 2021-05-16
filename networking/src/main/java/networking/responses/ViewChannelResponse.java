package networking.responses;

import networking.ResponseData;
import networking.data.MessageData;

import java.util.List;

public class ViewChannelResponse implements ResponseData {
    private static final long serialVersionUID = -8393653610357739682L;

    public final Response response;
    public final List<MessageData> messages;

    public ViewChannelResponse(Response response, List<MessageData> messages) {
        this.response = response;
        this.messages = messages;
    }
}
