package networking.responses;

import networking.ResponseData;
import networking.data.MessageData;
import server.Message;

import java.util.List;

public class ChannelMessagesResponse implements ResponseData {
    public final Response response;
    public final List<MessageData> messages;

    public ChannelMessagesResponse(Response response, List<MessageData> messages) {
        this.response = response;
        this.messages = messages;
    }
}
