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
import networking.*;

/**
 * Provides a convenient interface for networked communication on the server side.
 * Uses netty and java built in serialization internally.
 * TODO: Need new name to better reflect its intended use
 *
 * Netty code based on https://github.com/netty/netty/blob/4.1/example/src/main/java/io/netty/example/objectecho/ObjectEchoServer.java
 */
public class ServerNetworkingManager extends ChannelInitializer<SocketChannel> implements Runnable {
    private final MultiHandlerEventEmitter<ServerSession, Event> eventHandlers;
    private final SingleHandlerEventEmitter<ServerSession, Request<?>> requestHandlers;
    private final SingleHandlerEventEmitter<ServerSession, PersistentRequest<?>> persistentRequestHandlers;
    protected final int port;

    public ServerNetworkingManager(int port) {
        this.port = port;
        eventHandlers = new MultiHandlerEventEmitter<>();
        requestHandlers = new SingleHandlerEventEmitter<>();
        persistentRequestHandlers = new SingleHandlerEventEmitter<>();
    }

    public <T extends Event> EventHandler<ServerSession, T> onEvent(Class<T> type, EventHandler<ServerSession, T> handler) {
        return eventHandlers.add(type, handler);
    }

    public <T extends RequestData> EventHandler<ServerSession, Request<T>> onRequest(Class<T> type, EventHandler<ServerSession, Request<T>> handler) {
        return requestHandlers.set(type, handler);
    }

    public <T extends PersistentRequestData> EventHandler<ServerSession, PersistentRequest<T>> onPersistentRequest(Class<T> type, EventHandler<ServerSession, PersistentRequest<T>> handler) {
        return persistentRequestHandlers.set(type, handler);
    }

    protected void callEventHandlers(ServerSession session, Event event) {
        eventHandlers.call(event.getClass(), session, event);
    }

    protected void callRequestHandlers(ServerSession session, Request<RequestData> request) {
        requestHandlers.call(request.data.getClass(), session, request);
    }

    protected void callPersistentRequestHandlers(ServerSession session, PersistentRequest<PersistentRequestData> request) {
        persistentRequestHandlers.call(request.data.getClass(), session, request);
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast(
                new ObjectEncoder(),
                new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                new ServerSession(this)
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
