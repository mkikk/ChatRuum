package networking.responses;

import networking.ResponseData;
import networking.data.MessageData;

/**
 * Sent to all clients on a specific channel when a new message arrives to that channel.
 */
public class NewMessageResponse implements ResponseData {
    public final MessageData data;
    public NewMessageResponse(MessageData data) { this.data = data; }
}
