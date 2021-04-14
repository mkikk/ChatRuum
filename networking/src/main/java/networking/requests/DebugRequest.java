package networking.requests;

import networking.RequestData;
import networking.responses.DebugResponse;

public class DebugRequest implements RequestData<DebugResponse> {
    public final String message;

    public DebugRequest(String message) {
        this.message = message;
    }
}
