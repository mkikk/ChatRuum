package networking;

import server.Channel;
import server.User;

public class ServerSession extends Session {
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
    protected <T extends Event> void callEventHandlers(T event) {
        eventEmitter.call(this, event);
    }
}
