import java.util.Scanner;

public class PlayerFX extends PlayerCommon {
    private String id;
    private String name;
    private String discType;
    private String color;


    public PlayerFX(Integer id, String name, String color){
        super(id, color);
        this.name = name;
        this.color = color;
    }

    @Override
    public TurnRecord makeTurn(Board board) {
        TurnRecord turnRecord = board.putDisc(this, 1);
        return turnRecord;
    }
}
