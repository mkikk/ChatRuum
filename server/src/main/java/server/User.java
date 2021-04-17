package server;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import networking.data.UserData;

import java.io.DataOutputStream;
import java.nio.file.Path;
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "name", scope = User.class)
public class User implements PasswordProtected {
    private String name;
    private String password;
    @JsonIgnore
    private boolean isOnline;

    public User(@JsonProperty(value = "name") String name, @JsonProperty(value = "password")String password) {
        this.name = name;
        this.password = password;
        this.isOnline = true;
    }

    public String getPassword() {
        return password;
    }

    public void LogOff(){
        this.isOnline = false;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", isOnline=" + isOnline +
                '}';
    }

    @Override
    public boolean checkPassword(String givenPassword) {
        return givenPassword.equals(password);
    }

    public UserData convertToData() {
        return new UserData(name, isOnline);
    }
}
