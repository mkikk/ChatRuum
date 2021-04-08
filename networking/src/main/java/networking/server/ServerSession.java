package networking.server;

import io.netty.channel.ChannelHandlerContext;
import networking.*;
import networking.events.ConnectedEvent;
import networking.events.DisconnectedEvent;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Class to represent client session on server
 * @param <U> Type of user data stored on session
 */
public class ServerSession<U> extends AbstractSession {
    protected final ServerNetworkingManager<U> eventEmitter;
    protected final ConcurrentMap<Integer, PersistentRequest<?>> persistentRequests;

    protected U user;

    public ServerSession(ServerNetworkingManager<U> eventEmitter) {
        this.eventEmitter = eventEmitter;
        persistentRequests = new ConcurrentHashMap<>();
    }

    public void setUser(U user) {
        this.user = user;
    }

    public U getUser() {
        return user;
    }

    public void sendResponse(int id, ResponseData responseData) {
        Objects.requireNonNull(responseData, "Cannot send null response");

        send(new ResponseWrapper(id, responseData));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        var wrapper = (RequestWrapper) msg;
        if (wrapper.data == null) {
            // Null sent to notify that this persistent request has been closed by the client and future responses will not be received.
            // TODO: Replace null with more explicit value?

            var request = persistentRequests.remove(wrapper.id);
            if (request != null) {
                request.callCloseHandlers();
            }
        } else if (wrapper.data instanceof PersistentRequestData) {
            // Persistent request sent. Store and call appropriate handlers.

            var request = new PersistentRequest<>(wrapper.id, this, (PersistentRequestData) wrapper.data);
            persistentRequests.put(wrapper.id, request);
            eventEmitter.callPersistentRequestHandlers(this, request);
        } else {
            // Normal request sent. Call appropriate handlers.

            var request = new Request<>(wrapper.id, this, (RequestData<?>) wrapper.data);
            eventEmitter.callRequestHandlers(this, request);
        }

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        callEventHandlers(new ConnectedEvent());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        persistentRequests.forEach((k, v) -> v.callCloseHandlers());
        callEventHandlers(new DisconnectedEvent());
    }

    @Override
    protected void callEventHandlers(Event event) {
        eventEmitter.callEventHandlers(this, event);
    }
}
