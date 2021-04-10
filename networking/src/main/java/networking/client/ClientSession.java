package networking.client;

import io.netty.channel.ChannelHandlerContext;
import networking.*;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static io.netty.channel.ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE;

public class ClientSession extends AbstractSession {
    private final MultiHandlerEventEmitter<ClientSession, Event> eventHandlers;
    protected final ConcurrentMap<Integer, Request> requests;

    private int previousId = 0;

    public ClientSession() {
        eventHandlers = new MultiHandlerEventEmitter<>();
        requests = new ConcurrentHashMap<>();
    }

    protected int getNextId() {
        return ++previousId;
    }

    public <T extends Event> EventHandler<ClientSession, T> onEvent(Class<T> type, EventHandler<ClientSession, T> handler) {
        return eventHandlers.add(type, handler);
    }

    public <T extends Event> boolean removeEventHandler(Class<T> type, EventHandler<ClientSession, T> handler) {
        return eventHandlers.remove(type, handler);
    }

    public Request sendRequest(RequestData requestData) {
        Objects.requireNonNull(requestData, "Cannot send null request");

        int id = getNextId();
        var request = new Request(id, this);
        requests.put(id, request);
        ctx.writeAndFlush(new RequestWrapper(id, requestData)).addListener(FIRE_EXCEPTION_ON_FAILURE);

        return request;
    }

    public PersistentRequest sendPersistentRequest(PersistentRequestData requestData) {
        Objects.requireNonNull(requestData, "Cannot send null persistent request");

        int id = getNextId();
        var request = new PersistentRequest(id, this);
        requests.put(id, request);
        ctx.writeAndFlush(new RequestWrapper(id, requestData)).addListener(FIRE_EXCEPTION_ON_FAILURE);

        return request;
    }

    public void closeAllRequests() {
        requests.forEach((k, v) -> {
            // Have to explicitly close persistent requests
            if (v.isPersistent()) closePersistentRequest(k);
        });
        requests.clear();
    }

    protected void closePersistentRequest(int id) {
        if (requests.remove(id) != null) {
            ctx.writeAndFlush(new RequestWrapper(id, null)).addListener(FIRE_EXCEPTION_ON_FAILURE);
        }
    }

    /**
     * Closes the session
     */
    public void close() {
        ctx.channel().close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        var wrapper = (ResponseWrapper) msg;

        var request = requests.getOrDefault(wrapper.id, null);
        if (request == null) {
            System.out.println("Warning: Received response with no request: " + wrapper.data);
            return;
        }

        if (!request.isPersistent()) requests.remove(wrapper.id);
        request.receiveResponse(wrapper.data);
    }

    @Override
    protected void callEventHandlers(Event event) {
        eventHandlers.call(event.getClass(), this, event);
    }
}
