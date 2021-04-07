package networking.events;

import networking.Event;

public class ErrorEvent implements Event {
    public final Throwable error;

    public ErrorEvent(Throwable error) {
        this.error = error;
    }
}
