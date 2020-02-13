import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends javafx.application.Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox p = FXMLLoader.load(this.getClass().getResource("/FXML/splashscreen.fxml"));
        Scene s = new Scene(p);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(s);
        primaryStage.show();
//        primaryStage.setTitle("Ironman Assistant");
//        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("mainwindow.fxml"));
//        Parent p = loader.load();
//        MainWindowController controller = loader.getController();
//        Scene s = new Scene(p);
//        s.getAccelerators().put(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN), () -> {
//            try {
//                controller.saveChanges();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//        primaryStage.getIcons().add(new Image(this.getClass().getResource("/applogo.png").toExternalForm()));
//        primaryStage.setScene(s);
//        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
