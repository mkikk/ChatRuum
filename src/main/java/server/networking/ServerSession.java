package server.networking;

import io.netty.channel.ChannelHandlerContext;
import networking.*;
import server.User;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static io.netty.channel.ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE;

public class ServerSession extends AbstractSession {
    protected final ServerNetworkingManager eventEmitter;
    protected final ConcurrentMap<Integer, PersistentRequest<?>> persistentRequests;

    protected User user;

    public ServerSession(ServerNetworkingManager eventEmitter) {
        this.eventEmitter = eventEmitter;
        persistentRequests = new ConcurrentHashMap<>();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void sendResponse(int id, ResponseData responseData) {
        Objects.requireNonNull(responseData, "Cannot send null response");

        ctx.writeAndFlush(new ResponseWrapper(id, responseData)).addListener(FIRE_EXCEPTION_ON_FAILURE);
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

            var request = new Request<>(wrapper.id, this, (RequestData) wrapper.data);
            eventEmitter.callRequestHandlers(this, request);
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        persistentRequests.forEach((k, v) -> v.callCloseHandlers());
        super.channelInactive(ctx);
    }

    @Override
    protected void callEventHandlers(Event event) {
        eventEmitter.callEventHandlers(this, event);
    }
}
