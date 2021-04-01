package server.networking;

import networking.Event;
import networking.EventDispatcher;
import networking.AbstractSession;
import server.Channel;
import server.User;

public class ServerSession extends AbstractSession {
    private final EventDispatcher<ServerSession> eventDispatcher;
    protected User user;
    protected Channel activeChannel;

    public ServerSession(EventDispatcher<ServerSession> eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setActiveChannel(Channel activeChannel) {
        this.activeChannel = activeChannel;
    }

    public Channel getActiveChannel() {
        return activeChannel;
    }

    @Override
    protected void callEventHandlers(Event event) {
        eventDispatcher.call(this, event);
    }
}
