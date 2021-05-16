package networking.responses;

import networking.ResponseData;

public final class GenericResponse implements ResponseData {
    private static final long serialVersionUID = 3614368895383410572L;

    public final Response response;

    public GenericResponse(Response response) {
        this.response = response;
    }
}
