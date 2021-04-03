package networking;

@FunctionalInterface
public interface EventHandler<S, T> {
    /**
     * Called when an event of type T occurs. Should only be removed by a call to the event emitter from outside the handle method.
     * @param session Active session which the event is tied to
     * @param event The event data
     */
    void handle(S session, T event);
}
