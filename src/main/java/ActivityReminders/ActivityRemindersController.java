package ActivityReminders;

import Saving.Saveable;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ActivityRemindersController implements Saveable {

    @FXML
    private Accordion chosenActivitiesAccordion;
    @FXML
    private Accordion activitiesToChooseAccordion;

    public void initialize() {
        this.initAccordion(this.chosenActivitiesAccordion);
        this.initAccordion(this.activitiesToChooseAccordion);
    }

    private void initAccordion(Accordion accordion) {
        if (accordion == null) return;
        accordion.getPanes().clear();

        List<String> paneTitles = Stream.of(
                "Distractions and diversions",
                "Shop runs",
                "Others"
        ).sorted().collect(Collectors.toList());

        paneTitles.forEach(title -> {
            ListView<Activity> activities = new ListView<>();
            TitledPane pane = new TitledPane(title, activities);
            pane.setContent(activities);
            accordion.getPanes().add(pane);
        });
    }

    @Override
    public void saveChangesToFile() throws IOException {

    }
}
