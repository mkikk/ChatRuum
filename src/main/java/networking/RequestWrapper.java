package networking;

import java.io.Serializable;

public final class RequestWrapper implements Serializable {
    public final int id;
    public final RequestData data;

    public RequestWrapper(int id, RequestData data) {
        this.id = id;
        this.data = data;
    }
}
