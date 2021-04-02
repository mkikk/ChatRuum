package networking.requests;

import networking.Message;
import networking.RequestData;

/**
 * Sent by client to server to request joining a new channel.
 */
public class JoinChannelRequest implements RequestData {
    public final String channelName;
    public final String channelPassword;

    public JoinChannelRequest(String channelName, String channelPassword) {
        this.channelName = channelName;
        this.channelPassword = channelPassword;
    }
}
