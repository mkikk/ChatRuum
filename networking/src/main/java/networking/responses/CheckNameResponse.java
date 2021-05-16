package networking.responses;

import networking.ResponseData;

public class CheckNameResponse implements ResponseData {
    private static final long serialVersionUID = -2404107462641575748L;

    public final Result result;

    public CheckNameResponse(Result result) {
        this.result = result;
    }
}
