import Saving.Saveable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Pagination;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainWindowController {

    private List<Node> pages;
    private List<Saveable> controllers;


    @FXML
    private Pagination contents;


    public MainWindowController() {
        this.initializeComponentLists();
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

    private void initializeComponentLists() {
        this.pages = new ArrayList<>();
        this.controllers = new ArrayList<>();
        List<String> paths = Arrays.asList(
                "/FXML/todopage.fxml",
                "/FXML/milestonepage.fxml",
                "/FXML/activityreminderspage.fxml"
        );
        paths.forEach(path -> {
            try {
                FXMLLoader resourceLoader = new FXMLLoader(this.getClass().getResource(path));
                Node page = resourceLoader.load();
                this.pages.add(page);
                this.controllers.add(resourceLoader.getController());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public List<Saveable> getPageControllers() {
        return this.controllers;
    }
}
