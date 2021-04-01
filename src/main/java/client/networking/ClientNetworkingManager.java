package client.networking;

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
import networking.Event;
import networking.EventHandler;
import networking.MultiTypeEventEmitter;

/**
 * Provides a convenient interface for networked communication on the client side.
 * Uses netty and java built in serialization internally.
 * TODO: Need new name to better reflect its intended use
 *
 * Netty code based on https://github.com/netty/netty/blob/4.1/example/src/main/java/io/netty/example/objectecho/ObjectEchoClient.java
 */
public class ClientNetworkingManager extends ChannelInitializer<SocketChannel> implements Runnable {
    protected final MultiTypeEventEmitter<ClientSession> eventEmitter;
    protected final String host;
    protected final int port;

    public ClientNetworkingManager(String host, int port) {
        this.eventEmitter = new MultiTypeEventEmitter<>();
        this.host = host;
        this.port = port;
    }

    public <T extends Event> EventHandler<ClientSession, T> on(Class<T> type, EventHandler<ClientSession, T> handler) {
        return eventEmitter.add(type, handler);
    }

    public <T extends Event> boolean remove(Class<T> type, EventHandler<ClientSession, T> handler) {
        return eventEmitter.remove(type, handler);
    }

    public void clear() {
        eventEmitter.clear();
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast(
                new ObjectEncoder(),
                new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                new ClientSession(eventEmitter)
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
