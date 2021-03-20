package networking.messages.serverbound;

import networking.Message;

/**
 * Sent by client to server to request joining a new channel.
 */
public class JoinChannelMessage implements Message {
    public final String channelName;
    public final String channelPassword;

    public JoinChannelMessage(String channelName, String channelPassword) {
        this.channelName = channelName;
        this.channelPassword = channelPassword;
    }
}
