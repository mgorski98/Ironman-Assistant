import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class SplashScreenController {

    @FXML
    private AnchorPane rootContainer;

    public void initialize() {
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

    private void loadAndShowMainWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/mainwindow.fxml"));
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
            mainWindow.show();
            this.rootContainer.getScene().getWindow().hide();
        });
    }
}
