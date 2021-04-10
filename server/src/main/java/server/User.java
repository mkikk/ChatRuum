package server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import networking.data.UserData;

import java.io.DataOutputStream;
import java.nio.file.Path;

public class User implements PasswordProtected {
    @JsonProperty(value = "name")
    private String name;
    @JsonProperty(value = "password")
    private String password;
    @JsonIgnore
    private boolean isOnline;

    public User(String name, String password) {
        this.name = name;
        this.password = password;
        this.isOnline = true;
    }
    public User() {

    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public void LogOff(){
        this.isOnline = false;
    }

    private void writeToFile(int id, String name, String password) {
//        try(final var dataOutputStream = new DataOutputStream(Path)) {
//            dataOutputStream.writeUTF(name, password);
//        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
