package networking.responses;

import networking.ResponseData;

public class CheckNameResponse implements ResponseData {
    public final Result result;

    public CheckNameResponse(Result result) {
        this.result = result;
    }
}
