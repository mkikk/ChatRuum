package networking.server;

import networking.ResponseData;
import java.io.Serializable;

public abstract class AbstractRequest<T extends Serializable> {
    protected final int id;
    public final ServerSession session;
    public final T data;

    public AbstractRequest(int id, ServerSession session, T data) {
        this.id = id;
        this.session = session;
        this.data = data;
    }

    public void sendResponse(ResponseData responseData) {
        session.sendResponse(id, responseData);
    }
}
