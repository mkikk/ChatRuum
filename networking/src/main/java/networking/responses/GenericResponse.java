package networking.responses;

import networking.ResponseData;

public class GenericResponse implements ResponseData {
    public final Response response;

    public GenericResponse(Response response) {
        this.response = response;
    }
}
