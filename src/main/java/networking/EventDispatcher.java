package networking;

import java.util.concurrent.ConcurrentLinkedQueue;

public class EventDispatcher<S extends Session, T> {
    private final ConcurrentLinkedQueue<EventHandler<S, T>> handlers;
    // private final ConcurrentLinkedQueue<RemovableEventHandler<S, T>> removableHandlers;

    public EventDispatcher() {
        handlers = new ConcurrentLinkedQueue<>();
        // removableHandlers = new ConcurrentLinkedQueue<>();
    }

    public void add(EventHandler<S, T> handler) {
        handlers.add(handler);
    }

    public boolean remove(EventHandler<S, ?> handler) {
        return handlers.remove(handler);
    }

    public void call(S session, T event) {
        handlers.forEach(handler -> handler.handle(session, event));
    }
}
