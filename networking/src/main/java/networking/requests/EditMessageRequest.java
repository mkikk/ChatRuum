package networking.requests;

import networking.RequestData;
import networking.responses.EditChannelResponse;

import java.time.Instant;

public class EditMessageRequest implements RequestData<EditChannelResponse> {
    public String channelName;
    public String textAfter;
    public Instant time;

    public EditMessageRequest(String channelName, String textAfter, Instant time) {
        this.channelName = channelName;
        this.textAfter = textAfter;
        this.time = time;
    }
}
