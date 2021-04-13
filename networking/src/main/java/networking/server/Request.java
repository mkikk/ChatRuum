package networking.server;

import networking.RequestData;

public class Request<T extends RequestData> extends AbstractRequest<T> {
    public Request(int id, ServerSession session, T data) {
        super(id, session, data);
    }
}
