import Milestones.MilestoneListController;
import ToDoList.ToDoListController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Pagination;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainWindowController {

    private List<Node> pages;
    private ToDoListController todoController;
    private MilestoneListController milestoneController;

    @FXML
    private Pagination contents;

    public MainWindowController() {
        pages = new ArrayList<>();
        try {
            FXMLLoader loader1 = new FXMLLoader(this.getClass().getResource("/todopage.fxml"));
            pages.add(loader1.load());
            todoController = loader1.getController();
            FXMLLoader loader2 = new FXMLLoader(this.getClass().getResource("/milestonepage.fxml"));
            pages.add(loader2.load());
            milestoneController = loader2.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initialize() {
        contents.setPageFactory(pages::get);
        contents.setMaxPageIndicatorCount(pages.size());
        contents.setPageCount(pages.size());
        contents.setCurrentPageIndex(0);
    }

    public void saveChanges() {
        switch (this.contents.getCurrentPageIndex()) {
            case 0: {
                try {
                    Method m = this.todoController.getClass().getDeclaredMethod("saveTasksToFile");
                    m.setAccessible(true);
                    m.invoke(this.todoController);
                    new Alert(Alert.AlertType.INFORMATION, "Changes saved successfully!").showAndWait();
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
            break;
            case 1: {

            }
            break;
        }
    }
}
