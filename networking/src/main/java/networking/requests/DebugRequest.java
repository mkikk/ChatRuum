package networking.requests;

import networking.RequestData;

public class DebugRequest implements RequestData {
    public final String message;

    public DebugRequest(String message) {
        this.message = message;
    }
}
