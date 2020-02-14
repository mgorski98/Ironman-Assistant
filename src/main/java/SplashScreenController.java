import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

//TODO: add classes for ability to create profiles and create a file to store them
public class SplashScreenController {

    @FXML
    private VBox rootContainer;

    @FXML
    private Label titleDisplayLabel;

    private static final int TITLE_DISPLAY_FONT_SIZE = 36;

    public void initialize() {
        Font.loadFont(this.getClass().getResource("/Fonts/Beholder.ttf").toExternalForm(), 0);
        this.setLabelStyle();
        this.setBackgroundImage();
        this.startSplashScreen();
    }

    private void startSplashScreen() {
        new Thread(() -> {
            try {
                this.loadAndShowMainWindow();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void setLabelStyle() {
        final String styleString = "-fx-font-family: Beholder; -fx-font-size: " + TITLE_DISPLAY_FONT_SIZE + "px;";
        this.titleDisplayLabel.setStyle(styleString);
    }

    private void setBackgroundImage() {

    }

    private void loadAndShowMainWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/FXML/mainwindow.fxml"));
        Parent p = loader.load();
        MainWindowController controller = loader.getController();
        Scene s = new Scene(p);
        s.getAccelerators().put(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN), () -> {
            try {
                controller.saveChanges();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Platform.runLater(() -> {
            Stage mainWindow = new Stage();
            mainWindow.setScene(s);
            mainWindow.setTitle("Ironman Assistant");
            mainWindow.getIcons().add(new Image(this.getClass().getResource("/Images/applogo.png").toExternalForm()));
            mainWindow.setOnCloseRequest(event -> {
                Alert closingAlert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to save the changes from all the pages?");
                Optional<ButtonType> confirmationResult = closingAlert.showAndWait();
                confirmationResult.ifPresent(result -> {
                    controller.getPageControllers().forEach(c -> {
                        try {
                            c.saveChangesToFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                            //stop here, handle it in some other way
                        }
                    });
                });
            });
            mainWindow.show();
            this.rootContainer.getScene().getWindow().hide();
        });
    }
}
