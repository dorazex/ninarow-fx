public abstract class PlayerCommon implements Player {
    protected Integer turnsCount;
    private Integer id;
    private String discType;

    public PlayerCommon(Integer id, String discType){
        this.turnsCount = 0;
        this.id = id;
        this.discType = discType;
    }

    @Override
    public Integer getTurnsCount() {
        return this.turnsCount;
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public String getDiscType() {
        return this.discType;
    }

    @Override
    public String toString() {
        return String.format("Player #%d: disc=%s, turns=%d\n", this.id, this.discType, this.turnsCount);
    }
}
