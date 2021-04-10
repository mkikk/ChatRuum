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
    public static void main(String[] args) throws Exception {
//        final Map<String, User> stringUserMap = readUsers("test.json");
//        System.out.println(stringUserMap.toString());
        final HashMap<String, Channel> testHashMap = new HashMap<>();
        final User jaagup = new User("jaagup", "1234");
        final User miilo = new User("miilo", "1234");
        testHashMap.put("yldine", new Channel("yldine", "",
                new ArrayList<>(){{add(new Message("TERE", jaagup));add(new Message("tere??", miilo));}},
                new HashSet<User>(new ArrayList<>(){{add(jaagup);add(miilo);}})));
        testHashMap.put("lamp", new Channel("lamp", "1234",
                new ArrayList<>(){{add(new Message("Kas kedagi on siin", miilo));}},
                new HashSet<User>(new ArrayList<>(){{add(jaagup);}})));
        final HashMap<String, User> userTestHashMap = new HashMap<>();
        userTestHashMap.put("miilo", new User("miilo", "1234"));
        userTestHashMap.put("jaagup", new User("jaagup", "1234"));
        writeUsers("users.json", userTestHashMap);
        writeChannels("channels.json", testHashMap);
        final Map<String, User> stringUserMap = readUsers("users.json");
        System.out.println(stringUserMap.toString());
        final Map<String, Channel> stringChannelMap = readChannels("channels.json");
        System.out.println(stringChannelMap.toString());

    }
    public static Map<String, User> readUsers(String path) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        File file = Paths.get(path).toFile();
        final Map<String, User> stringUserMap = objectMapper.readValue(file, new TypeReference<Map<String, User>>() {
        });

        return stringUserMap;
    }
    public static void writeUsers(String path, Map<String, User> users) throws Exception{
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
    public static void writeChannels(String path, Map<String, Channel> channels) throws Exception{
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(Paths.get(path).toFile(), channels);

    }
}
