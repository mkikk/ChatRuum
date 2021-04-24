package server;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import networking.data.UserData;

import java.util.Arrays;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "name", scope = User.class)
public class User implements PasswordProtected {
    private String name;
    private byte[] password;
    private byte[] iv;
    private byte[] key;
    @JsonIgnore
    private boolean isOnline;

    public User(@JsonProperty(value = "name") String name, @JsonProperty(value = "password") byte[] password,
                @JsonProperty(value = "key") byte[] key, @JsonProperty(value = "iv") byte[] iv) {
        this.name = name;
        this.password = password;
        this.iv = iv;
        this.key = key;
        this.isOnline = true;
    }

    public User(String name, String password) {
        this.name = name;
        this.key = Crypto.generateKey(password);
        this.iv = Crypto.generateIV();
        this.password = Crypto.encrypt(password, key, iv);
        this.isOnline = true;
    }

    public byte[] getPassword() {
        return password;
    }

    public void LogOff() {
        this.isOnline = false;
    }

    public String getName() {
        return name;
    }

    public byte[] getIv() {
        return iv;
    }

    public byte[] getKey() {
        return key;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", password='" + Arrays.toString(password) + '\'' +
                ", isOnline=" + isOnline +
                '}';
    }

    @Override
    public boolean checkPassword(byte[] givenPassword) {
        System.out.println(Arrays.toString(password) + "==" + Arrays.toString(givenPassword));
        return Arrays.equals(givenPassword, password);
    }

    public UserData convertToData() {
        return new UserData(name, isOnline);
    }
}
