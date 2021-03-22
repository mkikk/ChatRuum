package networking;

public interface Session {
    /**
     * Sends the message over the network session as soon as possible.
     * Note that a send may still fail, as the actual sending may occur after this method has run in a different thread.
     * @param message
     */
    void sendMessage(Message message);

    // boolean isActive();
}
