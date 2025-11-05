package com.example.smarttodoapp.model;

import com.example.smarttodoapp.model.CategoryIconProvider;
import com.example.smarttodoapp.TaskFormController;
import com.example.smarttodoapp.TaskInfoController;
import com.example.smarttodoapp.data.TaskStore;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.io.IOException;

public class TaskListCell extends ListCell<Task> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private final ObservableList<Task> tasks;

    private final HBox root = new HBox(20);
    private final Button completeButton = new Button("Mark as Completed");
    private final HBox card = new HBox(12);
    private final VBox textBox = new VBox(4);
    private final Label titleLabel = new Label();
    private final Label metaLabel = new Label();
    private final Region spacer = new Region();
    private final HBox actionBox = new HBox(6);
    private final ImageView categoryIconView = new ImageView();
    private static final Image INFO_ICON = new Image(
            TaskListCell.class.getResource("/images/info.png").toExternalForm());
    private static final Image EDIT_ICON = new Image(
            TaskListCell.class.getResource("/images/edit.png").toExternalForm());
    private static final Image DELETE_ICON = new Image(
            TaskListCell.class.getResource("/images/delete.png").toExternalForm());

    private final Button infoButton = new Button();
    private final Button editButton = new Button();
    private final Button deleteButton = new Button();

    public TaskListCell(ObservableList<Task> tasks) {
        this.tasks = tasks;

        getStyleClass().add("task-list-cell");

        root.getStyleClass().add("task-cell");
        root.setAlignment(Pos.CENTER_LEFT);
        root.setPadding(new Insets(0, 16, 0, 0));

        completeButton.getStyleClass().add("mark-complete-button");
        completeButton.setFocusTraversable(false);

        card.getStyleClass().add("task-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(14, 24, 14, 24));

        textBox.getChildren().addAll(titleLabel, metaLabel);
        textBox.getStyleClass().add("task-card-text");

        titleLabel.getStyleClass().add("task-title");
        metaLabel.getStyleClass().add("task-meta");

        actionBox.getStyleClass().add("task-actions");
        actionBox.setAlignment(Pos.CENTER);

        infoButton.getStyleClass().add("task-action-button");
        editButton.getStyleClass().add("task-action-button");
        deleteButton.getStyleClass().addAll("task-action-button", "task-delete-button");

        categoryIconView.setFitWidth(32);
        categoryIconView.setFitHeight(32);
        categoryIconView.setPreserveRatio(true);
        categoryIconView.setSmooth(true);
        categoryIconView.setVisible(false);
        categoryIconView.setManaged(false);

        infoButton.setGraphic(createIconView(INFO_ICON));
        editButton.setGraphic(createIconView(EDIT_ICON));
        deleteButton.setGraphic(createIconView(DELETE_ICON));

        infoButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        editButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        deleteButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        infoButton.setFocusTraversable(false);
        editButton.setFocusTraversable(false);
        deleteButton.setFocusTraversable(false);

        infoButton.setTooltip(new Tooltip("Task details"));
        editButton.setTooltip(new Tooltip("Edit task"));
        deleteButton.setTooltip(new Tooltip("Delete task"));

        actionBox.getChildren().addAll(categoryIconView, infoButton, editButton, deleteButton);

        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox.setHgrow(card, Priority.ALWAYS);
        card.getChildren().addAll(textBox, spacer, actionBox);

        root.getChildren().addAll(completeButton, card);

        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        completeButton.setOnAction(e -> {
            Task item = getItem();
            if (item == null) {
                return;
            }
            item.setCompleted(!item.isCompleted());
            updateCompletionState(item);
            TaskStore.save(tasks);
            // force list to refresh to update style classes in sibling cells if needed
            if (getListView() != null) {
                getListView().refresh();
            }
        });

        deleteButton.setOnAction(e -> {
            Task item = getItem();
            if (item == null) {
                return;
            }
            tasks.remove(item);
            TaskStore.save(tasks);
        });

        infoButton.setOnAction(e -> {
            Task item = getItem();
            if (item == null) {
                return;
            }
            openTaskInfoDialog(item);
        });

        editButton.setOnAction(e -> {
            Task item = getItem();
            if (item == null) {
                return;
            }
            openTaskFormDialog(item);
        });
    }

    @Override
    protected void updateItem(Task task, boolean empty) {
        super.updateItem(task, empty);
        if (empty || task == null) {
            setText(null);
            setGraphic(null);
            return;
        }

        titleLabel.setText(task.getName());

        String due = task.getDueDate() != null ? DATE_FORMATTER.format(task.getDueDate()) : "No due date";
        String category = Optional.ofNullable(task.getCategory()).filter(s -> !s.isBlank()).orElse("No category");
        metaLabel.setText(String.format("Due Date: %s, Category: %s", due, category));

        updatePriorityStyle(task);
        updateCompletionState(task);
        updateCategoryIcon(task);

        setGraphic(root);
    }

    private void updatePriorityStyle(Task task) {
        card.getStyleClass().setAll("task-card");
        if (task.getPriority() != null) {
            card.getStyleClass().add("priority-" + task.getPriority());
        }
        if (task.isCompleted()) {
            card.getStyleClass().add("completed");
        }
    }

    private void updateCompletionState(Task task) {
        boolean overdue = isOverdue(task);

        completeButton.getStyleClass().removeAll("completed", "overdue");
        card.getStyleClass().remove("completed");

        if (task.isCompleted()) {
            completeButton.setText("Completed");
            completeButton.setDisable(false);
            completeButton.getStyleClass().add("completed");
            card.getStyleClass().add("completed");
        } else if (overdue) {
            completeButton.setText("Overdue");
            completeButton.setDisable(true);
            completeButton.getStyleClass().add("overdue");
        } else {
            completeButton.setText("Mark as Completed");
            completeButton.setDisable(false);
        }
    }

    private boolean isOverdue(Task task) {
        LocalDate dueDate = task.getDueDate();
        if (dueDate == null) {
            return false;
        }
        return !task.isCompleted() && dueDate.isBefore(LocalDate.now());
    }

    private void updateCategoryIcon(Task task) {
        Image icon = CategoryIconProvider.getIconForCategory(task.getCategory());
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

    private ImageView createIconView(Image image) {
        ImageView view = new ImageView(image);
        view.setFitWidth(24);
        view.setFitHeight(24);
        view.setPreserveRatio(true);
        return view;
    }

    private void openTaskInfoDialog(Task task) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/smarttodoapp/TaskInfoDialog.fxml"));
            Parent root = loader.load();
            TaskInfoController controller = loader.getController();

            Stage dialog = createDialogStage(root, "Task Details");
            controller.setDialogStage(dialog);
            controller.setTasks(tasks);
            controller.setTask(task);
            controller.setOnTaskUpdated(() -> {
                if (getListView() != null) {
                    getListView().refresh();
                } else {
                    updatePriorityStyle(task);
                    updateCompletionState(task);
                }
            });

            dialog.showAndWait();
        } catch (IOException ex) {
            showError("Unable to display task details.", ex);
        }
    }

    private void openTaskFormDialog(Task task) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/smarttodoapp/TaskFormDialog.fxml"));
            Parent root = loader.load();
            TaskFormController controller = loader.getController();
            controller.setTasks(tasks);
            controller.setTaskToEdit(task);

            Stage dialog = createDialogStage(root, "Edit Task");
            dialog.showAndWait();

            if (getListView() != null) {
                getListView().refresh();
            }
        } catch (IOException ex) {
            showError("Unable to open the task form.", ex);
        }
    }

    private Stage createDialogStage(Parent root, String title) {
        Stage dialog = new Stage();
        Stage owner = getOwnerStage();
        if (owner != null) {
            dialog.initOwner(owner);
        }
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(title);
        dialog.setScene(new Scene(root));
        return dialog;
    }

    private Stage getOwnerStage() {
        if (getListView() != null && getListView().getScene() != null) {
            return (Stage) getListView().getScene().getWindow();
        }
        return null;
    }

    private void showError(String message, Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.setContentText(ex.getMessage());
        alert.showAndWait();
    }
}
