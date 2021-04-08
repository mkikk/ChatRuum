package networking.server;

import networking.RequestData;
import networking.ResponseData;

public class Request<T extends RequestData<R>, R extends ResponseData> extends AbstractRequest<T> {
    public Request(int id, ServerSession<?> session, T data) {
        super(id, session, data);
    }

    public void sendResponse(R responseData) {
        session.sendResponse(id, responseData);
    }
}
