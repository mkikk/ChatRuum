package networking.client;

import networking.EventHandler;
import networking.ResponseData;
import networking.SingleHandlerEventEmitter;

/**
 * Object representing a persistent request, which can receive multiple responses
 * Allows setting a handler to be called when a specific type of response is received
 */
public class PersistentRequest extends AbstractRequest {
    protected final SingleHandlerEventEmitter<ClientSession, ResponseData> handlers;

    public PersistentRequest(int id, ClientSession session) {
        super(id, session);
        handlers = new SingleHandlerEventEmitter<>();
    }

    /**
     * Sets the event handler to be called upon receiving a response of the specified type. Overrides previously set handler for this type of response.
     */
    public <T extends ResponseData> void onResponse(Class<T> type, EventHandler<ClientSession, T> handler) {
        handlers.set(type, handler);
    }

    /**
     * Closes the persistent request and notifies the server, if possible
     * Note that requests will be closed automatically if the session closes, so there is no need to close requests if the session has already been closed
     * It is safe to close a request multiple times or close a request after the underlying session has been closed
     */
    public void close() {
        session.closePersistentRequest(id);
    }

    @Override
    public void receiveResponse(ResponseData data) {
        if (!handlers.call(data.getClass(), session, data)) {
            throw new RuntimeException("No handler for response of type " + data.getClass().getName() + " registered");
        }
    }

    @Override
    public boolean isPersistent() {
        return true;
    }
}
