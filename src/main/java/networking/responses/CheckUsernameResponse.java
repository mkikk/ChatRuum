package networking.responses;

import networking.ResponseData;

public class CheckUsernameResponse implements ResponseData {
    public final Result result;

    public CheckUsernameResponse(Result result) {
        this.result = result;
    }

    public enum Result {
        NAME_FREE,
        NAME_IN_USE,
        NAME_INVALID
    }
}
