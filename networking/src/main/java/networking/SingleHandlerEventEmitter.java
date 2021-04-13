package networking;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SingleHandlerEventEmitter<S, L> {
    private final ConcurrentMap<Class<?>, EventHandler<S, ? extends L>> eventHandlers;

    public SingleHandlerEventEmitter() {
        eventHandlers = new ConcurrentHashMap<>();
    }

    /**
     * Set event handler. Overrides previous handler.
     * @return Added event handler, for convenience when using lambdas.
     */
    public <T extends L> EventHandler<S, T> set(Class<?> type, EventHandler<S, T> handler) {
        eventHandlers.put(type, handler);
        return handler;
    }

    /**
     * Remove event handler.
     * @return True if handler was removed, false otherwise.
     */
    public <T extends L> boolean remove(Class<?> type, EventHandler<S, T> handler) {
        return eventHandlers.remove(type, handler);
    }

    /**
     * Remove all event handlers by type. Note that as this emitter only supports a single handler per event, this will remove a maximum of 1 handler.
     * @return True if handler was removed, false otherwise.
     */
    public boolean removeAll(Class<?> type) {
        return eventHandlers.remove(type) != null;
    }

    public void clear() {
        eventHandlers.clear();
    }

    @SuppressWarnings("unchecked")
    public <T extends L> void call(Class<?> type, S session, T event) {
        var handler = (EventHandler<S, T>) eventHandlers.getOrDefault(type, null);
        if (handler != null) {
            handler.handle(session, event);
        }
    }
}
