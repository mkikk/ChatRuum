package networking.requests;

import networking.RequestData;

public class CheckChannelNameRequest implements RequestData {
    public final String channelName;

    public CheckChannelNameRequest(String channelName) {
        this.channelName = channelName;
    }
}
