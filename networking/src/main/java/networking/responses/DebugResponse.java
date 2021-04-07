package networking.responses;

import networking.ResponseData;

public class DebugResponse implements ResponseData {
    public final String message;

    public DebugResponse(String message) {
        this.message = message;
    }
}
