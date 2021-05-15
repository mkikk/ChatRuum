package server;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import networking.data.UserData;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "name", scope = User.class
)
public class User {
    private final String name;
    @NotNull private final Password password;
    @JsonIgnore
    private boolean isOnline;
    private Map<String, Integer> favoriteChannels;

    public User(@JsonProperty(value = "name") String name, @JsonProperty(value = "password") @NotNull Password password,
                @JsonProperty(value = "favoriteChannels") Map<String, Integer> favoriteChannels) {
        this.name = name;
        this.password = password;
        this.isOnline = true;
        this.favoriteChannels = favoriteChannels;
    }

    public User(String name, @NotNull String password) {
        this.name = name;
        this.password = new Password(password);
        this.isOnline = true;
        this.favoriteChannels = new HashMap<>();
    }

    public void logOff() {
        this.isOnline = false;
    }

    public @NotNull Password getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public Map<String, Integer> getFavoriteChannels() {
        return favoriteChannels;
    }

    public void addVisitChannel(String channelName){
        final Integer visits = this.favoriteChannels.getOrDefault(channelName, 0);
        this.favoriteChannels.put(channelName, visits+1);
        System.out.println(favoriteChannels.toString());
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

    public UserData convertToData() {
        return new UserData(name, isOnline);
    }
}
