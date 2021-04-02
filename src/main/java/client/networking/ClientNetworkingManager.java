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
import networking.*;
import server.networking.PersistentRequest;
import server.networking.Request;
import server.networking.ServerSession;

/**
 * Provides a convenient interface for networked communication on the client side.
 * Uses netty and java built in serialization internally.
 * TODO: Need new name to better reflect its intended use
 *
 * Netty code based on https://github.com/netty/netty/blob/4.1/example/src/main/java/io/netty/example/objectecho/ObjectEchoClient.java
 */
public class ClientNetworkingManager extends ChannelInitializer<SocketChannel> implements Runnable {
    private final MultiHandlerEventEmitter<ClientSession, Event> eventHandlers;
    protected final String host;
    protected final int port;

    public ClientNetworkingManager(String host, int port) {
        this.host = host;
        this.port = port;
        eventHandlers = new MultiHandlerEventEmitter<>();
    }

    public <T extends Event> EventHandler<ClientSession, T> onEvent(Class<T> type, EventHandler<ClientSession, T> handler) {
        return eventHandlers.add(type, handler);
    }

    public <T extends Event> boolean removeEventHandler(Class<T> type, EventHandler<ClientSession, T> handler) {
        return eventHandlers.remove(type, handler);
    }

    protected void callEventHandlers(ClientSession session, Event event) {
        eventHandlers.call(event.getClass(), session, event);
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast(
                new ObjectEncoder(),
                new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                new ClientSession(this)
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
