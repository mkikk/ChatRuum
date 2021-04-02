package networking;

import java.io.Serializable;

public class ResponseWrapper implements Serializable {
    public final int id;
    public final ResponseData data;

    public ResponseWrapper(int id, ResponseData data) {
        this.id = id;
        this.data = data;
    }
}
