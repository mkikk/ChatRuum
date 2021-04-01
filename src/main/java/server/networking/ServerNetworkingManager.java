package server.networking;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import networking.EventDispatcher;

/**
 * Provides a convenient interface for networked communication on the server side.
 * Uses netty and java built in serialization internally.
 * TODO: Need new name to better reflect its intended use
 *
 * Netty code based on https://github.com/netty/netty/blob/4.1/example/src/main/java/io/netty/example/objectecho/ObjectEchoServer.java
 */
public class ServerNetworkingManager extends ChannelInitializer<SocketChannel> implements Runnable {
    protected final EventDispatcher<ServerSession> eventDispatcher;
    protected final int port;

    public ServerNetworkingManager(int port) {
        this.eventDispatcher = new EventDispatcher<>();
        this.port = port;
    }

    public void addHandlerGroup(ServerEventHandlerGroup handlerGroup) {
        eventDispatcher.add(handlerGroup);
    }

    public boolean removeHandlerGroup(ServerEventHandlerGroup handlerGroup) {
        return eventDispatcher.remove(handlerGroup);
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast(
                new ObjectEncoder(),
                new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                new ServerSession(eventDispatcher)
        );
    }

    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(this);

            // Bind and start to accept incoming connections.
            var channel = b.bind(port).sync().channel();
            System.out.println("Server listening on port " + port + "...");
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            System.out.println("Interrupted, server stopping...");
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
