package com.example.smarttodoapp;

import com.example.smarttodoapp.data.TaskStore;
import com.example.smarttodoapp.model.Task;
import com.example.smarttodoapp.model.TaskListCell;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 * Controller for the main task view that wires filtering, searching, and list updates.
 *
 * References:
 *   - JavaFX API for live list filtering:
 *   "<a href="https://openjfx.io/javadoc/21/javafx.base/javafx/collections/transformation/FilteredList.html">...</a>"
 *   - Oracle ListView cell factory guidance used as a basis for configuring:
 *   "<a href="https://docs.oracle.com/javase/8/javafx/user-interface-tutorial/list-view.htm">...</a>"
 */

public class MainController {
    @FXML
    private ListView<Task> taskListView;

    @FXML
    private ComboBox<String> categoryFilter;

    @FXML
    private ComboBox<String> completionFilter;

    @FXML
    private ComboBox<String> priorityFilter;

    @FXML
    private DatePicker dateFilter;

    @FXML
    private TextField searchField;

    private ObservableList<Task> tasks;
    private FilteredList<Task> filteredTasks;

    @FXML
    public void initialize() {
        tasks = TaskStore.load();
        filteredTasks = new FilteredList<>(tasks, task -> true);
        taskListView.setItems(filteredTasks);
        taskListView.setCellFactory(list -> new TaskListCell(tasks));

        initializeFilters();
        applyFilters();

        tasks.addListener((ListChangeListener<Task>) change -> {
            updateCategoryOptions();
            updatePriorityOptions();
            applyFilters();
        });

        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        }
    }

    @FXML
    public void switchToTaskFormScene(ActionEvent event) throws IOException {
        Stage owner = (Stage) ((Node) event.getSource()).getScene().getWindow();
        showTaskFormDialog(owner);
    }

    @FXML
    public void handleAddTaskMenuAction() throws IOException {
        showTaskFormDialog(getCurrentStage());
    }

    @FXML
    public void clearCompletedTasks() {
        if (tasks == null || tasks.isEmpty()) {
            return;
        }

        boolean removed = tasks.removeIf(Task::isCompleted);
        if (removed) {
            TaskStore.save(tasks);
            refreshTaskView();
        }
    }

    @FXML
    public void clearOverdueTasks() {
        if (tasks == null || tasks.isEmpty()) {
            return;
        }

        LocalDate today = LocalDate.now();
        boolean removed = tasks.removeIf(task -> task.getDueDate() != null && task.getDueDate().isBefore(today));
        if (removed) {
            TaskStore.save(tasks);
            refreshTaskView();
        }
    }

    @FXML
    public void handleExitApplication() {
        Platform.exit();
    }

    @FXML
    public void showAboutDialog() {
        Stage owner = getCurrentStage();

        Stage dialog = new Stage();
        if (owner != null) {
            dialog.initOwner(owner);
            dialog.initModality(Modality.APPLICATION_MODAL);
        }

        dialog.setTitle("About Smart ToDo");

        Label heading = new Label("Need help? We're here.");
        heading.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #f2f2f2;");

        Label message = new Label(
                "If there are any problems, please reach our technical team.\n" +
                        "Email: SmarttodoTechnical@gmail.com\n" +
                        "Contact: +60 16 - 457 3769"
        );
        message.setWrapText(true);
        message.setStyle("-fx-text-fill: #e0e0e0; -fx-font-size: 14px;");

        Button closeButton = new Button("Close");
        closeButton.setStyle("-fx-background-color: linear-gradient(to right, #a6a6a6, #ffffff); " +
                "-fx-text-fill: #000000; -fx-font-size: 14px; -fx-background-radius: 20; -fx-padding: 8 16;");
        closeButton.setOnAction(event -> dialog.close());

        VBox layout = new VBox(12, heading, message, closeButton);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: linear-gradient(to right, #0d0d0d, #3d3d3d);");

        Scene scene = new Scene(layout, 460, 220);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    @FXML
    public void applyFilters() {
        updateFilteredList();
    }

    private void initializeFilters() {
        if (completionFilter != null && completionFilter.getItems().isEmpty()) {
            completionFilter.getItems().addAll("All Tasks", "Completed", "Not Completed");
            completionFilter.getSelectionModel().selectFirst();
        }

        updateCategoryOptions();
        updatePriorityOptions();

        if (dateFilter != null) {
            dateFilter.setValue(null);
        }
    }

    private void updateCategoryOptions() {
        if (categoryFilter == null) {
            return;
        }

        String previousSelection = categoryFilter.getValue();
        Set<String> categories = new LinkedHashSet<>();
        for (Task task : tasks) {
            String category = task.getCategory();
            if (category != null && !category.isBlank()) {
                categories.add(category);
            }
        }

        categoryFilter.getItems().setAll("All Categories");
        categoryFilter.getItems().addAll(categories);

        if (previousSelection != null && categoryFilter.getItems().contains(previousSelection)) {
            categoryFilter.setValue(previousSelection);
        } else {
            categoryFilter.getSelectionModel().selectFirst();
        }
    }

    private void updatePriorityOptions() {
        if (priorityFilter == null) {
            return;
        }

        String previousSelection = priorityFilter.getValue();
        Set<String> priorities = new LinkedHashSet<>();
        for (Task task : tasks) {
            Integer priority = task.getPriority();
            if (priority != null) {
                priorities.add(priority.toString());
            }
        }

        priorityFilter.getItems().setAll("All Priorities");
        priorityFilter.getItems().addAll(priorities);

        if (previousSelection != null && priorityFilter.getItems().contains(previousSelection)) {
            priorityFilter.setValue(previousSelection);
        } else {
            priorityFilter.getSelectionModel().selectFirst();
        }
    }

    private void updateFilteredList() {
        if (filteredTasks == null) {
            return;
        }

        String selectedCategory = categoryFilter != null ? categoryFilter.getValue() : null;
        String selectedPriority = priorityFilter != null ? priorityFilter.getValue() : null;
        String completionSelection = completionFilter != null ? completionFilter.getValue() : null;
        LocalDate selectedDate = dateFilter != null ? dateFilter.getValue() : null;
        String keyword = searchField != null ? searchField.getText() : null;

        filteredTasks.setPredicate(task -> {
            if (task == null) {
                return false;
            }

            if (selectedCategory != null && !selectedCategory.equals("All Categories")) {
                String taskCategory = task.getCategory();
                if (taskCategory == null || !taskCategory.equalsIgnoreCase(selectedCategory)) {
                    return false;
                }
            }

            if (selectedPriority != null && !selectedPriority.equals("All Priorities")) {
                Integer taskPriority = task.getPriority();
                if (taskPriority == null || !selectedPriority.equals(taskPriority.toString())) {
                    return false;
                }
            }

            if (completionSelection != null) {
                if (completionSelection.equals("Completed") && !task.isCompleted()) {
                    return false;
                }
                if (completionSelection.equals("Not Completed") && task.isCompleted()) {
                    return false;
                }
            }

            if (selectedDate != null) {
                LocalDate dueDate = task.getDueDate();
                if (dueDate == null || !dueDate.isEqual(selectedDate)) {
                    return false;
                }
            }

            if (keyword != null && !keyword.isBlank()) {
                String lowerKeyword = keyword.toLowerCase();
                boolean matches = false;

                if (!matches && task.getName() != null && task.getName().toLowerCase().contains(lowerKeyword)) {
                    matches = true;
                }
                if (!matches && task.getDescription() != null && task.getDescription().toLowerCase().contains(lowerKeyword)) {
                    matches = true;
                }
                if (!matches && task.getCategory() != null && task.getCategory().toLowerCase().contains(lowerKeyword)) {
                    matches = true;
                }
                if (!matches && task.getPriority() != null && task.getPriority().toString().contains(lowerKeyword)) {
                    matches = true;
                }
                if (!matches && task.getDueDate() != null && task.getDueDate().toString().contains(lowerKeyword)) {
                    matches = true;
                }

                if (!matches) {
                    return false;
                }
            }

            return true;
        });
    }

    private void showTaskFormDialog(Stage owner) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TaskFormDialog.fxml"));
        Parent dialogRoot = loader.load();
        TaskFormController controller = loader.getController();
        controller.setTasks(tasks);

        Stage dialog = new Stage();
        if (owner != null) {
            dialog.initOwner(owner);
            dialog.initModality(Modality.APPLICATION_MODAL);
        }
        dialog.setTitle("Add Task");
        Scene scene = new Scene(dialogRoot);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        dialog.setScene(scene);
        dialog.showAndWait();

        refreshTaskView();
    }

    private void refreshTaskView() {
        taskListView.refresh();
        updateCategoryOptions();
        updatePriorityOptions();
        applyFilters();
    }

    private Stage getCurrentStage() {
        if (taskListView != null && taskListView.getScene() != null) {
            return (Stage) taskListView.getScene().getWindow();
        }
        return null;
    }
}
