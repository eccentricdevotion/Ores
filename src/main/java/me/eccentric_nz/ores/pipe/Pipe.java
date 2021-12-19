package me.eccentric_nz.ores.pipe;

public enum Pipe {

    STRAIGHT(1001),
    LEFT(1002),
    RIGHT(1003);

    private final int customeModelData;

    Pipe(int customeModelData) {
        this.customeModelData = customeModelData;
    }

    public int getCustomeModelData() {
        return customeModelData;
    }
}
