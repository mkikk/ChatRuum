package networking.events;

import networking.Event;

public class ConnectionEvent implements Event {
    /**
     * Ühenduse seisund
     */
    public final State state;

    public ConnectionEvent(State state) {
        this.state = state;
    }

    public enum State {
        CONNECTED,
        DISCONNECTED,
        ERROR
    }
}

