package server;

public interface PasswordProtected {
    boolean checkPassword(byte[] givenPassword);
}
