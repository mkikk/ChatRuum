package networking.responses;

import networking.ResponseData;

public class DebugResponse implements ResponseData {
    private static final long serialVersionUID = 439806602757024393L;

    public final String message;

    public DebugResponse(String message) {
        this.message = message;
    }
}
