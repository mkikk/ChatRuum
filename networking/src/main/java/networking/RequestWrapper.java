package networking;

import java.io.Serializable;

public final class RequestWrapper implements Serializable {
    public final int id;
    public final Serializable data;

    public RequestWrapper(int id, Serializable data) {
        this.id = id;
        this.data = data;
    }
}
