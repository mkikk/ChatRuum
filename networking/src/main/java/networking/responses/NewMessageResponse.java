package networking.responses;

import networking.ResponseData;
import networking.data.MessageData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Sent to all clients on a specific channel when a new message arrives to that channel.
 */
public class NewMessageResponse implements ResponseData {
    @NotNull public final MessageData data;
    public NewMessageResponse(@NotNull MessageData data) { this.data = data; }
}
