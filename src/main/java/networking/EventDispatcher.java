package networking;

import java.util.concurrent.ConcurrentLinkedQueue;

public class EventDispatcher<S extends Session, T> {
    private final ConcurrentLinkedQueue<EventHandler<S, T>> handlers;

    public EventDispatcher() {
        handlers = new ConcurrentLinkedQueue<>();
    }

    public void add(EventHandler<S, T> handler) {
        handlers.add(handler);
    }

    public void remove(EventHandler<S, ?> handler) {
        handlers.remove(handler);
    }

    public void call(S session, T event) {
        handlers.forEach(handler -> handler.handle(session, event));
    }
}
