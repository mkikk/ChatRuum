package networking;

import io.netty.channel.*;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import networking.events.ConnectedEvent;
import networking.events.DisconnectedEvent;
import networking.events.ErrorEvent;

import java.io.Serializable;

import static io.netty.channel.ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE;

public abstract class AbstractSession extends ChannelInboundHandlerAdapter {
    protected ChannelHandlerContext ctx;
    protected volatile boolean closedLocally = false;
    private final ExceptionForwarder exceptionForwarder = new ExceptionForwarder();

    private class ExceptionForwarder implements ChannelFutureListener {
        @Override
        public void operationComplete(ChannelFuture future) {
            if (!future.isSuccess()) {
                AbstractSession.this.exceptionCaught(null, future.cause());
            }
        }
    }

    protected abstract void callEventHandlers(Event event);

    public Channel getInternalChannel() {
        return ctx == null ? null : ctx.channel();
    }

    protected void send(Serializable data) {
        /*if (ctx == null) {
            exceptionCaught(null, new IllegalStateException("Attempt to send on closed session"));
            return;
        }*/

        ctx.writeAndFlush(data).addListener(exceptionForwarder);
    }

    /**
     * Closes the session
     */
    public void close() {
        closedLocally = true;
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        //this.ctx = null;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.err.println("Exception occurred in networked session:");
        cause.printStackTrace();
        callEventHandlers(new ErrorEvent(cause));
    }
}
