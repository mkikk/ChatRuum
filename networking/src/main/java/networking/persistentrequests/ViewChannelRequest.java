package networking.persistentrequests;

import networking.PersistentRequestData;

public class ViewChannelRequest implements PersistentRequestData {
    private static final long serialVersionUID = -7445500520523410041L;

    public final String channelName;

    public ViewChannelRequest(String channelName) {
        this.channelName = channelName;
    }
}
