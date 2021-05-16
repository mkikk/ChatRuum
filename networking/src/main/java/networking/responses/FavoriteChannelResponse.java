package networking.responses;

import networking.ResponseData;

import java.util.Map;

public class FavoriteChannelResponse implements ResponseData {
    public Map<String, Integer> channelPopularity;
    public Response response;

    public FavoriteChannelResponse(Response response, Map<String, Integer> channelPopularity) {
        this.channelPopularity = channelPopularity;
        this.response = response;
    }
}
