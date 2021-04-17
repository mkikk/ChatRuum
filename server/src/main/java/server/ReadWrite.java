package server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

//https://stackoverflow.com/questions/16310411/reading-multiple-json-objects-from-a-single-file-into-java-with-jackson
public class ReadWrite {
    public static <T> Map<String, T> readStringMap(String path) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        File file = new File("src\\main\\java\\data\\" + path);
        if (!file.exists()) return new HashMap<>();
        return objectMapper.readValue(file, new TypeReference<>(){});
    }

    public static void writeStringMap(String path, Map<String, ?> map) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File("src\\main\\java\\data\\" + path), map);
    }
}
