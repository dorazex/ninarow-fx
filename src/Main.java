import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("game.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(scene);

//        primaryStage.minWidthProperty().bind(scene.heightProperty().multiply(1));
//        primaryStage.minHeightProperty().bind(scene.widthProperty().divide(1));
//
//        primaryStage.setHeight(300);
//        primaryStage.setWidth(300);

        ScenicView.show(scene);
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
