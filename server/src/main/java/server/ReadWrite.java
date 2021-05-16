package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;

//https://stackoverflow.com/questions/16310411/reading-multiple-json-objects-from-a-single-file-into-java-with-jackson
public class ReadWrite {
    private static final Logger logger = LogManager.getLogger();

    public static void readServer(String path, ChatRuumServer server) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());

        File file = new File(path);
        if (!file.exists()) return;
        objectMapper.readerForUpdating(server).readValue(file);
    }

    public static void writeServer(String path, ChatRuumServer server) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();
        final File file = new File(path);
        logger.debug("Saving to: " + file.getAbsolutePath());
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, server);
    }
}
