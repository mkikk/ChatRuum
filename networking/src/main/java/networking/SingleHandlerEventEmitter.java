package networking;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Helper class to store and call event handlers with different event types, based on the class of the event or of a type related to the event.
 * Supports a single handler per event type.
 * Throws an exception if an event with no handlers occurs.
 * @param <S> Session type for the handlers allowed in this emitter
 * @param <L> Superclass of all events allowed in this emitter
 */
public class SingleHandlerEventEmitter<S, L> {
    private final ConcurrentMap<Class<?>, EventHandler<S, ? extends L>> eventHandlers;

    public SingleHandlerEventEmitter() {
        eventHandlers = new ConcurrentHashMap<>();
    }

    /**
     * Set event handler. Overrides previous handler.
     * NB! It is expected that the type parameter determines the specific type of T in some manner. Ensuring this is the responsibility of the caller.
     * @return Added event handler, for convenience when using lambdas.
     */
    public <T extends L> EventHandler<S, T> set(Class<?> type, EventHandler<S, T> handler) {
        eventHandlers.put(type, handler);
        return handler;
    }

    /**
     * Remove event handler.
     */
    public void remove(Class<?> type, EventHandler<S, ? extends L> handler) {
        eventHandlers.remove(type, handler);
    }

    /**
     * Remove all event handlers by type. Note that as this emitter only supports a single handler per event, this will remove a maximum of 1 handler.
     */
    public void removeAll(Class<?> type) {
        eventHandlers.remove(type);
    }

    public void clear() {
        eventHandlers.clear();
    }

    /**
     * Call event handlers for given type.
     * If there are no event handlers registered for the given type, this method will throw an exception.
     * NB! It is expected that the type parameter determines the specific type of T in some manner. Ensuring this is the responsibility of the caller.
     */
    @SuppressWarnings("unchecked")
    public <T extends L> boolean call(Class<?> type, S session, T event) {
        var handler = (EventHandler<S, T>) eventHandlers.getOrDefault(type, null);
        if (handler != null) {
            handler.handle(session, event);
            return true;
        } else {
            return false;
        }
    }
}
