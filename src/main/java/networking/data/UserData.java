package networking.data;

import java.io.Serializable;

public class UserData implements Serializable {
    public final String name;
    public final boolean isOnline;

    public UserData(String name, boolean isOnline) {
        this.name = name;
        this.isOnline = isOnline;
    }
}
