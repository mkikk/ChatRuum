package networking.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import networking.*;
import networking.events.ServerStoppedEvent;

import java.util.Scanner;

/**
 * Provides a convenient interface for networked communication on the server side.
 * Uses netty and java built in serialization internally.
 * TODO: Need new name to better reflect its intended use
 * <p>
 * Netty code based on https://github.com/netty/netty/blob/4.1/example/src/main/java/io/netty/example/objectecho/ObjectEchoServer.java
 *
 * @param <U> Type of user data stored on session
 */
public class ServerNetworkingManager<U> extends ChannelInitializer<SocketChannel> {
    private final MultiHandlerEventEmitter<ServerSession<U>, Event> eventHandlers;
    private final SingleHandlerEventEmitter<ServerSession<U>, Request<?, ?>> requestHandlers;
    private final SingleHandlerEventEmitter<ServerSession<U>, PersistentRequest<?>> persistentRequestHandlers;
    protected final int port;
    private volatile Channel serverChannel;

    public ServerNetworkingManager(int port) {
        this.port = port;
        eventHandlers = new MultiHandlerEventEmitter<>();
        requestHandlers = new SingleHandlerEventEmitter<>();
        persistentRequestHandlers = new SingleHandlerEventEmitter<>();
    }

    public <T extends Event> EventHandler<ServerSession<U>, T> onEvent(Class<T> type, EventHandler<ServerSession<U>, T> handler) {
        return eventHandlers.add(type, handler);
    }

    public <T extends RequestData<R>, R extends ResponseData> EventHandler<ServerSession<U>, Request<T, R>> onRequest(Class<T> type, EventHandler<ServerSession<U>, Request<T, R>> handler) {
        return requestHandlers.set(type, handler);
    }

    public <T extends PersistentRequestData> EventHandler<ServerSession<U>, PersistentRequest<T>> onPersistentRequest(Class<T> type, EventHandler<ServerSession<U>, PersistentRequest<T>> handler) {
        return persistentRequestHandlers.set(type, handler);
    }

    protected void callEventHandlers(ServerSession<U> session, Event event) {
        eventHandlers.call(event.getClass(), session, event);
    }

    protected void callRequestHandlers(ServerSession<U> session, Request<?, ?> request) {
        if (!requestHandlers.call(request.data.getClass(), session, request)) {
            throw new RuntimeException("No handler for request of type " + request.data.getClass().getName() + " registered");
        }
    }

    protected void callPersistentRequestHandlers(ServerSession<U> session, PersistentRequest<PersistentRequestData> request) {
        if (!persistentRequestHandlers.call(request.data.getClass(), session, request)) {
            throw new RuntimeException("No handler for persistent request of type " + request.data.getClass().getName() + " registered");
        }
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast(
                new ObjectEncoder(),
                new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                new ServerSession<>(ch.remoteAddress(), this)
        );
    }

    public synchronized void start() {
        if (serverChannel != null) return; // Server already running

        var bossGroup = new NioEventLoopGroup(1);
        var workerGroup = new NioEventLoopGroup();

        var bootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(this);

        // Bind and start to accept incoming connections.
        var bindFuture = bootstrap.bind(port);
        serverChannel = bindFuture.channel();
        bindFuture.addListener((ChannelFutureListener) future -> {
            System.out.println("Server listening on port " + port + "...");

            serverChannel.closeFuture().addListener(closeFuture -> {
                serverChannel = null;
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully().addListener(terminationFuture -> {
                    callEventHandlers(null, new ServerStoppedEvent());
                });
                System.out.println("Server stopping...");
            });
        });
    }

    public synchronized void stop() {
        if (serverChannel == null) return; // Server not running

        serverChannel.close();
    }
}
