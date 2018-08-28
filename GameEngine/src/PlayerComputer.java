import java.util.Random;

public class PlayerComputer extends PlayerCommon {
    public PlayerComputer(Integer id, String name, String discType){
        super(id, name, discType);
    }

    @Override
    public TurnRecord makeTurn(Board board) {
        Integer chosenColumn = new Random().nextInt(board.getColumns());
        TurnRecord turnRecord = board.putDisc(this, chosenColumn);
        Integer tries = 1;
        while (turnRecord == null && tries <= board.getColumns()){
            chosenColumn = (chosenColumn + 1) % board.getColumns();
            turnRecord = board.putDisc(this, chosenColumn);
            tries += 1;
        }
        this.turnsCount += 1;
        return turnRecord;
    }

    @Override
    public String toString() {
        return String.format("%s%s", "Computer\t\t", super.toString());
    }
}
