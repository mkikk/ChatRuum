package networking.netty;

import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import networking.Message;

import static io.netty.channel.ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE;

/**
 * Optimized threadsafe collection for NettySession objects.
 * Does not support retrieval of objects after being added.
 * Closed sessions will be automatically removed.
 * @deprecated Not really a good idea, probably. Very restrictive on possible actions.
 */
@Deprecated
public class NettySessionGroup {
    private final DefaultChannelGroup channelGroup;

    public NettySessionGroup() {
        this.channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    public void addSession(NettySession session) {
        channelGroup.add(session.getInternalChannel());
    }

    public void sendMessageAll(Message message) {
        channelGroup.writeAndFlush(message).addListener(FIRE_EXCEPTION_ON_FAILURE);
    }
}
