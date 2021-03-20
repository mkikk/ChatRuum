package networking.messages.serverbound;

import networking.Message;

public class ExitChannelMessage implements Message {
    public final String channelName;

    public ExitChannelMessage(String channelName) {
        this.channelName = channelName;
    }
}
