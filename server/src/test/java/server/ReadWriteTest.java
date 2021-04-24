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
        channelMap.put("yldine", new Channel("yldine", Crypto.encrypt(""),
                new ArrayList<>() {{
                    add(new Message("TERE", jaagup));
                    add(new Message("tere??", miilo));
                }},
                new HashSet<>(new ArrayList<>() {{
                    add(miilo);
                    add(jaagup);

                }}), Crypto.generateKey(""), Crypto.generateIV()));
        channelMap.put("lamp", new Channel("lamp", Crypto.encrypt("1234"),
                new ArrayList<>() {{
                    add(new Message("Kas kedagi on siin", miilo));
                }},
                new HashSet<>(new ArrayList<>() {{
                    add(jaagup);
                }}), Crypto.generateKey("1234"), Crypto.generateIV()));
        final HashMap<String, User> userMap = new HashMap<>();
        userMap.put("miilo", new User("miilo", "1234"));
        userMap.put("jaagup", new User("jaagup", "1234"));
        writeStringMap("testUsers.json", userMap);
        writeStringMap("testChannels.json", channelMap);
        final Map<String, User> stringUserMap = readUserMap("testUsers.json");
        final Map<String, Channel> stringChannelMap = readChannelMap("testChannels.json");
        assertEquals(stringUserMap.get("miilo").getName(), userMap.get("miilo").getName());
        assertTrue(stringUserMap.get("miilo").checkPassword(userMap.get("miilo").getPassword()));
        assertEquals(stringChannelMap.get("yldine").getName(), channelMap.get("yldine").getName());
        assertTrue(stringChannelMap.get("yldine").checkPassword(channelMap.get("yldine").getPassword()));
        assertEquals(stringChannelMap.get("yldine").getMessages().get(0).getText(), channelMap.get("yldine").getMessages().get(0).getText());
        assertEquals(stringChannelMap.get("yldine").getUsers().size(), channelMap.get("yldine").getUsers().size());
    }

    @Test
    public void testReadWriteServer() throws Exception {
        {
            final User jaagup = new User("jaagup", "1234");
            final User miilo = new User("miilo", "1234");
            final ChatRuumServer chatRuumServer = new ChatRuumServer(5055);
            chatRuumServer.channels.put("yldine", new Channel("yldine", Crypto.encrypt(""),
                    Arrays.asList(new Message("TERE", jaagup), new Message("tere??", miilo)),
                    new HashSet<>(Arrays.asList(jaagup, miilo)
                    ), Crypto.generateKey(""), Crypto.generateIV()));
            chatRuumServer.users.put("miilo", miilo);
            writeServer("testServer.json", chatRuumServer);
        }
        {
            final ChatRuumServer chatRuumServer2 = new ChatRuumServer(5555);
            readServer("testServer.json", chatRuumServer2);
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
