package server;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.JSONPObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

//https://attacomsian.com/blog/jackson-create-json-object
//https://stackoverflow.com/questions/16310411/reading-multiple-json-objects-from-a-single-file-into-java-with-jackson
public class ReadWrite {
    public static Map<String, User> readUsers(String path) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        File file = Paths.get(path).toFile();
        final Map<String, User> stringUserMap = objectMapper.readValue(file, new TypeReference<Map<String, User>>() {
        });

        return stringUserMap;
    }

    public static void writeUsers(String path, Map<String, User> users) throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(Paths.get(path).toFile(), users);
    }

    public static Map<String, Channel> readChannels(String path) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        File file = Paths.get(path).toFile();
        final Map<String, Channel> stringChannelMap = objectMapper.readValue(file, new TypeReference<Map<String, Channel>>() {
        });
        return stringChannelMap;
    }

    public static void writeChannels(String path, Map<String, Channel> channels) throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(Paths.get(path).toFile(), channels);
    }
}
