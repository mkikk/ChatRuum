package networking.requests;

import networking.RequestData;
import networking.responses.EditChannelResponse;

public class EditMessageRequest implements RequestData<EditChannelResponse> {
    public String channelName;
    public String textAfter;
    public String time;

    public EditMessageRequest(String channelName, String textAfter, String time) {
        this.channelName = channelName;
        this.textAfter = textAfter;
        this.time = time;
    }
}
