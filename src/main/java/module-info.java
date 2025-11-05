module com.example.smarttodoapp {
    requires javafx.controls;
    requires javafx.fxml;

    // Jackson
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.datatype.jsr310;

    // JavaFX FXML access to controllers
    opens com.example.smarttodoapp to javafx.fxml;

    // Allow Jackson (and JavaFX table bindings) to reflect on your model
    opens com.example.smarttodoapp.model to com.fasterxml.jackson.databind, javafx.base;

    // (Optional) if you ever serialize things in data package
    opens com.example.smarttodoapp.data to com.fasterxml.jackson.databind;

    // So the JVM can see your Launcher/MainApplication
    exports com.example.smarttodoapp;
}
