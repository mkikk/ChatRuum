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

import java.net.InetSocketAddress;

/**
 * Provides a convenient interface for networked communication on the client side.
 * Uses netty and java built in serialization internally.
 * TODO: Need new name to better reflect its intended use
 *
 * Netty code based on https://github.com/netty/netty/blob/4.1/example/src/main/java/io/netty/example/objectecho/ObjectEchoClient.java
 */
public class ClientNetworkingManager extends ChannelInitializer<SocketChannel> {
    private final int port;
    private volatile ClientSession session;

    public ClientNetworkingManager(int port) {
        this.port = port;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        p.addLast(
                new ObjectEncoder(),
                new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                session
        );
    }

    public synchronized ClientSession connect(String host) {
        if (session != null) return session;

        var targetAddress = new InetSocketAddress(host, port);
        session = new ClientSession(targetAddress);

        var group = new NioEventLoopGroup();

        var bootstrap = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(this);

        System.out.println("Client connecting to " + targetAddress + "...");
        bootstrap.connect(targetAddress).addListener((ChannelFutureListener) connectFuture -> {
            if (connectFuture.isSuccess()) {
                session.callEventHandlers(new ConnectedEvent());
            } else {
                session.callEventHandlers(new NotConnectedEvent(connectFuture.cause()));
            }

            connectFuture.channel().closeFuture().addListener(closeFuture -> {
                synchronized (this) {
                    session = null;
                }
                group.shutdownGracefully();
                System.out.println("Client closing...");
            });
        });

        return session;
    }
}
