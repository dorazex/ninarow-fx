import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.converter.NumberStringConverter;


import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Desktop;
import java.util.List;
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
    @FXML
    private ListView<Label> playersDetailsListView;
    @FXML
    private HBox boardTopHBox;
    @FXML
    private HBox boardBottomHBox;
    @FXML
    private Label durationLabel;

    private Game game;
    private Boolean isGameEnded;
    ArrayList<Player> players;
    private Desktop desktop = Desktop.getDesktop();
    private HashMap<String, Object> configMap;
    private static List<String> colors = Arrays.asList(
            "#ff7a7a",
            "#9ff4af",
            "CYAN",
            "YELLOW",
            "WHITE",
            "PINK");



    public Controller()
    {
    }

    private void updateMessage(String message, Boolean isError){
        if (isError) {
            messageLabel.setTextFill(Paint.valueOf("RED"));
            messageLabel.setBackground(new Background(new BackgroundFill(Paint.valueOf("YELLOW"), CornerRadii.EMPTY, Insets.EMPTY)));
        } else {
            messageLabel.setTextFill(Paint.valueOf("BLACK"));
            messageLabel.setBackground(null);
        }
        messageLabel.setText(message);
        messageLabel.setPrefHeight(25);
        messageLabel.setMinHeight(25);
        messageLabel.setMaxHeight(25);
    }

    public Boolean makeTurn(Integer column){
        Player currentPlayer = game.getPlayers().get(game.getCurrentPlayerIndex());
        if (currentPlayer.getClass().equals(PlayerFX.class)) {
            TurnRecord turnRecord = ((PlayerFX) currentPlayer).makeTurnFX(game.getBoard(), column);

            game.getHistory().pushTurn(turnRecord);
            ((PlayerFX) currentPlayer).setTurnsCount(currentPlayer.getTurnsCount() + 1);
            System.out.println(game.toString());
            if (game.isEndWithWinner()) {
                game.setWinnerPlayer(currentPlayer);
                return true;
            }
            game.advanceToNextPlayer();
            makeComputerTurns();
        }
        return game.getBoard().isFull();
    }

    private void initializeBoard(Integer rows, Integer columns){

        boardGridPane.getColumnConstraints().clear();
        boardGridPane.getRowConstraints().clear();
        boardGridPane.setAlignment(Pos.CENTER);
        boardGridPane.getChildren().clear();

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

            Button topColumnButton = new Button("X");
            topColumnButton.setId(String.format("boardTopButton%d", x));
            Button bottomColumnButton = new Button("X");
            bottomColumnButton.setId(String.format("boardBottomButton%d", x));
            topColumnButton.setMaxWidth(Double.MAX_VALUE);
            bottomColumnButton.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(topColumnButton, Priority.ALWAYS);
            HBox.setHgrow(bottomColumnButton, Priority.ALWAYS);
            this.boardTopHBox.getChildren().add(topColumnButton);
            this.boardBottomHBox.getChildren().add(bottomColumnButton);

            final int columnIndex = x;
            topColumnButton.setOnAction((ActionEvent event) -> {
                topColumnButton.fireEvent(new UserTurnClickEvent(columnIndex));
            });

            topColumnButton.addEventHandler(CustomEvent.CUSTOM_EVENT_TYPE, new UserTurnClickEventHandler() {

                @Override
                public void onUserClick(int column) {
                    if (!game.getIsStarted()){
                        updateMessage("Must start game before making turns", true);
                        return;
                    }
                    isGameEnded = makeTurn(column);
                }

            });

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

        this.configMap = parametersMap;

        String variant = (String) parametersMap.get("variant");
        Integer target = (Integer) parametersMap.get("target");
        Integer rows = (Integer) parametersMap.get("rows");
        Integer columns = (Integer) parametersMap.get("columns");

        this.game = new Game(target, rows, columns);

        playersDetailsListView.getItems().clear();
        ObservableList<Label> items = playersDetailsListView.getItems();
        for (Player player: this.getPlayers()){
            Label playerDetailsLabel = new Label();
            playerDetailsLabel.textProperty().bind(player.detailsProperty());
            playerDetailsLabel.setBackground(
                    new Background(
                            new BackgroundFill(
                                    Paint.valueOf(player.getDiscType()),
                                    CornerRadii.EMPTY,
                                    Insets.EMPTY)));
            items.add(playerDetailsLabel);
        }
        playersDetailsListView.setPrefHeight(items.size() * 24 + 2);

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
                this.updateMessage(task.getMessage(), false);
            } else {
                this.updateMessage(task.getMessage(), true);
            }
        });

    }

    private String getXmlPathUserInput(){
        final FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) boardAnchor.getScene().getWindow();

        File file = fileChooser.showOpenDialog(stage);
        if (file == null) return null;
        return file.getAbsolutePath();
    }

    private void loadHandler(){
        if (this.game != null && this.game.getIsStarted()){
            updateMessage("Cannot load XML: game has already started", true);
        } else {
            String configXmlPath = getXmlPathUserInput();
            if (configXmlPath == null) updateMessage("No file chosen", true);
            loadXml(configXmlPath);
        }
    }

    private ArrayList<Player> getPlayers(){
        if (this.players != null){
            return this.players;
        }
        ArrayList<Player> players = new ArrayList<>();
        ArrayList<HashMap<String, String>> playersMap = (ArrayList<HashMap<String, String>>) this.configMap.get("players");
        Integer i = 0;
        for (HashMap<String, String> playerMap: playersMap){
            Integer id = Integer.parseInt(playerMap.get("id"));
            String name = playerMap.get("name");
            String type = playerMap.get("type");
            String color = Controller.colors.get(i);

            Player player;
            switch (type){
                case "Human":
                    player = new PlayerFX(id, name, color);
                    break;
                case "Computer":
                    player = new PlayerComputer(id, name,  color);
                    break;
                default:
                    player = new PlayerCommon(id, name,  color) {
                        @Override
                        public TurnRecord makeTurn(Board board) {
                            return null;
                        }
                    };
                    break;
            }
            players.add(player);
            i++;
        }
        this.players = players;
        return players;
    }

    private void makeComputerTurns(){
        this.isGameEnded = false;
        while (!this.isGameEnded){
            Player currentPlayer = game.getPlayers().get(game.getCurrentPlayerIndex());
            if (!currentPlayer.getClass().equals(PlayerFX.class)){
                this.isGameEnded = this.game.makeTurn();
                System.out.println(game.toString());
            }else{
                break;
            }
        }
    }

    private void startHandler(){
        if (this.game == null){
            updateMessage("No game loaded yet", true);
        } else if (this.game.getIsStarted() == true){
            updateMessage("Game has already started", true);
        } else{
            this.game.start(this.getPlayers());
            updateMessage("Game started successfully", false);
//            this.durationLabel.textProperty().bind(game.durationProperty());
            KeyFrame update = new KeyFrame(Duration.seconds(0.5), event -> {
                durationLabel.setText(game.getDurationString());
            });
            Timeline tl = new Timeline(update);
            tl.setCycleCount(Timeline.INDEFINITE);
            tl.play();

            System.out.println(this.game.toString());
            System.out.println(this.game.getBoard().toString());
            this.makeComputerTurns();
        }
    }

    @FXML
    private void initialize()
    {
        loadButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                loadHandler();
            }
        });

        startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                startHandler();
            }
        });

    }

    @FXML
    private void printOutput()
    {
    }
}
