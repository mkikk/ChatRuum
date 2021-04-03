package networking.events;

import networking.Event;

public class ConnectionEvent implements Event {
    /**
     * Ühenduse seisund
     */
    public final ConnectionState state;

    public ConnectionEvent(ConnectionState state) {
        this.state = state;
    }
}

