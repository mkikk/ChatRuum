package networking.requests;

import networking.RequestData;
import networking.responses.GenericResponse;

/**
 * Sent by client to send new message to channel
 */
public class SendMessageRequest implements RequestData<GenericResponse> {
    private static final long serialVersionUID = -9156387944395450928L;

    public String channelName;
    public String text;

    public SendMessageRequest(String channelName, String text) {
        this.channelName = channelName;
        this.text = text;
    }
}
