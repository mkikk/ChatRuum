package networking.requests;

import networking.RequestData;
import networking.responses.GenericResponse;

/**
 * Sent by client to send new message to channel
 */
public class SendMessageRequest implements RequestData<GenericResponse> {
    public String channelName;
    public String text;

    public SendMessageRequest(String channelName, String text) {
        this.channelName = channelName;
        this.text = text;
    }
}
