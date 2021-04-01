package server.networking;

import networking.Event;
import networking.EventDispatcher;
import networking.AbstractSession;
import networking.MultiTypeEventEmitter;
import server.Channel;
import server.User;

public class ServerSession extends AbstractSession {
    private final MultiTypeEventEmitter<ServerSession> eventEmitter;
    protected User user;
    protected Channel activeChannel;

    public ServerSession(MultiTypeEventEmitter<ServerSession> eventEmitter) {
        this.eventEmitter = eventEmitter;
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
        eventEmitter.call(this, event);
    }
}
