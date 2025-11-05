package com.example.smarttodoapp;

import com.example.smarttodoapp.data.TaskStore;
import com.example.smarttodoapp.model.Task;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

public class TaskFormController {

    @FXML
    private TextField taskNameTextField;
    @FXML
    private TextArea taskDescriptionTextField;
    @FXML
    private ComboBox<String> taskCategoryComboBox;
    @FXML
    private ComboBox<Integer> taskPriorityComboBox;
    @FXML
    private DatePicker taskDueDateDatePicker;
    @FXML
    private Button taskSaveButton;
    @FXML
    private Button taskCancelButton;
    @FXML
    private Label formTitleLabel;

    private ObservableList<Task> tasks;
    private Task taskToEdit;

    public void setTasks(ObservableList<Task> tasks) {
        this.tasks = tasks;
    }

    public void setTaskToEdit(Task task) {
        this.taskToEdit = task;
        if (task == null) {
            return;
        }

        formTitleLabel.setText("Edit Task");
        taskSaveButton.setText("Update");

        taskNameTextField.setText(task.getName());
        taskDescriptionTextField.setText(task.getDescription() == null ? "" : task.getDescription());

        if (task.getCategory() != null && !task.getCategory().isBlank()
                && !taskCategoryComboBox.getItems().contains(task.getCategory())) {
            taskCategoryComboBox.getItems().add(task.getCategory());
        }
        taskCategoryComboBox.setValue(task.getCategory());

        if (task.getPriority() != null
                && !taskPriorityComboBox.getItems().contains(task.getPriority())) {
            taskPriorityComboBox.getItems().add(task.getPriority());
        }
        taskPriorityComboBox.setValue(task.getPriority());

        taskDueDateDatePicker.setValue(task.getDueDate());
    }

    @FXML
    public void initialize() {
        taskPriorityComboBox.getItems().setAll(1,2,3,4);
        taskCategoryComboBox.getItems().setAll("Study", "Work", "Shopping", "Fitness", "Personal", "Healthy", "Home");
    }

    @FXML
    public void save(ActionEvent event) {

        if (tasks == null) {
            new Alert(Alert.AlertType.ERROR, "Task list is not available. Please reopen the form from the main window.").showAndWait();
            return;
        }

        String taskName = taskNameTextField.getText();
        if (taskName == null || taskName.isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Please enter a task name.").showAndWait();
            return;
        }

        String taskCategory = taskCategoryComboBox.getValue();
        Integer taskPriority = taskPriorityComboBox.getValue();
        LocalDate taskDueDate = taskDueDateDatePicker.getValue();

        if (taskCategory == null || taskPriority == null || taskDueDate == null) {
            new Alert(Alert.AlertType.WARNING, "Please complete all fields before saving.").showAndWait();
            return;
        }

        if (taskToEdit != null) {
            taskToEdit.setName(taskName);
            taskToEdit.setDescription(taskDescriptionTextField.getText());
            taskToEdit.setCategory(taskCategory);
            taskToEdit.setPriority(taskPriority);
            taskToEdit.setDueDate(taskDueDate);
        } else {
            Task task = new Task(taskName, taskDescriptionTextField.getText(), taskCategory, taskPriority, taskDueDate, false);
            tasks.add(task);
        }

        TaskStore.save(tasks);

        closeWindow();

    }

    @FXML
    private void cancel(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) taskSaveButton.getScene().getWindow();
        stage.close();
    }
}
