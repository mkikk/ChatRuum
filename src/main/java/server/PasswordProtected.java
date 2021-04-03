package server;

public interface PasswordProtected {
    boolean checkPassword(String givenPassword);
}
