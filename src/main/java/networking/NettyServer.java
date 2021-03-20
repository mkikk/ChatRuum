package networking;

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
import io.netty.handler.logging.LoggingHandler;

public class NettyServer extends ChannelInitializer<SocketChannel> implements Runnable {
    protected final MultiTypeEventEmitter<NettyServerSession> eventEmitter;
    protected final int port;

    public NettyServer() {
        this(5050);
    }

    public NettyServer(int port) {
        this.eventEmitter = new MultiTypeEventEmitter<>();
        this.port = port;
    }

    public <T extends Event> EventHandler<NettyServerSession, T> on(Class<T> type, EventHandler<NettyServerSession, T> handler) {
        return eventEmitter.add(type, handler);
    }

    public <T extends Event> boolean remove(Class<T> type, EventHandler<NettyServerSession, T> handler) {
        return eventEmitter.remove(type, handler);
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast(
                new ObjectEncoder(),
                new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                new NettyServerSession(eventEmitter)
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
