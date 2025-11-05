package com.example.smarttodoapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("MainWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 800);
        scene.getStylesheets().add(Objects.requireNonNull(MainApplication.class.getResource("styles.css")).toExternalForm());
        stage.setTitle("Smart To Do Application");
        try {
            InputStream iconStream = getClass().getResourceAsStream("/images/icon.png");

            if (iconStream != null) {
                Image icon = new Image(iconStream);
                stage.getIcons().add(icon);
            } else {
                System.err.println("Application icon not found in resources.");
            }
        } catch (Exception e) {
            System.err.println("Failed to load application icon: " + e.getMessage());
        }
        stage.setScene(scene);
        stage.show();
    }
}
