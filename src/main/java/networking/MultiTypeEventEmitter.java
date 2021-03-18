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

    /**
     * Add event handler.
     * @return Added event handler, for convenience when using lambdas.
     */
    @SuppressWarnings("unchecked")
    protected <T extends Event> EventHandler<S, T> add(Class<T> type, EventHandler<S, T> handler) {
        var handlers = (EventDispatcher<S, T>) messageHandlers.computeIfAbsent(type, t -> new EventDispatcher<S, T>());
        handlers.add(handler);
        return handler;
    }

    /**
     * Remove event handler.
     * @return True if handler was removed, false otherwise.
     */
    protected <T extends Event> boolean remove(Class<T> type, EventHandler<S, T> handler) {
        var handlers = messageHandlers.getOrDefault(type, null);
        return handler != null && handlers.remove(handler);
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
