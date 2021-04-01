package networking;

import java.util.concurrent.ConcurrentLinkedQueue;

public class EventDispatcher<S> {
    private final ConcurrentLinkedQueue<EventHandlerGroup<S>> handlers;

    public EventDispatcher() {
        handlers = new ConcurrentLinkedQueue<>();
    }

    public void add(EventHandlerGroup<S> handler) {
        handlers.add(handler);
    }

    public boolean remove(EventHandlerGroup<S> handler) {
        return handlers.remove(handler);
    }

    public void call(S session, Event event) {
        handlers.forEach(handler -> handler.handle(session, event));
    }
}
