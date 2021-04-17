package server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static server.ReadWrite.*;


import java.util.*;

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
                new HashSet<>(new ArrayList<>() {{
                    add(miilo);
                    add(jaagup);

                }})));
        channelMap.put("lamp", new Channel("lamp", "1234",
                new ArrayList<>() {{
                    add(new Message("Kas kedagi on siin", miilo));
                }},
                new HashSet<>(new ArrayList<>() {{
                    add(jaagup);
                }})));
        final HashMap<String, User> userMap = new HashMap<>();
        userMap.put("miilo", new User("miilo", "1234"));
        userMap.put("jaagup", new User("jaagup", "1234"));
        writeStringMap("src\\main\\java\\data\\testUsers.json", userMap);
        writeStringMap("src\\main\\java\\data\\testChannels.json", channelMap);
        final Map<String, User> stringUserMap = readUserMap("src\\main\\java\\data\\testUsers.json");
        final Map<String, Channel> stringChannelMap = readChannelMap("src\\main\\java\\data\\testChannels.json");
        assertEquals(stringUserMap.get("miilo").getName(), userMap.get("miilo").getName());
        assertEquals(stringUserMap.get("miilo").getPassword(), userMap.get("miilo").getPassword());
        assertEquals(stringChannelMap.get("yldine").getName(), channelMap.get("yldine").getName());
        assertEquals(stringChannelMap.get("yldine").getPassword(), channelMap.get("yldine").getPassword());
        assertEquals(stringChannelMap.get("yldine").getMessages().get(0).getText(), channelMap.get("yldine").getMessages().get(0).getText());
        assertEquals(stringChannelMap.get("yldine").getUsers().size(), channelMap.get("yldine").getUsers().size());
    }

    @Test
    public void testReadWriteServer() throws Exception {
        {
            final User jaagup = new User("jaagup", "1234");
            final User miilo = new User("miilo", "1234");
            final ChatRuumServer chatRuumServer = new ChatRuumServer(5055);
            chatRuumServer.channels.put("yldine", new Channel("yldine", "",
                    Arrays.asList(new Message("TERE", jaagup), new Message("tere??", miilo)),
                    new HashSet<>(Arrays.asList(jaagup, miilo)
                    )));
            chatRuumServer.users.put("miilo", miilo);
            writeServer("src\\main\\java\\data\\testServer.json", chatRuumServer);
        }
        {
            final ChatRuumServer chatRuumServer2 = new ChatRuumServer(5555);
            readServer("src\\main\\java\\data\\testServer.json", chatRuumServer2);
            final Set<User> kasutajad = chatRuumServer2.channels.get("yldine").getUsers();
            final Iterator<User> it = kasutajad.iterator();
            User next = it.next();
            while (it.hasNext()) {
                if (next.getName().equals("miilo")) {
                    System.out.println("nice");
                    break;
                }
                it.remove();
                next = it.next();
            }
            User miilo1 = chatRuumServer2.channels.get("yldine").getMessages().get(1).getSender();
            User miilo2 = chatRuumServer2.users.get("miilo");
            System.out.println(miilo1.hashCode() + " " + miilo2.hashCode() + " " + next.hashCode());
            assertEquals(miilo1, miilo2);
            assertEquals(next, miilo2);
        }
    }
}
