package Milestones;

import Saving.Saveable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MilestoneListController implements Saveable {

    @FXML
    private ListView<Milestone> milestoneListView;
    @FXML
    private DatePicker datepicker;
    @FXML
    private TextField milestoneTextField;
    @FXML
    private TextField milestoneTotalLevelTextField;
    @FXML
    private Button addMilestoneButton;
    @FXML
    private Button clearButton;

    public void initialize() {
        this.setButtonDelegates();
        this.setUpNumberValidator();
        this.setMilestoneViewCellFactory();
        try {
            this.loadMilestones();
        } catch (IOException ignored) { }
    }

    private void setUpNumberValidator() {
        this.milestoneTotalLevelTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                StringProperty sp = (StringProperty)observable;
                sp.setValue(oldValue);
            }
        });
    }

    private void setMilestoneViewCellFactory() {
        this.milestoneListView.setCellFactory(new Callback<ListView<Milestone>, ListCell<Milestone>>() {
            @Override
            public ListCell<Milestone> call(ListView<Milestone> param) {
                return new ListCell<Milestone>() {
                    @Override
                    protected void updateItem(Milestone item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!this.isEmpty()) {
                            this.setText(item.getMilestoneInfo());
                        }
                    }
                };
            }
        });
    }

    private void setButtonDelegates() {
        this.addMilestoneButton.setOnAction(event -> this.addMilestone());
        this.clearButton.setOnAction(event -> this.clearInputs());
    }

    private void addMilestone() {
        if (this.milestoneTextField.getText().isEmpty() ||
            this.milestoneTotalLevelTextField.getText().isEmpty() ||
            this.datepicker.getValue() == null) {
            new Alert(Alert.AlertType.ERROR, "Please input valid information!").showAndWait();
            return;
        }
        Milestone m = this.getMilestoneFromGUI();
        this.milestoneListView.getItems().add(m);
        this.milestoneListView.getItems().sort(Comparator.comparing(Milestone::getMilestoneDate));
        this.milestoneListView.refresh();
        this.clearInputs();
    }

    private Milestone getMilestoneFromGUI() {
        Milestone m = new Milestone();
        m.setMilestoneDate(this.datepicker.getValue());
        m.setMilestoneInfo(this.milestoneTextField.getText());
        int level = Integer.parseInt(this.milestoneTotalLevelTextField.getText());
        m.setTotalLevel(level);
        return m;
    }

    private void clearInputs() {
        this.datepicker.setValue(null);
        this.milestoneTotalLevelTextField.setText("");
        this.milestoneTextField.setText("");
        this.milestoneListView.getSelectionModel().clearSelection();
    }

    private void saveMilestonesToFile() throws IOException {
        String path = System.getProperty("user.dir") + File.separator + "Data/milestones.json";
        Gson g = new GsonBuilder().setPrettyPrinting().create();
        List<Milestone> milestones = new ArrayList<>(this.milestoneListView.getItems());
        String json = g.toJson(milestones);
        Files.write(Paths.get(path), json.getBytes());
    }

    private void loadMilestones() throws IOException {
        String path = System.getProperty("user.dir") + File.separator + "Data/milestones.json";
        String json = String.join("\n", Files.readAllLines(Paths.get(path)));
        Type t = new TypeToken<List<Milestone>>() {}.getType();
        List<Milestone> milestones = new Gson().fromJson(json, t);
        this.milestoneListView.getItems().clear();
        this.milestoneListView.getItems().addAll(milestones);
        this.milestoneListView.getItems().sort(Comparator.comparing(Milestone::getMilestoneDate));
    }

    @Override
    public void saveChangesToFile() throws IOException {
        this.saveMilestonesToFile();
    }
}
