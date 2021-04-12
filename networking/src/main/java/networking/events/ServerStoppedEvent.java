package networking.events;

import networking.Event;

/**
 * Occurs on server *after* the server has stopped and all open channels and threads have been cleaned up.
 * Note: Session will be null when this event occurs
 */
public class ServerStoppedEvent implements Event {
}
