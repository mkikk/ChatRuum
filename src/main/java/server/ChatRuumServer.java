package server;

import server.networking.ServerNetworkingManager;

import java.util.HashMap;
import java.util.Map;

public class ChatRuumServer {
    private final Map<String, Channel> channels;
    private final Map<String, User> users;
    private final ServerNetworkingManager server;
    private Thread serverThread;

    public ChatRuumServer(int port) {
        channels = new HashMap<>();
        users = new HashMap<>();
        server = new ServerNetworkingManager(port);
        setupServer();
    }

    private void setupServer() {
        /*server.onRequest(PasswordLoginMessage.class, (session, req) -> {
            System.out.println("User logging in: " + req.data.username);

            final User user = users.get(req.data.username);
            if (user != null && user.checkPassword(req.data.password)) {
                session.setUser(user);
                session.sendMessage(new LoginResponseMessage(Response.OK));
            } else {
                session.sendMessage(new LoginResponseMessage(Response.FORBIDDEN));
            }
        });

        server.onRequest(JoinChannelMessage.class, (session, msg) -> {
            System.out.println("Joining channel: " + msg.channelName);
            final Channel channel = channels.get(msg.channelName);
            if (channel != null && channel.checkPassword(msg.channelPassword)) {
                session.setActiveChannel(channel);
            }
        });

        server.onRequest(ViewChannelMessage.class, (session, msg) -> {
            System.out.println("Viewing channel: " + msg.channelName);
            session.setActiveChannel(channels.get(msg.channelName));
        });
        server.onRequest(SendMessageMessage.class, (session, msg) -> {
            Channel channel = session.getActiveChannel();
            channel.sendMessage(new Message(msg.text, session.getUser()));

        });
        server.onRequest(ExitChannelMessage.class, (session, msg) -> {
            System.out.println("Exiting channel: " + msg.channelName);
            session.setActiveChannel(null);
        });*/
    }

    public void startServer() {
        if (serverThread != null) return; // Server already running
        serverThread = new Thread(server);
        serverThread.start();
    }
}
