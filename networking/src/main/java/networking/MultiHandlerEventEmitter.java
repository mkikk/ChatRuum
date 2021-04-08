package networking;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Helper class to store and call event handlers with different event types, based on the class of the event or of a type related to the event.
 * Supports multiple event handlers per event type.
 * Does nothing if an event with no handlers occurs.
 * @param <S> Session type for the handlers allowed in this emitter
 * @param <L> Superclass of all events allowed in this emitter
 */
public class MultiHandlerEventEmitter<S, L> {
    private final ConcurrentMap<Class<?>, EventDispatcher<S, ? extends L>> eventHandlers;

    public MultiHandlerEventEmitter() {
        eventHandlers = new ConcurrentHashMap<>();
    }

    /**
     * Add event handler.
     * NB! It is expected that the type parameter determines the specific type of T in some manner. Ensuring this is the responsibility of the caller.
     * @return Added event handler, for convenience when using lambdas.
     */
    @SuppressWarnings("unchecked")
    public <T extends L> EventHandler<S, T> add(Class<?> type, EventHandler<S, T> handler) {
        var handlers = (EventDispatcher<S, T>) eventHandlers.computeIfAbsent(type, t -> new EventDispatcher<S, T>());
        handlers.add(handler);
        return handler;
    }

    /**
     * Remove specific event handler.
     * NB! The caller needs to ensure the given type and handler match up.
     */
    public void remove(Class<?> type, EventHandler<S, ? extends L> handler) {
        var handlers = eventHandlers.getOrDefault(type, null);
        if (handler != null) handlers.remove(handler);
    }

    /**
     * Remove all event handlers of type.
     */
    public void removeAll(Class<?> type) {
        eventHandlers.remove(type);
    }

    public void clear() {
        eventHandlers.clear();
    }

    /**
     * Call event handlers for given type.
     * If there are no event handlers registered for the given type, this method will do nothing.
     * NB! It is expected that the type parameter determines the specific type of T in some manner. Ensuring this is the responsibility of the caller.
     */
    @SuppressWarnings("unchecked")
    public <T extends L> boolean call(Class<?> type, S session, T event) {
        var handlers = (EventDispatcher<S, T>) eventHandlers.getOrDefault(type, null);
        if (handlers != null) {
            return handlers.call(session, event);
        } else {
            return false;
        }
    }
}
