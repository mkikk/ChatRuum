package networking.responses;

import networking.ResponseData;

import java.util.Map;

public class FavoriteChannelResponse implements ResponseData {
    private static final long serialVersionUID = 8869541993724591005L;

    public Map<String, Integer> channelPopularity;
    public Response response;

    public FavoriteChannelResponse(Response response, Map<String, Integer> channelPopularity) {
        this.channelPopularity = channelPopularity;
        this.response = response;
    }
}
