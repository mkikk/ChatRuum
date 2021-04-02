package server.networking;

import networking.RequestData;
import networking.ResponseData;

public class Request<T extends RequestData> {
    protected final int id;
    protected final ServerSession session;
    public final T data;

    public Request(int id, ServerSession session, T data) {
        this.id = id;
        this.session = session;
        this.data = data;
    }

    public void sendResponse(ResponseData responseData) {
        session.sendResponse(id, responseData);
    }
}
