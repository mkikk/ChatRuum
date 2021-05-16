package networking.requests;

import networking.RequestData;
import networking.responses.GenericResponse;

public class CanViewChannelRequest implements RequestData<GenericResponse> {
    public final String channelName;

    public CanViewChannelRequest(String channelName) {
        this.channelName = channelName;
    }
}
