package server;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import networking.data.UserData;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "name", scope = User.class
)
public class User {
    private final String name;
    @NotNull private final Password password;
    @JsonIgnore
    private boolean isOnline;

    public User(@JsonProperty(value = "name") String name, @JsonProperty(value = "password") @NotNull Password password) {
        this.name = name;
        this.password = password;
        this.isOnline = true;
    }

    public User(String name, @NotNull String password) {
        this.name = name;
        this.password = new Password(password);
        this.isOnline = true;
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
