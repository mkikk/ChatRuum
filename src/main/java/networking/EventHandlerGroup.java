package networking;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class EventHandlerGroup<S> {
    private final ConcurrentMap<Class<? extends Event>, EventHandler<S, Event>> messageHandlers;

    public EventHandlerGroup() {
        messageHandlers = new ConcurrentHashMap<>();
    }

    /**
     * Add event handler.
     * @return Added event handler, for convenience when using lambdas.
     */
    @SuppressWarnings("unchecked")
    public <T extends Event> EventHandler<S, T> on(Class<T> type, EventHandler<S, T> handler) {
        messageHandlers.put(type, (EventHandler<S, Event>) handler);
        return handler;
    }

    /**
     * Remove event handler.
     * @return True if handler was removed, false otherwise.
     */
    public <T extends Event> boolean remove(Class<T> type) {
        return messageHandlers.remove(type) != null;
    }

    public void handle(S session, Event event) {
        var type = event.getClass();
        var handler = messageHandlers.getOrDefault(type, null);
        if (handler != null) {
            handler.handle(session, event);
        }
    }
}
