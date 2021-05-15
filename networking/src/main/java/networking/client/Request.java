package networking.client;

import networking.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Object representing a one-time request
 * Allows setting a handler to be called when a response is received
 * @param <R> type of the expected response
 */
public class Request<R extends ResponseData> extends AbstractRequest {
    private static final Logger logger = LogManager.getLogger();

    protected EventHandler<ClientSession, R> handler;

    public Request(int id, ClientSession session) {
        super(id, session);
    }

    /**
     * Sets the event handler to be called upon receiving a response. Overrides previously set handler.
     */
    public void onResponse(EventHandler<ClientSession, R> handler) {
        this.handler = handler;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void receiveResponse(ResponseData data) {
        if (handler == null) {
            logger.warn("No handler for response registered");
            return;
        }
        handler.handle(session, (R) data);
    }

    @Override
    public boolean isPersistent() {
        return false;
    }
}
