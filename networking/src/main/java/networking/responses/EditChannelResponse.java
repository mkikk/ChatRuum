package networking.responses;


import networking.ResponseData;
import networking.data.MessageData;
import org.apache.logging.log4j.message.Message;

import java.util.List;

public class EditChannelResponse implements ResponseData {
    public List<MessageData> messages;
    public Response response;

    public EditChannelResponse(Response response, List<MessageData> messages) {
        this.response = response;
        this.messages = messages;
    }
}
