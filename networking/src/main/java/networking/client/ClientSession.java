package networking.client;

import io.netty.channel.ChannelHandlerContext;
import networking.*;
import networking.events.ConnectionDroppedEvent;
import networking.events.DisconnectedEvent;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientSession extends AbstractSession {
    protected final MultiHandlerEventEmitter<ClientSession, Event> eventHandlers;
    protected final ConcurrentMap<Integer, AbstractRequest> requests;

    private final AtomicInteger previousId;

    public ClientSession() {
        eventHandlers = new MultiHandlerEventEmitter<>();
        requests = new ConcurrentHashMap<>();
        previousId = new AtomicInteger(0);
    }

    protected int getNextId() {
        return previousId.getAndIncrement();
    }

    /**
     * Add an event handler for one of the events
     */
    public <T extends Event> EventHandler<ClientSession, T> onEvent(Class<T> type, EventHandler<ClientSession, T> handler) {
        return eventHandlers.add(type, handler);
    }

    /**
     * Remove a given event handler
     */
    public <T extends Event> void removeEventHandler(Class<T> type, EventHandler<ClientSession, T> handler) {
        eventHandlers.remove(type, handler);
    }

    /**
     * Sends a request over the network
     * @param requestData request to send
     * @return object representing the request, which can be used to handle the response
     */
    public <R extends ResponseData> Request<R> sendRequest(RequestData<R> requestData) {
        Objects.requireNonNull(requestData, "Attempt to send null request");

        int id = getNextId();
        var request = new Request<R>(id, this);
        requests.put(id, request);
        send(new RequestWrapper(id, requestData));

        return request;
    }

    /**
     * Sends a persistent request over the network
     * @param requestData persistent request to send
     * @return object representing the request, which can be used to handle the responses
     */
    public PersistentRequest sendPersistentRequest(PersistentRequestData requestData) {
        Objects.requireNonNull(requestData, "Attempt to send null persistent request");

        int id = getNextId();
        var request = new PersistentRequest(id, this);
        requests.put(id, request);
        send(new RequestWrapper(id, requestData));

        return request;
    }

    /**
     * Closes all open requests, including persistent requests
     * This should only be called manually if the application changes state and all existing requests should be discarded
     * This does not need to be called before closing the session, the server will close open requests automatically
     */
    public void closeAllRequests() {
        requests.forEach((k, v) -> {
            // Have to explicitly close persistent requests
            if (v.isPersistent()) closePersistentRequest(k);
        });
        requests.clear();
    }

    protected void closePersistentRequest(int id) {
        if (requests.remove(id) != null) {
            send(new RequestWrapper(id, null));
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
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
    public void channelInactive(ChannelHandlerContext ctx) {
        requests.clear(); // No close messages can be sent for ongoing requests, so we drop all ongoing requests that have not been closed.
        if (!closedLocally) { // If client has not called close, assume the connection was dropped and call relevant event handlers
            callEventHandlers(new ConnectionDroppedEvent());
        }
        callEventHandlers(new DisconnectedEvent());
    }

    @Override
    protected void callEventHandlers(Event event) {
        eventHandlers.call(event.getClass(), this, event);
    }
}
