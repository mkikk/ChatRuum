package networking.requests;

import networking.RequestData;
import networking.responses.CheckNameResponse;

public class CheckChannelNameRequest implements RequestData<CheckNameResponse> {
    public final String channelName;

    public CheckChannelNameRequest(String channelName) {
        this.channelName = channelName;
    }
}
