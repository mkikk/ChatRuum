package networking;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import networking.events.ConnectionEvent;
import networking.events.ConnectionEvent.State;

import static io.netty.channel.ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE;

public class NettyClientSession extends ChannelInboundHandlerAdapter implements Session {
    protected final MultiTypeEventEmitter<NettyClientSession> eventEmitter;
    protected ChannelHandlerContext curCtx;

    public NettyClientSession(MultiTypeEventEmitter<NettyClientSession> eventEmitter) {
        this.eventEmitter = eventEmitter;
    }

    @Override
    public void sendMessage(Message message) {
        curCtx.writeAndFlush(message).addListener(FIRE_EXCEPTION_ON_FAILURE);;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        curCtx = ctx;
        eventEmitter.call(this, new ConnectionEvent(State.CONNECTED));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        curCtx = ctx;
        eventEmitter.call(this, new ConnectionEvent(State.DISCONNECTED));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        eventEmitter.call(this, (Message) msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
        eventEmitter.call(this, new ConnectionEvent(State.ERROR));
    }
}
