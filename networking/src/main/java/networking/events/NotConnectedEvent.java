package networking.events;

import networking.Event;

public class NotConnectedEvent implements Event {
    public final Throwable cause;

    public NotConnectedEvent(Throwable cause) {
        this.cause = cause;
    }
}
