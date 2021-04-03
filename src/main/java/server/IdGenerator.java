package server;

public class IdGenerator {
    private int id = -1;

    public int getId() {
        id += 1;
        return id;
    }
}
