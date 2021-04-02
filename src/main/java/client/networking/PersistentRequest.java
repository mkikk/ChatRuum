package client.networking;

public class PersistentRequest extends Request {
    public PersistentRequest(int id, ClientSession session) {
        super(id, session);
    }

    public void close() {
        session.closePersistentRequest(id);
    }

    @Override
    public boolean isPersistent() {
        return true;
    }
}
