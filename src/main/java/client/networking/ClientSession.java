package client.networking;

import networking.AbstractSession;
import networking.Event;
import networking.MultiTypeEventEmitter;

public class ClientSession extends AbstractSession {
    protected final MultiTypeEventEmitter<ClientSession> eventEmitter;

    public ClientSession(MultiTypeEventEmitter<ClientSession> eventEmitter) {
        this.eventEmitter = eventEmitter;
    }

    @Override
    protected void callEventHandlers(Event event) {
        eventEmitter.call(this, event);
    }
}
