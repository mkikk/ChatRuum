package networking;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class NettyClient extends ChannelInitializer<SocketChannel> implements Runnable {
    protected final MultiTypeEventEmitter<NettyClientSession> eventEmitter;
    protected final String host;
    protected final int port;

    public NettyClient(String host) {
        this(host, 5050);
    }

    public NettyClient(String host, int port) {
        this.eventEmitter = new MultiTypeEventEmitter<>();
        this.host = host;
        this.port = port;
    }

    public <T extends Event> EventHandler<NettyClientSession, T> on(Class<T> type, EventHandler<NettyClientSession, T> handler) {
        return eventEmitter.add(type, handler);
    }

    public <T extends Event> boolean remove(Class<T> type, EventHandler<NettyClientSession, T> handler) {
        return eventEmitter.remove(type, handler);
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast(
                new ObjectEncoder(),
                new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                new NettyClientSession(eventEmitter)
        );
    }

    @Override
    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group);
            b.channel(NioSocketChannel.class);
            b.handler(this);

            // Start the connection attempt.
            var channel = b.connect(host, port).sync().channel();
            System.out.println("Client connecting to " + channel.remoteAddress() + "...");
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            System.out.println("Interrupted, client stopping..."); // TODO: Logging
        } finally {
            group.shutdownGracefully();
        }
    }
}
