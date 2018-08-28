public abstract class PlayerCommon implements Player {
    protected Integer turnsCount;
    private Integer id;
    private String name;
    private String discType;

    public PlayerCommon(Integer id, String name, String discType){
        this.turnsCount = 0;
        this.id = id;
        this.name = name;
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
    public String getName() {
        return name;
    }

    @Override
    public String getDiscType() {
        return this.discType;
    }

    @Override
    public String toString() {
        return String.format("#%d: \tdisc=%s, \t\tturns=%d\n", this.id, this.discType, this.turnsCount);
    }
}
