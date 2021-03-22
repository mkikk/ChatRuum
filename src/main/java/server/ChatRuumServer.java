package server;

import networking.netty.NettyServer;
import networking.events.responses.Response;
import networking.events.responses.LoginResponseMessage;
import networking.events.requests.*;

import java.util.HashMap;
import java.util.Map;

public class ChatRuumServer {
    private final Map<String, Channel> channels;
    private final Map<String, User> users;
    private final NettyServer server;
    private Thread serverThread;

    public ChatRuumServer(int port) {
        channels = new HashMap<>();
        users = new HashMap<>();
        server = new NettyServer(port);
        setupServer();
    }

    private void setupServer() {
        server.on(PasswordLoginMessage.class, (session, msg) -> {
            System.out.println("User logging in: " + msg.username);

            final User user = users.get(msg.username);
            if (user != null && user.checkPassword(msg.password)) {
                session.setUser(user);
                session.sendMessage(new LoginResponseMessage(Response.OK));
            } else {
                session.sendMessage(new LoginResponseMessage(Response.FORBIDDEN));
            }
        });

        server.on(JoinChannelMessage.class, (session,msg) -> {
            System.out.println("Joining channel: " + msg.channelName);
            final Channel channel = channels.get(msg.channelName);
            if (channel != null && channel.checkPassword(msg.channelPassword)) {
                session.setActiveChannel(channel);
            }
        });

        server.on(ViewChannelMessage.class, (session, msg) -> {
            System.out.println("Viewing channel: " + msg.channelName);
            session.setActiveChannel(channels.get(msg.channelName));
        });
        server.on(SendMessageMessage.class, (session, msg) -> {
            Channel channel = session.getActiveChannel();
            channel.sendMessage(new Message(msg.text, session.getUser()));

        });
        server.on(ExitChannelMessage.class, (session, msg) -> {
            System.out.println("Exiting channel: " + msg.channelName);
            session.setActiveChannel(null);
        });
    }

    public void startServer() {
        if (serverThread != null) return; // Server already running
        serverThread = new Thread(server);
        serverThread.start();
    }
}
