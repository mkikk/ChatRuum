package networking.persistentrequests;

import networking.PersistentRequestData;

public class ViewChannelRequest implements PersistentRequestData {
    public final String channelName;

    public ViewChannelRequest(String channelName) {
        this.channelName = channelName;
    }
}
