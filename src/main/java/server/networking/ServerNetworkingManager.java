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
import networking.Event;
import networking.EventHandler;
import networking.MultiTypeEventEmitter;

/**
 * Provides a convenient interface for networked communication on the server side.
 * Uses netty and java built in serialization internally.
 * TODO: Need new name to better reflect its intended use
 *
 * Netty code based on https://github.com/netty/netty/blob/4.1/example/src/main/java/io/netty/example/objectecho/ObjectEchoServer.java
 */
public class ServerNetworkingManager extends ChannelInitializer<SocketChannel> implements Runnable {
    protected final MultiTypeEventEmitter<ServerSession> eventEmitter;
    protected final int port;

    public ServerNetworkingManager(int port) {
        this.eventEmitter = new MultiTypeEventEmitter<>();
        this.port = port;
    }

    public <T extends Event> EventHandler<ServerSession, T> on(Class<T> type, EventHandler<ServerSession, T> handler) {
        return eventEmitter.add(type, handler);
    }

    public <T extends Event> boolean remove(Class<T> type, EventHandler<ServerSession, T> handler) {
        return eventEmitter.remove(type, handler);
    }

    public void clear() {
        eventEmitter.clear();
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast(
                new ObjectEncoder(),
                new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                new ServerSession(eventEmitter)
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
