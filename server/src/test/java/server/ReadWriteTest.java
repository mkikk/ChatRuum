package server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static server.ReadWrite.*;


import java.util.*;

public class ReadWriteTest {
    @Test
    public void testReadWriteServer() throws Exception {
        {
            System.out.println("Writing server to JSON");

            final User jaagup = new User("jaagup", "1234");
            final User miilo = new User("miilo", "1234");
            final ChatRuumServer chatRuumServer = new ChatRuumServer(5055);
            chatRuumServer.channels.put("yldine", new Channel("yldine", null,
                    Arrays.asList(new Message("TERE", jaagup), new Message("tere??", miilo)),
                    new HashSet<>(Arrays.asList(jaagup, miilo)))
            );
            chatRuumServer.channels.put("teine", new Channel("teine", "2312"));
            chatRuumServer.users.put("miilo", miilo);
            chatRuumServer.users.put("jaagup", jaagup);

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
                    break;
                }
                next = it.next();
            }
            User miilo1 = chatRuumServer2.channels.get("yldine").getMessages().get(1).getSender();
            User miilo2 = chatRuumServer2.users.get("miilo");
            System.out.println(miilo1.hashCode() + " " + miilo2.hashCode() + " " + next.hashCode());
            assertSame(miilo1, miilo2);
            assertSame(next, miilo2);

            var stringUserMap = chatRuumServer2.users;
            var stringChannelMap = chatRuumServer2.channels;
            assertEquals(stringUserMap.get("miilo").getName(), "miilo");
            assertTrue(stringUserMap.get("miilo").checkPassword("1234"));
            assertEquals(stringChannelMap.get("yldine").getName(), "yldine");
            assertTrue(stringChannelMap.get("yldine").checkPassword(""));
            assertEquals(stringChannelMap.get("yldine").getMessages().get(0).getText(), "TERE");
            assertEquals(stringChannelMap.get("yldine").getUsers().size(), 2);

            assertTrue(stringChannelMap.get("yldine").checkPassword(null));
            assertTrue(stringChannelMap.get("yldine").checkPassword("fasdfads"));
            assertTrue(stringChannelMap.get("teine").checkPassword("2312"));
            assertFalse(stringChannelMap.get("teine").checkPassword("2313"));
        }
    }
}
