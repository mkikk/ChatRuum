package networking;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MultiHandlerEventEmitter<S, L> {
    private final ConcurrentMap<Class<?>, EventDispatcher<S, ? extends L>> eventHandlers;

    public MultiHandlerEventEmitter() {
        eventHandlers = new ConcurrentHashMap<>();
    }

    /**
     * Add event handler.
     * @return Added event handler, for convenience when using lambdas.
     */
    @SuppressWarnings("unchecked")
    public <T extends L> EventHandler<S, T> add(Class<?> type, EventHandler<S, T> handler) {
        var handlers = (EventDispatcher<S, T>) eventHandlers.computeIfAbsent(type, t -> new EventDispatcher<S, T>());
        handlers.add(handler);
        return handler;
    }

    /**
     * Remove event handler.
     * @return True if handler was removed, false otherwise.
     */
    public <T extends L> boolean remove(Class<?> type, EventHandler<S, T> handler) {
        var handlers = eventHandlers.getOrDefault(type, null);
        return handler != null && handlers.remove(handler);
    }

    /**
     * Remove all event handlers of type.
     * @return True if at least one handler was removed, false otherwise.
     * Note: May return true even if no handlers were removed, if some handlers existed previously, but were removed individually.
     */
    public boolean removeAll(Class<?> type) {
        return eventHandlers.remove(type) != null;
    }

    public void clear() {
        eventHandlers.clear();
    }

    @SuppressWarnings("unchecked")
    public <T extends L> void call(Class<?> type, S session, T event) {
        var handlers = (EventDispatcher<S, T>) eventHandlers.getOrDefault(type, null);
        if (handlers != null) {
            handlers.call(session, event);
        }
    }
}
