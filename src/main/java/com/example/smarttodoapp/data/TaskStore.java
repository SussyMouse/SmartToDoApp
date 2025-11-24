package com.example.smarttodoapp.data;

import com.example.smarttodoapp.model.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class TaskStore {

    // Use uppercase name and use the same name everywhere
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    // Save inside your project at:
    // ...\SmartToDoApp\src\main\java\com\example\smarttodoapp\tasks.json
    private static final Path STORE_PATH = Paths.get(
            System.getProperty("user.dir"),
            "src", "main", "resources",
            "com", "example", "smarttodoapp",
            "tasks.json"
    );

    public static ObservableList<Task> load() {
        try {
            ensureFileReady();

            // Read all text first, sanity check, then parse
            String json = Files.readString(STORE_PATH).trim();
            if (json.isEmpty()) {
                // empty file -> repair
                Files.writeString(STORE_PATH, "[]", StandardOpenOption.TRUNCATE_EXISTING);
                json = "[]";
            }

            List<Task> list = MAPPER.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<List<Task>>() {});
            System.out.println("Loaded from: " + STORE_PATH.toAbsolutePath() + " (" + list.size() + " items)");
            return javafx.collections.FXCollections.observableArrayList(list);

        } catch (com.fasterxml.jackson.core.JsonProcessingException jp) {
            // Corrupted JSON -> auto-repair to empty array
            System.out.println("Corrupted JSON detected. Resetting to empty array. " + jp.getMessage());
            try {
                Files.writeString(STORE_PATH, "[]", StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException ignore) {}
            return javafx.collections.FXCollections.observableArrayList();

        } catch (IOException e) {
            System.out.println("Error loading tasks: " + e.getMessage());
            return javafx.collections.FXCollections.observableArrayList();
        }
    }

    private static void ensureFileReady() throws IOException {
        if (java.nio.file.Files.notExists(STORE_PATH.getParent())) {
            java.nio.file.Files.createDirectories(STORE_PATH.getParent());
        }
        if (java.nio.file.Files.notExists(STORE_PATH)) {
            java.nio.file.Files.writeString(STORE_PATH, "[]"); // create with empty array
        }
    }

    public static void save(javafx.collections.ObservableList<Task> tasks) {
        try {
            ensureFileReady();
            java.util.List<Task> list = new java.util.ArrayList<>(tasks);
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(STORE_PATH.toFile(), list);
            System.out.println("Saved to: " + STORE_PATH.toAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error saving tasks: " + e.getMessage());
        }
    }

}
