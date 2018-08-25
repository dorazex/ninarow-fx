import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller {
    @FXML
    private VBox mainVBox;
    @FXML
    private HBox controlsHBox;
    @FXML
    public AnchorPane boardAnchor;
    @FXML
    private GridPane boardGridPane;
    @FXML
    private Button loadButton;
    @FXML
    private Button startButton;
    @FXML
    private Button historyButton;
    @FXML
    private Button endGameButton;
    @FXML
    private Label messageLabel;

    private Game game;
    private Desktop desktop = Desktop.getDesktop();


    public Controller()
    {
    }

    private void initializeBoard(Integer rows, Integer columns){

        boardGridPane.getColumnConstraints().clear();
        boardGridPane.getRowConstraints().clear();
        boardGridPane.setAlignment(Pos.CENTER);

        RowConstraints rowConstraints = new RowConstraints(
                0,
                0,
                0,
                Priority.ALWAYS,
                VPos.CENTER,
                true);
        rowConstraints.setPercentHeight(100);
        ColumnConstraints columnConstraints = new ColumnConstraints(
                0,
                0,
                0,
                Priority.ALWAYS,
                HPos.CENTER, true);
        columnConstraints.setPercentWidth(100);

        for (int x = 0; x < columns; x++) {
            boardGridPane.getColumnConstraints().add(columnConstraints);
            boardGridPane.getRowConstraints().add(rowConstraints);
            for (int y = 0; y < rows; y++) {
                Pane cellPane = new Pane();
                BorderStroke[] borders = new BorderStroke[4];
                Arrays.fill(borders, new BorderStroke(
                        Paint.valueOf("BLACK"),
                        BorderStrokeStyle.SOLID,
                        new CornerRadii(0.5),
                        BorderWidths.DEFAULT));
                cellPane.setBorder(new Border(borders));
                cellPane.setBackground(
                        new Background(
                                new BackgroundFill(
                                        Paint.valueOf("GREY"),
                                        CornerRadii.EMPTY,
                                        new Insets(0)
                                )
                        )
                );
                boardGridPane.add(cellPane, x, y);
            }
        }
    }

    private void createGame(HashMap<String, Object> parametersMap){
        String variant = (String) parametersMap.get("variant");
        Integer target = (Integer) parametersMap.get("target");
        Integer rows = (Integer) parametersMap.get("rows");
        Integer columns = (Integer) parametersMap.get("columns");

        this.game = new Game(target, rows, columns);
        initializeBoard(this.game.getBoard().getRows(), this.game.getBoard().getColumns());
    }

    private void loadXml(String configFilePath){
//        configFilePath = "/home/duke/Downloads/ex1-small.xml";

        final String configFilePathFinal = configFilePath;
        Task<ObservableList<HashMap<String, Object>>> task = new Task<ObservableList<HashMap<String, Object>>>() {
            @Override protected ObservableList<HashMap<String, Object>> call() throws Exception {
                HashMap<String, Object> parametersMap = null;
                ObservableList<HashMap<String, Object>> results = FXCollections.observableArrayList();
                try {
                    parametersMap = XmlLoader.getGameBasicInitParameters(configFilePath);
                    results.add(parametersMap);
                    updateMessage("XML Loaded successfully");
                    return results;
                } catch (ConfigXmlException e){
                    updateMessage(e.getMessage());
                }
                return results;
            }
        };

        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
        try {
            th.join();
        } catch (InterruptedException e){

        }

        task.setOnSucceeded(event -> {
            if (task.getValue().size() != 0) {
                createGame(task.getValue().get(0));
                messageLabel.setText(task.getMessage());
            } else {
                messageLabel.setTextFill(Paint.valueOf("RED"));
                messageLabel.setBackground(new Background(new BackgroundFill(Paint.valueOf("YELLOW"), CornerRadii.EMPTY, Insets.EMPTY)));
                messageLabel.setText(task.getMessage());
            }
        });

    }

    private String getXmlPathUserInput(){
        final FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) boardAnchor.getScene().getWindow();

        File file = fileChooser.showOpenDialog(stage);
        return file.getAbsolutePath();
    }

    private void loadHandler(){
        String configXmlPath = getXmlPathUserInput();
        loadXml(configXmlPath);
    }

    @FXML
    private void initialize()
    {
        loadButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                loadHandler();
            }
        });

    }

    @FXML
    private void printOutput()
    {
    }
}
