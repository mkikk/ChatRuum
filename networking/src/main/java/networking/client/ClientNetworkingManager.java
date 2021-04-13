package networking.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import networking.events.ConnectedEvent;
import networking.events.NotConnectedEvent;

/**
 * Provides a convenient interface for networked communication on the client side.
 * Uses netty and java built in serialization internally.
 * TODO: Need new name to better reflect its intended use
 *
 * Netty code based on https://github.com/netty/netty/blob/4.1/example/src/main/java/io/netty/example/objectecho/ObjectEchoClient.java
 */
public class ClientNetworkingManager extends ChannelInitializer<SocketChannel> {
    private ClientSession session;
    private EventLoopGroup group;

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        p.addLast(
                new ObjectEncoder(),
                new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                session
        );
    }

    public ClientSession connect(String host, int port) {
        setupSession();

        Bootstrap b = new Bootstrap();
        b.group(group);
        b.channel(NioSocketChannel.class);
        b.handler(this);

        var connectFuture = b.connect(host, port);
        System.out.println("Client connecting to " + connectFuture.channel().remoteAddress() + "...");
        connectFuture.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                session.callEventHandlers(new ConnectedEvent());
            } else {
                session.callEventHandlers(new NotConnectedEvent(future.cause()));
            }
        });
        connectFuture.channel().closeFuture().addListener((ChannelFutureListener) future -> {
            System.out.println("Client closing...");
            cleanupSession();
        });

        return session;
    }

    private synchronized void setupSession() {
        if (session != null || group != null) throw new IllegalStateException("Client session already running");

        session = new ClientSession();
        group = new NioEventLoopGroup();
    }

    private synchronized void cleanupSession() {
        if (group == null || session == null) throw new IllegalStateException("Client session not running");

        group.shutdownGracefully();
        group = null;
        session = null;
    }
}
