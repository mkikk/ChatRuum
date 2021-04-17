package server;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static server.ReadWrite.readStringMap;
import static server.ReadWrite.writeStringMap;

public class ReadWriteTest {
    @Test
    public void testReadWrite() throws Exception {
        final HashMap<String, Channel> channelMap = new HashMap<>();
        final User jaagup = new User("jaagup", "1234");
        final User miilo = new User("miilo", "1234");
        channelMap.put("yldine", new Channel("yldine", "",
                new ArrayList<>() {{
                    add(new Message("TERE", jaagup));
                    add(new Message("tere??", miilo));
                }},
                new HashSet<User>(new ArrayList<>() {{
                    add(jaagup);
                    add(miilo);
                }})));
        channelMap.put("lamp", new Channel("lamp", "1234",
                new ArrayList<>() {{
                    add(new Message("Kas kedagi on siin", miilo));
                }},
                new HashSet<User>(new ArrayList<>() {{
                    add(jaagup);
                }})));
        final HashMap<String, User> userMap = new HashMap<>();
        userMap.put("miilo", new User("miilo", "1234"));
        userMap.put("jaagup", new User("jaagup", "1234"));
        writeStringMap("testUsers.json", userMap);
        writeStringMap("testChannels.json", channelMap);
        final Map<String, User> stringUserMap = readStringMap("testUsers.json");
        final Map<String, Channel> stringChannelMap = readStringMap("testChannels.json");

        System.out.println(userMap +"\n" + stringUserMap.toString());
        System.out.println();
        System.out.println(channelMap + "\n" +stringChannelMap.toString());
//        assertTrue(userMap.equals(stringUserMap));
//        assertTrue(userMap.equals(stringUserMap));
    }
}
