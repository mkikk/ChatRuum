package server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

//https://attacomsian.com/blog/jackson-create-json-object
//https://stackoverflow.com/questions/16310411/reading-multiple-json-objects-from-a-single-file-into-java-with-jackson
public class ReadWrite {
    public static Map<String, User> readUsers(String path) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        File file = Paths.get(path).toFile();
        if (file.createNewFile() || Files.readAllLines(Path.of(path)).isEmpty()) return new HashMap<>();
        return objectMapper.readValue(file, new TypeReference<>(){});
    }

    public static void writeUsers(String path, Map<String, User> users) throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(Paths.get(path).toFile(), users);
    }

    public static Map<String, Channel> readChannels(String path) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        File file = Paths.get(path).toFile();
        if (file.createNewFile() || Files.readAllLines(Path.of(path)).isEmpty()) return new HashMap<>();
        return objectMapper.readValue(file, new TypeReference<>() {});
    }

    public static void writeChannels(String path, Map<String, Channel> channels) throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(Paths.get(path).toFile(), channels);
    }
}
