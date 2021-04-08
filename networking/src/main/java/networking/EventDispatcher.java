package networking;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Helper class to call multiple event handlers of the same type at the same time
 * @param <S>
 * @param <T>
 */
public class EventDispatcher<S, T> {
    private final ConcurrentLinkedQueue<EventHandler<S, T>> handlers;
    // private final ConcurrentLinkedQueue<RemovableEventHandler<S, T>> removableHandlers;

    public EventDispatcher() {
        handlers = new ConcurrentLinkedQueue<>();
        // removableHandlers = new ConcurrentLinkedQueue<>();
    }

    public void add(EventHandler<S, T> handler) {
        handlers.add(handler);
    }

    public void remove(EventHandler<?, ?> handler) {
        handlers.remove(handler);
    }

    public boolean call(S session, T event) {
        handlers.forEach(handler -> handler.handle(session, event));
        return !handlers.isEmpty(); // TODO: Make this safe against race conditions
    }
}
