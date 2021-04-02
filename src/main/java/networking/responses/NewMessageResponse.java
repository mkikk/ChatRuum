package networking.responses;

import networking.ResponseData;

/**
 * Sent to all clients on a specific channel when a new message arrives to that channel.
 */
public class NewMessageResponse implements ResponseData {
    public final String message;
    public NewMessageResponse(String message) { this.message = message; }
}
