package networking.client;

import networking.EventHandler;
import networking.ResponseData;
import networking.SingleHandlerEventEmitter;

public abstract class AbstractRequest {
    protected final int id;
    protected final ClientSession session;

    public AbstractRequest(int id, ClientSession session) {
        this.id = id;
        this.session = session;
    }

    public abstract void receiveResponse(ResponseData data);

    /**
     * Check if request is persistent
     * @return return true to indicate that this request should be kept open by the session, until explicitly closed
     */
    public abstract boolean isPersistent();
}
