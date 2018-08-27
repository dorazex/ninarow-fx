public interface Player {
    Integer getTurnsCount();
    Integer getId();
    String getDiscType();
    String getName();
    TurnRecord makeTurn(Board board);
}
