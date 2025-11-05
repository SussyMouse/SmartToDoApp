package com.example.smarttodoapp;

import com.example.smarttodoapp.model.CategoryIconProvider;
import com.example.smarttodoapp.data.TaskStore;
import com.example.smarttodoapp.model.Task;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class TaskInfoController {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    @FXML
    private Label taskNameLabel;
    @FXML
    private Label taskDescriptionLabel;
    @FXML
    private Label taskCategoryLabel;
    @FXML
    private ImageView categoryIconView;
    @FXML
    private Label taskDueDateLabel;
    @FXML
    private Label taskPriorityLabel;
    @FXML
    private Label taskStatusLabel;
    @FXML
    private Button markCompleteButton;

    private ObservableList<Task> tasks;
    private Task task;
    private Stage dialogStage;
    private Runnable onTaskUpdated;

    public void setTasks(ObservableList<Task> tasks) {
        this.tasks = tasks;
    }

    public void setTask(Task task) {
        this.task = task;
        refreshTaskDetails();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setOnTaskUpdated(Runnable onTaskUpdated) {
        this.onTaskUpdated = onTaskUpdated;
    }

    @FXML
    public void editTask() {
        if (task == null || tasks == null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TaskFormDialog.fxml"));
            Parent root = loader.load();
            TaskFormController controller = loader.getController();
            controller.setTasks(tasks);
            controller.setTaskToEdit(task);

            Stage editStage = new Stage();
            if (dialogStage != null) {
                editStage.initOwner(dialogStage);
            }
            editStage.initModality(Modality.APPLICATION_MODAL);
            editStage.setTitle("Edit Task");
            editStage.setScene(new Scene(root));
            editStage.showAndWait();

            refreshTaskDetails();
            notifyTaskUpdated();
        } catch (IOException ex) {
            showError("Unable to open the edit form.", ex);
        }
    }

    @FXML
    public void toggleCompleted() {
        if (task == null || tasks == null) {
            return;
        }

        if (isOverdue(task)) {
            return;
        }

        task.setCompleted(!task.isCompleted());
        TaskStore.save(tasks);
        refreshTaskDetails();
        notifyTaskUpdated();
    }

    @FXML
    public void close() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    private void refreshTaskDetails() {
        if (task == null) {
            return;
        }

        taskNameLabel.setText(Optional.ofNullable(task.getName()).orElse("Unnamed Task"));
        taskDescriptionLabel.setText(Optional.ofNullable(task.getDescription()).filter(s -> !s.isBlank())
                .orElse("No additional description."));
        taskCategoryLabel.setText(Optional.ofNullable(task.getCategory()).filter(s -> !s.isBlank())
                .orElse("No category"));
        updateCategoryIcon(task.getCategory());
        taskPriorityLabel.setText(Optional.ofNullable(task.getPriority()).map(Object::toString)
                .orElse("No priority"));
        taskDueDateLabel.setText(task.getDueDate() != null ? DATE_FORMATTER.format(task.getDueDate()) : "No due date");
        taskStatusLabel.setText(task.isCompleted() ? "Completed" : "Active");

        boolean overdue = isOverdue(task);

        if (overdue) {
            markCompleteButton.setVisible(false);
            markCompleteButton.setManaged(false);
        } else {
            markCompleteButton.setVisible(true);
            markCompleteButton.setManaged(true);
            markCompleteButton.setText(task.isCompleted() ? "Mark as Incomplete" : "Mark as Completed");
        }
    }

    private void updateCategoryIcon(String category) {
        Image icon = CategoryIconProvider.getIconForCategory(category);
        if (categoryIconView != null) {
            if (icon != null) {
                categoryIconView.setImage(icon);
                categoryIconView.setVisible(true);
                categoryIconView.setManaged(true);
            } else {
                categoryIconView.setImage(null);
                categoryIconView.setVisible(false);
                categoryIconView.setManaged(false);
            }
        }
    }

    private void notifyTaskUpdated() {
        if (onTaskUpdated != null) {
            onTaskUpdated.run();
        }
    }

    private boolean isOverdue(Task task) {
        LocalDate dueDate = task.getDueDate();
        if (dueDate == null) {
            return false;
        }
        return !task.isCompleted() && dueDate.isBefore(LocalDate.now());
    }

    private void showError(String message, Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.setContentText(ex.getMessage());
        alert.showAndWait();
    }
}