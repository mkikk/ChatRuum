package server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;

//https://stackoverflow.com/questions/16310411/reading-multiple-json-objects-from-a-single-file-into-java-with-jackson
public class ReadWriteManager {
    private static final Logger logger = LogManager.getLogger();

    @NotNull private final File file;
    @NotNull private final ChatRuumServer server;

    private final Thread shutdownThread;

    private final ObjectMapper objectMapper;

    /**
     * Creates ReadWriteManager, which manages saving server data to file.
     * @param path path to read from and write to
     * @param server server for which to read and write data
     */
    public ReadWriteManager(@NotNull String path, @NotNull ChatRuumServer server) {
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        this.file = new File(path);
        this.server = server;

        shutdownThread = new Thread(() -> {
            logger.info("JVM shutting down. Saving server data to file...");
            try {
                writeServer();
                logger.info("Server data saved to file.");
            } catch (Exception exception) {
                logger.error("Error writing server data during JVM shutdown hook:", exception);
            }
        }, "rw-shutdown-thread");
    }

    /**
     * Sets up shutdown hook to write to file on shutdown.
     * Call cleanup when stopping server to ensure hook is cleaned up.
     */
    public void setup() {
        Runtime.getRuntime().addShutdownHook(shutdownThread);
    }

    /**
     * Cleans up shutdown hook.
     * Should be called when server is stopped from code.
     */
    public void cleanup() {
        Runtime.getRuntime().removeShutdownHook(shutdownThread);
    }

    /**
     * Reads server data from file into existing server instance.
     * @throws IOException if reading failed.
     */
    public void readServer() throws IOException {
        if (!file.exists()) return;
        logger.debug("Reading from: " + file.getAbsolutePath());
        objectMapper.readerForUpdating(server).readValue(file);
    }

    /**
     * Writes server data to file.
     * @throws IOException if writing failed
     */
    public void writeServer() throws IOException {
        logger.debug("Writing to: " + file.getAbsolutePath());
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, server);
    }

    /**
     * Tries to writes server data to file.
     * Never throws. Logs error if write failed.
     */
    public void tryWriteServer() {
        try {
            writeServer();
        } catch (Exception exception) {
            logger.error("Error while trying to write server data to file:", exception);
        }

    }
}
