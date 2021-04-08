package networking.requests;

import networking.RequestData;
import networking.responses.GenericResponse;

public class CreateChannelRequest implements RequestData<GenericResponse> {
    public final String channelName;
    public final String channelPassword;

    public CreateChannelRequest(String channelName, String channelPassword) {
        this.channelName = channelName;
        this.channelPassword = channelPassword;
    }
}
