package networking.netty;

import client.ClientSession;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import networking.Event;
import networking.Message;
import networking.MultiTypeEventEmitter;
import networking.Session;
import networking.events.ConnectionEvent;
import networking.events.ConnectionState;

import static io.netty.channel.ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE;

public class NettyClientSession extends NettySession implements ClientSession {
    protected final MultiTypeEventEmitter<NettyClientSession> eventEmitter;
    protected ChannelHandlerContext curCtx;

    public NettyClientSession(MultiTypeEventEmitter<NettyClientSession> eventEmitter) {
        this.eventEmitter = eventEmitter;
    }

    @Override
    protected <T extends Event> void callEventHandlers(T event) {
        eventEmitter.call(this, event);
    }
}
