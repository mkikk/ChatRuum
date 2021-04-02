package networking;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import networking.events.ConnectionEvent;
import networking.events.ConnectionState;

import static io.netty.channel.ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE;

public abstract class AbstractSession extends ChannelInboundHandlerAdapter {
    protected ChannelHandlerContext ctx;

    protected abstract void callEventHandlers(Event event);

    // @Override
    // public boolean isActive() {
    //    return curCtx != null;
    // }

    public Channel getInternalChannel() {
        return ctx == null ? null : ctx.channel();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        callEventHandlers(new ConnectionEvent(ConnectionState.CONNECTED));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        callEventHandlers(new ConnectionEvent(ConnectionState.DISCONNECTED));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // TODO: Is this appropriate error handling?
        cause.printStackTrace();
        ctx.close();
        callEventHandlers(new ConnectionEvent(ConnectionState.ERROR));
    }
}
