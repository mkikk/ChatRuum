package networking.requests;

import networking.RequestData;
import networking.responses.DebugResponse;

public class DebugRequest implements RequestData<DebugResponse> {
    private static final long serialVersionUID = -1616811676023315759L;

    public final String message;

    public DebugRequest(String message) {
        this.message = message;
    }
}
