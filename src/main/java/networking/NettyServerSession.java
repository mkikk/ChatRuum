package networking;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import networking.events.ConnectionState;
import server.Channel;
import server.ServerSession;
import server.User;
import networking.events.ConnectionEvent;

import static io.netty.channel.ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE;

public class NettyServerSession extends ChannelInboundHandlerAdapter implements ServerSession {
    protected final MultiTypeEventEmitter<NettyServerSession> eventEmitter;
    protected User user;
    protected Channel activeChannel;
    protected ChannelHandlerContext curCtx;

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
    public void sendMessage(Message message) {
        if (curCtx != null) curCtx.writeAndFlush(message).addListener(FIRE_EXCEPTION_ON_FAILURE);
    }

    @Override
    public boolean isActive() {
        return curCtx != null;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        curCtx = ctx;
        eventEmitter.call(this, new ConnectionEvent(ConnectionState.CONNECTED));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        eventEmitter.call(this, new ConnectionEvent(ConnectionState.DISCONNECTED));
        curCtx = null;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        eventEmitter.call(this, (Message) msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // TODO: Is this appropriate error handling?
        cause.printStackTrace();
        ctx.close();
        eventEmitter.call(this, new ConnectionEvent(ConnectionState.ERROR));
        curCtx = null;
    }
}
