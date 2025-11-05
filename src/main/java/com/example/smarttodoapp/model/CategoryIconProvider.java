package com.example.smarttodoapp.model;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Provides shared access to category icons stored in the application resources.
 */
public final class CategoryIconProvider {

    private static final Map<String, Image> ICONS = createIconMap();

    private CategoryIconProvider() {
        // Utility class
    }

    private static Map<String, Image> createIconMap() {
        Map<String, Image> icons = new HashMap<>();
        register(icons, "work", "Work_Icon.png");
        register(icons, "study", "Study_Icon.png");
        register(icons, "shopping", "Shopping_Icon.png");
        register(icons, "grocery", "Shopping_Icon.png");
        register(icons, "personal", "Personal_Icon.png");
        register(icons, "home", "Home_Icon.png");
        register(icons, "house", "Home_Icon.png");
        register(icons, "healthy", "Healthy_Icon.png");
        register(icons, "health", "Healthy_Icon.png");
        register(icons, "fitness", "Fitness_Icon.png");
        register(icons, "workout", "Fitness_Icon.png");
        register(icons, "exercise", "Fitness_Icon.png");
        return icons;
    }

    private static void register(Map<String, Image> icons, String key, String fileName) {
        Image image = loadImage(fileName);
        if (image != null) {
            icons.put(key, image);
        }
    }

    private static Image loadImage(String fileName) {
        String path = "/images/" + fileName;
        var resource = CategoryIconProvider.class.getResource(path);
        if (resource == null) {
            return null;
        }
        return new Image(resource.toExternalForm());
    }

    /**
     * Returns the icon associated with the provided category, or {@code null} when no icon exists.
     *
     * @param category category label entered for a task
     * @return an {@link Image} for the category or {@code null} if none is configured
     */
    public static Image getIconForCategory(String category) {
        if (category == null) {
            return null;
        }
        String normalized = normalize(category);
        if (normalized.isEmpty()) {
            return null;
        }
        Image icon = ICONS.get(normalized);
        if (icon == null && normalized.endsWith("s")) {
            icon = ICONS.get(normalized.substring(0, normalized.length() - 1));
        }
        return icon;
    }

    private static String normalize(String category) {
        return category.trim().toLowerCase(Locale.ENGLISH).replaceAll("[^a-z0-9]", "");
    }
}