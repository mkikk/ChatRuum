package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.*;

public class ReadWriteManagerTest {
    private static final Logger logger = LogManager.getLogger();

    @Test
    public void testReadWriteServer() throws Exception {
        final var time1 = Instant.parse("2020-03-12T13:12:32.321Z");
        final var time2 = Instant.now();

        {
            logger.info("Writing server to JSON");

            final ChatRuumServer chatRuumServer = new ChatRuumServer(5055);
            final ReadWriteManager readWrite = new ReadWriteManager("testServer.json", chatRuumServer);

            final User jaagup = new User("jaagup", "1234");
            final User miilo = new User("miilo", "1234");
            chatRuumServer.channels.put("yldine", new Channel(
                    "yldine",
                    null,
                    Arrays.asList(new Message("TERE", jaagup, time1), new Message("tere??", miilo, time2)),
                    new HashSet<>(Arrays.asList(jaagup, miilo))
            ));
            chatRuumServer.channels.put("teine", new Channel("teine", "2312"));
            chatRuumServer.users.put("miilo", miilo);
            chatRuumServer.users.put("jaagup", jaagup);

            readWrite.writeServer();
        }

        {
            final ChatRuumServer chatRuumServer2 = new ChatRuumServer(5555);
            final ReadWriteManager readWrite = new ReadWriteManager("testServer.json", chatRuumServer2);
            readWrite.readServer();

            final Set<User> kasutajad = chatRuumServer2.channels.get("yldine").getUsers();
            User miiloKasutajad = kasutajad.stream().filter(u -> u.getName().equals("miilo")).findFirst().orElseThrow();
            User miilo1 = chatRuumServer2.channels.get("yldine").getMessages().get(1).getSender();
            User miilo2 = chatRuumServer2.users.get("miilo");
            logger.info(miilo1.hashCode() + " " + miilo2.hashCode() + " " + miiloKasutajad.hashCode());
            assertSame(miilo1, miilo2);
            assertSame(miiloKasutajad, miilo2);

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

            assertEquals(stringChannelMap.get("yldine").getMessages().get(0).getTime(), time1);
            assertEquals(stringChannelMap.get("yldine").getMessages().get(1).getTime(), time2);
        }
    }
}
