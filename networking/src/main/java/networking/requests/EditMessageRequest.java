package networking.requests;

import networking.RequestData;
import networking.responses.GenericResponse;

import java.time.Instant;

public class EditMessageRequest implements RequestData<GenericResponse> {
    private static final long serialVersionUID = -7424508339572982954L;

    public String channelName;
    public String textAfter;
    public Instant time;

    public EditMessageRequest(String channelName, String textAfter, Instant time) {
        this.channelName = channelName;
        this.textAfter = textAfter;
        this.time = time;
    }
}
