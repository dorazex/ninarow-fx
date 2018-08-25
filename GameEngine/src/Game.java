import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Game {
    private int target;
    private Board board;
    private ArrayList<Player> players;
    private Boolean isStarted;
    private Integer currentPlayerIndex;
    private Date startDate;
    private Player winnerPlayer;
    private History history;

    public int getTarget() { return target; }

    public Board getBoard(){ return board; }

    public ArrayList<Player> getPlayers() { return players; }

    public Boolean getIsStarted() {
        return isStarted;
    }

    public Integer getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public Player getWinnerPlayer(){
        return this.winnerPlayer;
    }

    public History getHistory() {
        return history;
    }

    public Game(){};

    public Game(int target, int rows, int columns){
        this.target = target;
        this.board = new Board(rows, columns);
        this.isStarted = false;
        this.currentPlayerIndex = 0;
        this.startDate = null;
        this.winnerPlayer = null;
        this.history = new History();
    }

    public Game(int target, Board board){
        this.target = target;
        this.board = board;
        this.isStarted = false;
        this.currentPlayerIndex = 0;
        this.startDate = null;
    }

    private String getDurationString(Date currentDate) {
        long diffInSeconds = (currentDate.getTime() - this.startDate.getTime()) / 1000;
        long seconds = Math.floorMod(diffInSeconds, 60);
        long minutes = Math.floorDiv(diffInSeconds, 60);

        return String.format("%02d:%02d", minutes, seconds);
    }

    private void advanceToNextPlayer(){
        this.currentPlayerIndex = (this.currentPlayerIndex+ 1) % this.players.size();
    }

    private Boolean isEndWithWinner(){
        return this.board.isTargetReached(this.target);
    }

    public void start(ArrayList<Player> players){
        this.players = players;
        this.board.addPlayers(this.players);

        this.isStarted = true;
        this.startDate = new Date();
    }

    public Boolean makeTurn(){
        TurnRecord turnRecord = this.players.get(this.currentPlayerIndex).makeTurn(this.board);
        this.history.pushTurn(turnRecord);
        if (this.isEndWithWinner()){
            this.winnerPlayer = this.players.get(this.currentPlayerIndex);
            return true;
        }
        this.advanceToNextPlayer();
        return this.board.isFull();
    }

    @Override
    public String toString() {

        String fullFormat =
                "%s\n" +
                "------------------------------------\n" +
                "Game started: %s\n" +
                "Target: %d\n" +
                "Turn of: %d\n" +
                "%s" +
                "\n" +
                "//////  Board  //////\n" +
                "%s\n" +
                "Time: %s\n\n" +
                "%s\n";

        String shortFormat =
                "------------------------------------\n" +
                "Game started: %s\n" +
                "Target: %d\n" +
                "//////  Board  //////\n" +
                "%s\n\n" +
                "%s\n";

        String menu =
                "Commands:\n" +
                "1 - LOAD config XML file\n" +
                "2 - START game\n" +
                "3 - SHOW game state\n" +
                "4 - PLAY turn\n" +
                "5 - SHOW history\n" +
                "6 - EXIT game\n";

        if (this.isStarted) {
            String playersBlock = "";
            for (Player player: this.players){
                playersBlock += player.toString();
            }

            String headerLine = String.format("Game of %d players, on a %dx%d board",
                    this.players.size(),
                    this.board.getRows(),
                    this.board.getColumns());

            Date currentTime = new Date();
            String durationString = this.getDurationString(currentTime);
            return String.format(fullFormat,
                    headerLine,
                    this.isStarted.toString(),
                    this.target,
                    this.currentPlayerIndex + 1,
                    playersBlock,
                    this.board,
                    durationString,
                    menu);
        } else {
            return String.format(shortFormat,
                    this.isStarted.toString(),
                    this.target,
                    this.board,
                    menu);
        }
    }
}
