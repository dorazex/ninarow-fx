public interface Player {
    Integer getTurnsCount();
    Integer getId();
    String getDiscType();
    TurnRecord makeTurn(Board board);
}
