package networking.requests;

import networking.RequestData;

public class CreateChannelRequest implements RequestData {
    public final String channelName;
    public final String channelPassword;

    public CreateChannelRequest(String channelName, String channelPassword) {
        this.channelName = channelName;
        this.channelPassword = channelPassword;
    }
}
