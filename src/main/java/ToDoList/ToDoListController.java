package ToDoList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ToDoListController {

    private static Map<TaskPriority, Color> priorityColors;

    static {
        priorityColors = new HashMap<>();
        priorityColors.put(TaskPriority.LOWEST, Color.GREEN);
        priorityColors.put(TaskPriority.AVERAGE, Color.ORANGE);
        priorityColors.put(TaskPriority.HIGHEST, Color.RED);
    }


    @FXML
    private TreeView<ToDoTask> taskTreeView;

    @FXML
    private TextField taskDescriptionTextField;
    @FXML
    private ComboBox<TaskPriority> taskPriorityComboBox;
    @FXML
    private TextArea additionalInfoTextArea;
    @FXML
    private TextArea infoDisplayArea;
    @FXML
    private Button clearButton;
    @FXML
    private Button addTaskButton;
    @FXML
    private TabPane tabpane;

    public void initialize() {
        this.setTreeViewProperties();
        this.fillPriorityCombobox();
        this.setButtonDelegates();
        this.loadAndSetTreeValues();
    }

    private void setTreeViewProperties() {
        //set cell factory to checkbox
        this.taskTreeView.setCellFactory(p -> new CheckBoxTreeCell<ToDoTask>() {
            @Override
            public void updateItem(ToDoTask item, boolean empty) {
                super.updateItem(item, empty);
                if (!isEmpty()) {
                    this.setText(this.getItem().getTaskDescription());
                    this.setTextFill(priorityColors.get(this.getItem().getPriority()));
                }
            }
        });
        this.taskTreeView.setShowRoot(false);
        this.taskTreeView.setRoot(new CheckBoxTreeItem<>(null));
        this.taskTreeView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                TreeItem<ToDoTask> _task = this.taskTreeView.getSelectionModel().getSelectedItem();
                if (_task != null) {
                    ToDoTask task = _task.getValue();
                    StringBuilder sb = new StringBuilder();
                    sb.append(task.getTaskDescription()).append("\n");
                    task.getCompletionDate().ifPresent(datetime -> {
                        sb.append("Completed on ");
                        sb.append(datetime.toString());
                        sb.append("\n");
                    });
                    sb.append("Additional information: ");
                    sb.append(task.getAdditionalInfo().isEmpty() ? "None" : task.getAdditionalInfo());
                    this.infoDisplayArea.setText(sb.toString());
                    this.tabpane.getSelectionModel().select(1);
                }
            }
        });
    }

    private void setButtonDelegates() {
        this.clearButton.setOnAction(event -> {
            this.clearInputs();
        });
        this.addTaskButton.setOnAction(event -> this.addTask());
    }

    private void clearInputs() {
        this.taskPriorityComboBox.getSelectionModel().select(0);
        this.taskDescriptionTextField.setText("");
        this.additionalInfoTextArea.setText("");
        this.taskTreeView.getSelectionModel().clearSelection();
    }

    private void loadAndSetTreeValues() {
        try {
            List<ToDoTask> tasks = this.loadTasksFromFile();
            Platform.runLater(() -> {
                this.setUpTree(this.taskTreeView.getRoot(), tasks);
            });
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Could not find tasks.json file.").showAndWait();
        } catch (ConcurrentModificationException ignored) { }
    }

    private void addTask() {
        String taskDescription = this.taskDescriptionTextField.getText();
        if (taskDescription.trim().isEmpty()) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please input a valid task");
                alert.showAndWait();
            });
            return;
        }
        String additionalInfo = this.additionalInfoTextArea.getText();
        TaskPriority priority = this.taskPriorityComboBox.getSelectionModel().getSelectedItem();
        ToDoTask task = new ToDoTask();
        task.setPriority(priority);
        task.setTaskDescription(taskDescription);
        task.setAdditionalInfo(additionalInfo);
        CheckBoxTreeItem<ToDoTask> taskCheckBox = new CheckBoxTreeItem<>(task);
        taskCheckBox.setIndependent(true);
        taskCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                if (task.getCompletionDate().isPresent()) return;
                task.setCompletionDate(Optional.of(LocalDateTime.now()));
            }
        });
        TreeItem<ToDoTask> item = this.taskTreeView.getSelectionModel().getSelectedItem();
        Comparator<TreeItem<ToDoTask>> treeSortComparator = Comparator.
                comparing((TreeItem<ToDoTask> treeitem) -> treeitem.getValue().getPriority()).reversed();
        if (item != null) {
            item.getChildren().add(taskCheckBox);
            ToDoTask toDoTask = item.getValue();
            toDoTask.addRequirement(task);
            item.setExpanded(true);
            item.getChildren().sort(treeSortComparator);
        } else {
            TreeItem<ToDoTask> root = this.taskTreeView.getRoot();
            root.getChildren().add(taskCheckBox);
            root.getChildren().sort(treeSortComparator);
        }
        this.clearInputs();
    }

    private void fillPriorityCombobox() {
        this.taskPriorityComboBox.setCellFactory(p -> new ListCell<TaskPriority>() {
            @Override
            protected void updateItem(TaskPriority item, boolean empty) {
                super.updateItem(item, empty);
                if (!isEmpty()) {
                    final String name = item.toString().toLowerCase();
                    final String finalName = Character.toUpperCase(name.charAt(0)) + name.substring(1);
                    setText(finalName);
                }
            }
        });
        this.taskPriorityComboBox.getItems().clear();
        this.taskPriorityComboBox.getItems().addAll(TaskPriority.values());
        this.taskPriorityComboBox.getSelectionModel().select(0);
    }

    private void setUpTree(TreeItem<ToDoTask> root, List<ToDoTask> tasks) {
        tasks.forEach(task -> {
            CheckBoxTreeItem<ToDoTask> item = new CheckBoxTreeItem<>(task);
            root.getChildren().add(item);
            item.setIndependent(true);
            if (task.getCompletionDate().isPresent()) {
                item.setSelected(true);
            }
            item.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    if (task.getCompletionDate().isPresent()) return;
                    task.setCompletionDate(Optional.of(LocalDateTime.now()));
                }
            });
            setUpTree(item, task.getRequirements());
        });
    }

    private List<ToDoTask> loadTasksFromFile() throws IOException {
        String path = System.getProperty("user.dir") + File.separator + "Data/tasks.json";
        List<String> lines = Files.readAllLines(Paths.get(path));
        String json = String.join("\n", lines);
        Type t = new TypeToken<List<ToDoTask>>() {}.getType();
        return new Gson().fromJson(json, t);
    }

    private List<ToDoTask> collectTasks() {
        return this.taskTreeView.
                getRoot().
                getChildren().
                stream().
                map(TreeItem::getValue).
                collect(Collectors.toList());
    }

    private void saveTasksToFile() throws IOException {
        String path = System.getProperty("user.dir") + File.separator + "Data/tasks.json";
        Gson g = new GsonBuilder().setPrettyPrinting().create();
        List<ToDoTask> tasks = this.collectTasks();
        String json = g.toJson(tasks);
        Files.write(Paths.get(path), json.getBytes());
    }
}
