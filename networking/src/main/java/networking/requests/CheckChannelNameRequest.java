package networking.requests;

import networking.RequestData;
import networking.responses.CheckNameResponse;

public class CheckChannelNameRequest implements RequestData<CheckNameResponse> {
    private static final long serialVersionUID = -5589728220514378403L;

    public final String channelName;

    public CheckChannelNameRequest(String channelName) {
        this.channelName = channelName;
    }
}
