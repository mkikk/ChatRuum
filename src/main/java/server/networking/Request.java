package server.networking;

import networking.RequestData;
import networking.ResponseData;

public class Request<T extends RequestData> extends AbstractRequest<T> {
    public Request(int id, ServerSession session, T data) {
        super(id, session, data);
    }
}
