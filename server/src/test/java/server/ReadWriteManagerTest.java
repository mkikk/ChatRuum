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
            miilo.addVisitChannel("yldine");
            miilo.addVisitChannel("yldine");
            miilo.addVisitChannel("yldine");
            miilo.addVisitChannel("teine");

            readWrite.writeServer();
        }

        {
            final ChatRuumServer chatRuumServer2 = new ChatRuumServer(5555);
            final ReadWriteManager readWrite = new ReadWriteManager("testServer.json", chatRuumServer2);
            readWrite.readServer();

            final var stringUserMap = chatRuumServer2.users;
            final var stringChannelMap = chatRuumServer2.channels;
            assertEquals(stringUserMap.size(), 2);
            assertEquals(stringChannelMap.size(), 2);

            final List<User> channelUsers = stringChannelMap.get("yldine").getUsers();
            assertEquals(channelUsers.size(), 2);
            User miiloChannel = channelUsers.stream().filter(u -> u.getName().equals("miilo")).findFirst().orElseThrow();
            User miilo1 = stringChannelMap.get("yldine").getMessages().get(1).getSender();
            User miilo2 = stringUserMap.get("miilo");
            logger.info(miilo1.hashCode() + " " + miilo2.hashCode() + " " + miiloChannel.hashCode());
            assertSame(miilo1, miilo2);
            assertSame(miiloChannel, miilo2);
            assertEquals(miilo1.getFavoriteChannels().get("yldine"), 3);
            assertEquals(miilo1.getFavoriteChannels().get("teine"), 1);

            assertEquals(stringUserMap.get("miilo").getName(), "miilo");
            assertTrue(stringUserMap.get("miilo").checkPassword("1234"));
            assertEquals(stringChannelMap.get("yldine").getName(), "yldine");
            assertTrue(stringChannelMap.get("yldine").checkPassword(""));

            assertTrue(stringChannelMap.get("yldine").checkPassword(null));
            assertTrue(stringChannelMap.get("yldine").checkPassword("fasdfads"));
            assertTrue(stringChannelMap.get("teine").checkPassword("2312"));
            assertFalse(stringChannelMap.get("teine").checkPassword("2313"));

            final List<Message> channelMessages = stringChannelMap.get("yldine").getMessages();
            assertEquals(channelMessages.get(0).getText(), "TERE");
            assertEquals(channelMessages.get(0).getTime(), time1);
            assertEquals(channelMessages.get(1).getTime(), time2);
        }
    }
}
