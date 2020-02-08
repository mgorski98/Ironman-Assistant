import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;


public class Main extends javafx.application.Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Ironman Assistant");
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("mainwindow.fxml"));
        Parent p = loader.load();
        MainWindowController controller = loader.getController();
        Scene s = new Scene(p);
        s.getAccelerators().put(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN), controller::saveChanges);
        primaryStage.setScene(s);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
