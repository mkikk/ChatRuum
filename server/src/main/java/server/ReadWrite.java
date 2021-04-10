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

public class ReadWrite {
    public static void main(String[] args) throws Exception {
//        final Map<String, User> stringUserMap = readUsers("test.json");
//        System.out.println(stringUserMap.toString());
        final HashMap<String, Channel> testHashMap = new HashMap<>();
        testHashMap.put("yldine", new Channel("yldine", ""));
        testHashMap.put("lamp", new Channel("lamp", "1234"));
        writeChannels("test.json", testHashMap);
        //readChannels("test.json");

    }
    public static Map<String, User> readUsers(String path) throws IOException {
        final Map<String, User> users = new HashMap<>();
        final ObjectMapper objectMapper = new ObjectMapper();
        File file = Paths.get(path).toFile();
        JsonParser parser;
        try {
            parser = new JsonFactory().createParser(Paths.get(path).toFile());
        } catch (FileNotFoundException e) {
            file.createNewFile();
            parser = new JsonFactory().createParser(Paths.get(path).toFile());

        }
        for(Iterator it = objectMapper.readValues(parser, User.class)
                ;it.hasNext();) {
            final User next = (User) it.next();
            users.put(next.getName(), next);
        }
        return users;
    }
    public static void writeUsers(String path, Map<String, User> users) throws Exception{
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        users.forEach((name, user) -> {
            try {
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(Paths.get(path).toFile(), user);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }
    public static Map<String, Channel> readChannels(String path) throws IOException {
        final Map<String, Channel> channels = new HashMap<>();
        final ObjectMapper objectMapper = new ObjectMapper();
        File file = Paths.get(path).toFile();
        JsonParser parser;
        try {
            parser = new JsonFactory().createParser(Paths.get(path).toFile());
        } catch (FileNotFoundException e) {
            file.createNewFile();
            parser = new JsonFactory().createParser(Paths.get(path).toFile());

        }

        for(Iterator it = objectMapper.readValues(parser, new TypeReference<Channel>(){})
            ;it.hasNext();) {
            final Channel next = (Channel) it.next();
            channels.put(next.getName(), next);
        }
        return channels;
    }
    public static void writeChannels(String path, Map<String, Channel> channels) throws Exception{
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ObjectNode rootNode = objectMapper.createObjectNode();
        channels.forEach((name, channel) -> {
            try {
                final String s = objectMapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(channel);
                rootNode.put(channel.getName(), s);
                System.out.println(s);

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }});
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(Paths.get(path).toFile(), rootNode);

    }
}
