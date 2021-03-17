package networking;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MultiTypeEventEmitter<S extends Session> {
    private final ConcurrentMap<Class<? extends Event>, EventDispatcher<S, ? extends Event>> messageHandlers;

    public MultiTypeEventEmitter() {
        messageHandlers = new ConcurrentHashMap<>();
    }

    @SuppressWarnings("unchecked")
    protected  <T extends Event> void add(Class<T> type, EventHandler<S, T> handler) {
        var handlers = (EventDispatcher<S, T>) messageHandlers.computeIfAbsent(type, t -> new EventDispatcher<S, T>());
        handlers.add(handler);
    }

    protected  <T extends Event> void remove(Class<T> type, EventHandler<S, T> handler) {
        var handlers = messageHandlers.getOrDefault(type, null);
        if (handler != null) handlers.remove(handler);
    }

    @SuppressWarnings("unchecked")
    protected  <T extends Event> void call(S session, T message) {
        var type = message.getClass();
        var handlers = (EventDispatcher<S, T>) messageHandlers.getOrDefault(type, null);
        if (handlers != null) {
            handlers.call(session, message);
        }
    }
}
