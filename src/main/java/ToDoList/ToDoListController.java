package ToDoList;

import Saving.Saveable;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ToDoListController implements Saveable {

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
    private TreeView<ToDoTask> finishedTasksTreeView;

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
        this.setTaskTreeViewProperties();
        this.setFinishedTasksViewProperties();
        this.fillPriorityCombobox();
        this.setButtonDelegates();
        try {
            this.loadTasksFromFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setTaskTreeViewProperties() {
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

    private void setFinishedTasksViewProperties() {
        this.finishedTasksTreeView.setRoot(new TreeItem<>());
        this.finishedTasksTreeView.setShowRoot(false);
        this.finishedTasksTreeView.setCellFactory(p -> new TreeCell<ToDoTask>() {
            @Override
            public void updateItem(ToDoTask item, boolean empty) {
                super.updateItem(item, empty);
                if (!isEmpty()) {
                    this.setText(this.getItem().getTaskDescription());
                    this.setTextFill(priorityColors.get(this.getItem().getPriority()));
                }
            }
        });
    }

    private void setButtonDelegates() {
        this.clearButton.setOnAction(event -> this.clearInputs());
        this.addTaskButton.setOnAction(event -> this.addTask());
    }

    private void clearInputs() {
        this.taskPriorityComboBox.getSelectionModel().select(0);
        this.taskDescriptionTextField.setText("");
        this.additionalInfoTextArea.setText("");
        this.taskTreeView.getSelectionModel().clearSelection();
    }

    private void addTask() {
        String taskDescription = this.taskDescriptionTextField.getText();
        //if user had input only white spaces or hasnt input anything
        if (taskDescription.trim().isEmpty()) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please input a valid task");
                alert.showAndWait();
            });
            return;
        }
        String additionalInfo = this.additionalInfoTextArea.getText();
        TaskPriority priority = this.taskPriorityComboBox.getSelectionModel().getSelectedItem();

        //if there already is that task in the tree
        if (this.taskTreeView.getRoot().getChildren().stream().anyMatch(i -> i.getValue().getTaskDescription().equals(taskDescription))) {
            new Alert(Alert.AlertType.WARNING, String.format("Task \"%s\" already exists.", taskDescription)).showAndWait();
            return;
        }

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
                //if root is this item's parent
                if (taskCheckBox.getParent() == this.taskTreeView.getRoot()) {
                    TreeItem<ToDoTask> taskTreeItem = new TreeItem<>(taskCheckBox.getValue());
                    this.fillChildren(taskCheckBox, taskTreeItem);
                    this.finishedTasksTreeView.getRoot().getChildren().add(taskTreeItem);
                    this.taskTreeView.getRoot().getChildren().remove(taskCheckBox);
                }
            }
        });

        TreeItem<ToDoTask> item = this.taskTreeView.getSelectionModel().getSelectedItem();
        //sorting criteria, first compare using priority, then by using the task description
        Comparator<TreeItem<ToDoTask>> treeSortComparator = Comparator.
                comparing((TreeItem<ToDoTask> treeitem) -> treeitem.getValue().getPriority()).
                reversed().
                thenComparing((TreeItem<ToDoTask> treeitem) -> treeitem.getValue().getTaskDescription());

        if (item != null) { //means we have something selected
            item.getValue().addRequirement(task);
            item.getChildren().add(taskCheckBox);
            item.setExpanded(true);
            item.getChildren().sort(treeSortComparator);
        } else { //if it is null then add to root
            TreeItem<ToDoTask> root = this.taskTreeView.getRoot();
            root.getChildren().add(taskCheckBox);
            root.getChildren().sort(treeSortComparator);
        }

        this.clearInputs();//clear user inputs, together with selection
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
                    //if root is this item's parent
                    if (item.getParent() == this.taskTreeView.getRoot()) {
                        TreeItem<ToDoTask> taskTreeItem = new TreeItem<>(item.getValue());
                        this.fillChildren(item, taskTreeItem);
                        this.finishedTasksTreeView.getRoot().getChildren().add(taskTreeItem);
                        this.taskTreeView.getRoot().getChildren().remove(item);
                    }
                }
            });
            setUpTree(item, task.getRequirements());
        });
    }

    private void fillChildren(TreeItem<ToDoTask> source, TreeItem<ToDoTask> destination) {
        source.getChildren().forEach(treeitem -> {
            TreeItem<ToDoTask> item = new TreeItem<>(treeitem.getValue());
            destination.getChildren().add(item);
            fillChildren(treeitem, item);
        });
    }

    private void setUpFinishedTasksTree(TreeItem<ToDoTask> root, List<ToDoTask> tasks) {
        tasks.forEach(task -> {
            TreeItem<ToDoTask> item = new TreeItem<>(task);
            root.getChildren().add(item);
            setUpFinishedTasksTree(item, task.getRequirements());
        });
    }

    private void loadTasksFromFile() throws IOException {
        String path = System.getProperty("user.dir") + File.separator + "Data/tasks.json";
        List<String> lines = Files.readAllLines(Paths.get(path));
        String json = String.join("\n", lines);

        Type t = new TypeToken<List<ToDoTask>>() {}.getType();
        List<ToDoTask> tasks = new Gson().fromJson(json, t);

        //split the list into two: one with completed, one with incomplete tasks
        Predicate<ToDoTask> completedPredicate = task -> task.getCompletionDate().isPresent();
        List<ToDoTask> completed = tasks.stream().filter(completedPredicate).collect(Collectors.toList());
        List<ToDoTask> incomplete = tasks.stream().filter(completedPredicate.negate()).collect(Collectors.toList());


        Platform.runLater(() -> {
            this.setUpTree(this.taskTreeView.getRoot(), incomplete);
        });

        Platform.runLater(() -> {
            this.setUpFinishedTasksTree(this.finishedTasksTreeView.getRoot(), completed);
        });
    }

    private List<ToDoTask> collectTasks() {
        //collect finished and unfinished and put them in a single list
        List<ToDoTask> tasks = new ArrayList<>();
        List<ToDoTask> finished = this.finishedTasksTreeView.getRoot().getChildren().stream().map(TreeItem::getValue).collect(Collectors.toList());
        List<ToDoTask> unfinished = this.taskTreeView.getRoot().getChildren().stream().map(TreeItem::getValue).collect(Collectors.toList());
        tasks.addAll(finished);
        tasks.addAll(unfinished);
        return tasks;
    }

    private void saveTasksToFile() throws IOException {
        String path = System.getProperty("user.dir") + File.separator + "Data/tasks.json";
        Gson g = new GsonBuilder().setPrettyPrinting().create();
        List<ToDoTask> tasks = this.collectTasks();
        String json = g.toJson(tasks);
        Files.write(Paths.get(path), json.getBytes());
    }

    @Override
    public void saveChangesToFile() throws IOException {
        this.saveTasksToFile();
    }
}
