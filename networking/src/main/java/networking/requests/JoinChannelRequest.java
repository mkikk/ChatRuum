package networking.requests;

import networking.RequestData;
import networking.responses.GenericResponse;
import org.jetbrains.annotations.NotNull;

/**
 * Sent by client to server to request joining a new channel.
 */
public class JoinChannelRequest implements RequestData<GenericResponse> {
    private static final long serialVersionUID = 6277982076460377033L;

    public final String channelName;
    @NotNull public final String channelPassword;

    public JoinChannelRequest(String channelName, @NotNull String channelPassword) {
        this.channelName = channelName;
        this.channelPassword = channelPassword;
    }
}
