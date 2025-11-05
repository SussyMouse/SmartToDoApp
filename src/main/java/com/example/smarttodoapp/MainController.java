package com.example.smarttodoapp;

import com.example.smarttodoapp.data.TaskStore;
import com.example.smarttodoapp.model.Task;
import com.example.smarttodoapp.model.TaskListCell;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    @FXML
    private ListView<Task> taskListView;

    private ObservableList<Task> tasks;

    @FXML
    public void initialize() {
        tasks = TaskStore.load();
        taskListView.setItems(tasks);
        taskListView.setCellFactory(list -> new TaskListCell(tasks));
    }

    @FXML
    public void switchToTaskFormScene(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TaskFormDialog.fxml"));
        Parent dialogRoot = loader.load();
        TaskFormController controller = loader.getController();
        controller.setTasks(tasks);

        Stage owner = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Add Task");
        dialog.setScene(new Scene(dialogRoot));
        dialog.showAndWait();

        taskListView.refresh();
    }

//    public void switchToTaskInfoScene(ActionEvent event) throws IOException {
//        root = FXMLLoader.load(getClass().getResource("TaskInfoDialog.fxml"));
//        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
//        scene = new Scene(root);
//        stage.setScene(scene);
//        stage.show();
//    }

}
