package networking;

import io.netty.channel.*;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import networking.events.ConnectedEvent;
import networking.events.ConnectionDroppedEvent;
import networking.events.DisconnectedEvent;
import networking.events.ErrorEvent;

import java.io.Serializable;
import java.net.SocketAddress;

import static io.netty.channel.ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE;

public abstract class AbstractSession extends ChannelInboundHandlerAdapter {
    private ChannelHandlerContext ctx;
    private SocketAddress targetAddress;
    protected volatile boolean closedLocally;
    private final ExceptionForwarder exceptionForwarder;

    private class ExceptionForwarder implements ChannelFutureListener {
        @Override
        public void operationComplete(ChannelFuture future) {
            if (!future.isSuccess()) {
                exceptionCaught(ctx, future.cause());
            }
        }
    }

    public AbstractSession(SocketAddress targetAddress) {
        this.targetAddress = targetAddress;
        closedLocally = false;
        exceptionForwarder = new ExceptionForwarder();
    }

    protected abstract void callEventHandlers(Event event);

    public Channel getInternalChannel() {
        return ctx == null ? null : ctx.channel();
    }

    /**
     * Gets the target address as a printable string.
     * Note that the returned address may not be valid, if an invalid address was given
     */
    public SocketAddress getTargetAddress() {
        return targetAddress;
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
        System.err.println("Exception occurred in networked session with " + getTargetAddress() + ":");
        cause.printStackTrace();
        callEventHandlers(new ErrorEvent(cause));
        if (ctx.channel().isOpen()) {
            System.err.println("Session may be in an invalid state and will be closed automatically to avoid unexpected behaviour");
        } else {
            System.err.println("Session is already closed and should be discarded");
        }
        ctx.close();
    }
}
