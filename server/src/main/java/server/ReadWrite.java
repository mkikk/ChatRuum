package server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.*;

//https://stackoverflow.com/questions/16310411/reading-multiple-json-objects-from-a-single-file-into-java-with-jackson
public class ReadWrite {
    public static Map<String, User> readUserMap(String path) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(path);
        if (!file.exists()) return new HashMap<>();
        return objectMapper.readValue(file, new TypeReference<HashMap<String, User>>() {
        });
    }

    public static Map<String, Channel> readChannelMap(String path) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(path);
        if (!file.exists()) return new HashMap<>();
        return objectMapper.readValue(file, new TypeReference<HashMap<String, Channel>>() {
        });
    }

    public static void readServer(String path, ChatRuumServer server) throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(path);
        if (!file.exists()) return;
        objectMapper.readerForUpdating(server).readValue(file);
    }

    public static void writeServer(String path, ChatRuumServer server) throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();
        final File file = new File(path);
        System.out.println(file.getAbsolutePath());
        if (!file.exists()) file.createNewFile();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, server);
    }

    public static void writeStringMap(String path, Map<String, ?> map) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final File file = new File(path);
        System.out.println(file.getAbsolutePath());
        if (!file.exists()) file.createNewFile();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, map);
    }


}
