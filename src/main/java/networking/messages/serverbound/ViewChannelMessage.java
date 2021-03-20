package networking.messages.serverbound;

import networking.Message;

public class ViewChannelMessage implements Message {
    public final String channelName;

    public ViewChannelMessage(String channelName) {
        this.channelName = channelName;
    }
}
