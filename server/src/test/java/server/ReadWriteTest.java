package server;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static server.ReadWrite.*;

public class ReadWriteTest {
    @Test
    public void testReadWrite() throws Exception {
        final HashMap<String, Channel> testHashMap = new HashMap<>();
        final User jaagup = new User("jaagup", "1234");
        final User miilo = new User("miilo", "1234");
        testHashMap.put("yldine", new Channel("yldine", "",
                new ArrayList<>() {{
                    add(new Message("TERE", jaagup));
                    add(new Message("tere??", miilo));
                }},
                new HashSet<User>(new ArrayList<>() {{
                    add(jaagup);
                    add(miilo);
                }})));
        testHashMap.put("lamp", new Channel("lamp", "1234",
                new ArrayList<>() {{
                    add(new Message("Kas kedagi on siin", miilo));
                }},
                new HashSet<User>(new ArrayList<>() {{
                    add(jaagup);
                }})));
        final HashMap<String, User> userTestHashMap = new HashMap<>();
        userTestHashMap.put("miilo", new User("miilo", "1234"));
        userTestHashMap.put("jaagup", new User("jaagup", "1234"));
        writeUsers("testUsers.json", userTestHashMap);
        writeChannels("testChannels.json", testHashMap);
        final Map<String, User> stringUserMap = readUsers("testUsers.json");
        System.out.println(stringUserMap.toString());
        final Map<String, Channel> stringChannelMap = readChannels("testChannels.json");
        System.out.println(stringChannelMap.toString());
    }
}
