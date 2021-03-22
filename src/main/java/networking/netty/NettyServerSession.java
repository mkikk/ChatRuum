package networking.netty;

import io.netty.channel.ChannelHandlerContext;
import networking.Event;
import networking.MultiTypeEventEmitter;
import server.Channel;
import server.ServerSession;
import server.User;

public class NettyServerSession extends NettySession implements ServerSession {
    private final MultiTypeEventEmitter<NettyServerSession> eventEmitter;
    protected User user;
    protected Channel activeChannel;

    public NettyServerSession(MultiTypeEventEmitter<NettyServerSession> eventEmitter) {
        this.eventEmitter = eventEmitter;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setActiveChannel(Channel activeChannel) {
        this.activeChannel = activeChannel;
    }

    @Override
    public Channel getActiveChannel() {
        return activeChannel;
    }

    @Override
    protected <T extends Event> void callEventHandlers(T event) {
        eventEmitter.call(this, event);
    }
}
