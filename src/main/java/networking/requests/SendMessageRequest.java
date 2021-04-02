package networking.requests;

import networking.Message;
import networking.RequestData;

/**
 * Sent by client to send new message to channel
 */
public class SendMessageRequest implements RequestData {
    public String channelName;
    public String text;

    public SendMessageRequest(String channelName, String text) {
        this.channelName = channelName;
        this.text = text;
    }
}
