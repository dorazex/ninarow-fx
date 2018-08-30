import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Board {
    private int rows;
    private int columns;
    private HashMap<Integer, String> playersDiscTypeMap;
    private ArrayList<ArrayList<Integer>> cells;
    private Integer playersCount;

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public ArrayList<ArrayList<Integer>> getCells() {
        return cells;
    }

    public Board(int rows, int columns){
        this.rows = rows;
        this.columns = columns;

        this.playersDiscTypeMap = new HashMap<>();
        this.playersDiscTypeMap.put(0, "_");

        this.clearBoard();
    }

    private void clearBoard(){
        this.cells = new ArrayList<>();
        for (int i = 0; i < this.columns; i++) {
            ArrayList<Integer> column = new ArrayList<Integer>();
            for (int j = 0; j < this.rows; j++) {
                column.add(0);
            }
            this.cells.add(column);
        }
    }

    private Boolean canInsert(Integer column){
        return !this.getAvailableIndexInColumn(column).equals(-1);
    }

    private Integer getAvailableIndexInColumn(int column){
        return this.cells.get(column).lastIndexOf(0);
    }

    private Integer countAvailableCells(){
        Integer count = 0;
        for (ArrayList<Integer> column: this.cells){
            for (Integer cellContent: column){
                if (cellContent.equals(0)) count+=1;
            }
        }
        return count;
    }

    private Boolean isTargetInSequence(String sequence, Integer target){
        for (int i = 1; i < this.playersCount + 1; i++) {
            if (sequence.matches(String.format(".*%d{%d}.*", i, target))) {
                return true;
            }
        }
        return false;
    }

    private Integer getCellContent(Integer row, Integer column){
        return this.cells.get(column).get(row);
    }

    public Boolean isTargetReached(Integer target){
        String sequenceToCheck = "";
        for (int i = 0; i < this.rows; i++) {
            sequenceToCheck = "";
            for (ArrayList<Integer> column: this.cells){
                sequenceToCheck += column.get(i);
            }
            if (this.isTargetInSequence(sequenceToCheck, target)) return true;
        }

        for (ArrayList<Integer> column: this.cells){
            sequenceToCheck = "";
            for (Integer cellContent: column){
                sequenceToCheck += String.format("%d", cellContent);
            }
            if (this.isTargetInSequence(sequenceToCheck, target)) return true;
        }

        String boardAsLongString = "";
        for (int i = 0; i < this.rows; i++) {
            for (ArrayList<Integer> column: this.cells){
                boardAsLongString += column.get(i);
            }
        }

        for (int i = 0; i < boardAsLongString.length(); i++) {
            sequenceToCheck = "";
            for (int j = i; j < boardAsLongString.length(); j+=target+1) {
                sequenceToCheck += boardAsLongString.charAt(j);
            }
            if (this.isTargetInSequence(sequenceToCheck, target)) return true;
            sequenceToCheck = "";
            for (int j = i; j < boardAsLongString.length(); j+=target-1) {
                sequenceToCheck += boardAsLongString.charAt(j);
            }
            if (this.isTargetInSequence(sequenceToCheck, target)) return true;
        }

        return false;
    }

    public Boolean isFull(){
        return this.countAvailableCells().equals(0);
    }

    public void addPlayers(ArrayList<Player> players){
        for (Player player: players){
            String lastCharOfDiscType = Character.toString(player.getDiscType().charAt(player.getDiscType().length() - 1));
            this.playersDiscTypeMap.put(player.getId(), lastCharOfDiscType);
        }
        this.playersCount = players.size();
    }

    public TurnRecord putDisc(Player player, int column){
        TurnRecord turnRecord = null;
        if (this.canInsert(column)){
            this.cells.get(column).set(this.getAvailableIndexInColumn(column), player.getId());
            turnRecord = new TurnRecord(player, column);
        }
        return turnRecord;
    }

    @Override
    public String toString() {
        String boardString = "";
        for (int i = 0; i < this.rows; i++) {
            String padding = "  ";
            if (this.rows - i > 9){
                padding = " ";
            }
            boardString += String.format("%d%s", this.rows - i, padding);
            for (ArrayList<Integer> column: this.cells){

                boardString += this.playersDiscTypeMap.get(column.get(i)) + "  ";
            }
            boardString += "\n";
        }
        boardString += "   ";
        for (int i = 0; i < this.columns; i++) {
            String padding = "  ";
            if (i + 1 > 9){
                padding = " ";
            }
            boardString += String.format("%d%s", i + 1, padding);
        }

        return boardString;
    }
}
