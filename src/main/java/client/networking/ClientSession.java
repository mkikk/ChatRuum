package client.networking;

import networking.AbstractSession;
import networking.Event;
import networking.EventDispatcher;

public class ClientSession extends AbstractSession {
    protected final EventDispatcher<ClientSession> eventDispatcher;

    public ClientSession(EventDispatcher<ClientSession> eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    protected void callEventHandlers(Event event) {
        eventDispatcher.call(this, event);
    }
}
