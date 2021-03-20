package server;

import networking.data.UserData;

import java.io.DataOutputStream;
import java.nio.file.Path;

public class User implements PasswordProtected {
    private final String name;
    private String password;
    private boolean isOnline;

    public User(String name, String password) {
        this.name = name;
        this.password = password;
        this.isOnline = true;
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

    @Override
    public boolean checkPassword(String givenPassword) {
        return givenPassword.equals(password);
    }

    public UserData getAsData() {
        return new UserData(name, isOnline);
    }
}
