package server.networking;

import networking.EventDispatcher;
import networking.EventHandler;
import networking.PersistentRequestData;

public class PersistentRequest<T extends PersistentRequestData> extends Request<T> {
    protected final EventDispatcher<ServerSession, PersistentRequest<T>> closeHandlers;

    public PersistentRequest(int id, ServerSession session, T data) {
        super(id, session, data);
        closeHandlers = new EventDispatcher<>();
    }

    protected void onClose(EventHandler<ServerSession, PersistentRequest<T>> handler) {
        closeHandlers.add(handler);
    }

    protected void callCloseHandlers() {
        closeHandlers.call(session, this);
    }
}
