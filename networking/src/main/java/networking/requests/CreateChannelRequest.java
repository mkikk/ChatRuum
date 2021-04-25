package networking.requests;

import networking.RequestData;
import networking.responses.GenericResponse;
import org.jetbrains.annotations.Nullable;

public class CreateChannelRequest implements RequestData<GenericResponse> {
    public final String channelName;
    @Nullable public final String channelPassword;

    public CreateChannelRequest(String channelName, @Nullable String channelPassword) {
        this.channelName = channelName;
        this.channelPassword = channelPassword;
    }
}
