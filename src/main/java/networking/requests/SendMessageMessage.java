package networking.requests;

import networking.Message;

/**
 * Sent by client to send new message to channel
 */
public class SendMessageMessage implements Message {
    public String channelName;
    public String text;

    public SendMessageMessage(String channelName, String text) {
        this.channelName = channelName;
        this.text = text;
    }
}
