package GUI.networking;

import networking.*;

public class Request {
    protected final int id;
    protected final ClientSession session;
    protected final SingleHandlerEventEmitter<ClientSession, ResponseData> handlers;

    public Request(int id, ClientSession session) {
        this.id = id;
        this.session = session;
        handlers = new SingleHandlerEventEmitter<>();
    }

    public <T extends ResponseData> EventHandler<ClientSession, T> onResponse(Class<T> type, EventHandler<ClientSession, T> handler) {
        return handlers.set(type, handler);
    }

    public void receiveResponse(ResponseData data) {
        handlers.call(data.getClass(), session, data);
    }

    public boolean isPersistent() {
        return false;
    }
}
