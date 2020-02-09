import ActivityReminders.ActivityRemindersController;
import Milestones.MilestoneListController;
import Saving.Saveable;
import ToDoList.ToDoListController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Pagination;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainWindowController {

    private List<Node> pages;
    private List<Saveable> controllers;

    @FXML
    private Pagination contents;

    public MainWindowController() {
        this.pages = new ArrayList<>();
        this.controllers = new ArrayList<>();
        try {
            FXMLLoader loader1 = new FXMLLoader(this.getClass().getResource("/todopage.fxml"));
            this.pages.add(loader1.load());
            ToDoListController controller1 = loader1.getController();
            this.controllers.add(controller1);
            FXMLLoader loader2 = new FXMLLoader(this.getClass().getResource("/milestonepage.fxml"));
            this.pages.add(loader2.load());
            MilestoneListController controller2 = loader2.getController();
            this.controllers.add(controller2);
            FXMLLoader loader3 = new FXMLLoader(this.getClass().getResource("/activityreminderspage.fxml"));
            this.pages.add(loader3.load());
            ActivityRemindersController controller3 = loader3.getController();
            this.controllers.add(controller3);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initialize() {
        this.contents.setPageFactory(pages::get);
        this.contents.setMaxPageIndicatorCount(pages.size());
        this.contents.setPageCount(pages.size());
        this.contents.setCurrentPageIndex(0);
    }

    public void saveChanges() throws IOException {
        this.controllers.get(this.contents.getCurrentPageIndex()).saveChangesToFile();
        new Alert(Alert.AlertType.INFORMATION, "Saved changes successfully!").showAndWait();
    }
}
