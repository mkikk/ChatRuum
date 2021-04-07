package networking.server;

import networking.PersistentRequestData;
import java.util.function.Consumer;

import java.util.concurrent.ConcurrentLinkedQueue;

public class PersistentRequest<T extends PersistentRequestData> extends AbstractRequest<T> {
    protected final ConcurrentLinkedQueue<Consumer<PersistentRequest<T>>> closeHandlers;

    public PersistentRequest(int id, ServerSession session, T data) {
        super(id, session, data);
        closeHandlers = new ConcurrentLinkedQueue<>();
    }

    public void onClose(Consumer<PersistentRequest<T>> handler) {
        closeHandlers.add(handler);
    }

    protected void callCloseHandlers() {
        closeHandlers.forEach(h -> h.accept(this));
    }
}
