package networking;

/**
 * @deprecated Currently not supported by event emitter implementation
 */
@FunctionalInterface
public interface RemovableEventHandler<S extends Session, T> {
    /**
     * Called when an event of type T occurs. Handler can be removed by returning true.
     * @param session Active session which the event is tied to
     * @param event The event data
     * @return Return true to remove event handler. The handle method will then not be called for subsequent events.
     */
    boolean handle(S session, T event);
}
