package networking;

public class ClientSession extends Session {
    protected final MultiTypeEventEmitter<ClientSession> eventEmitter;

    public ClientSession(MultiTypeEventEmitter<ClientSession> eventEmitter) {
        this.eventEmitter = eventEmitter;
    }

    @Override
    protected <T extends Event> void callEventHandlers(T event) {
        eventEmitter.call(this, event);
    }
}
