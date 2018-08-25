public class TurnRecord {
    private Player player;
    private Integer column;

    public Player getPlayer() {
        return player;
    }

    public Integer getColumn() {
        return column;
    }

    public TurnRecord(Player player, Integer column){
        this.player = player;
        this.column = column;
    }

    @Override
    public String toString() {
        return String.format("Player <%s> have put a disc of type <%s> at column <%d>",
                this.player.getId(),
                this.player.getDiscType(),
                this.column);
    }
}
