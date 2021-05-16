package server;

import com.fasterxml.jackson.annotation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "name", scope = User.class
)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class User {
    private final String name;
    @NotNull private final Password password;
    private final Map<String, Integer> favoriteChannels;

    @JsonIgnore private boolean isOnline;

    public User(String name, @NotNull String password) {
       this(name, new Password(password), new HashMap<>());
    }

    @JsonCreator
    public User(@JsonProperty(value = "name") String name, @JsonProperty(value = "password") @NotNull Password password,
                @JsonProperty(value = "favoriteChannels") Map<String, Integer> favoriteChannels) {
        this.name = name;
        this.password = password;
        this.isOnline = true;
        this.favoriteChannels = favoriteChannels;
    }

    public String getName() {
        return name;
    }

    public Map<String, Integer> getFavoriteChannels() {
        return favoriteChannels;
    }

    public void addVisitChannel(String channelName){
        final int visits = favoriteChannels.getOrDefault(channelName, 0);
        favoriteChannels.put(channelName, visits + 1);
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", isOnline=" + isOnline +
                '}';
    }

    public boolean checkPassword(String givenPassword) {
        return password.checkPassword(givenPassword);
    }
}
