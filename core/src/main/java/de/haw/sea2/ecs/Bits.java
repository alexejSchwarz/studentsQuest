package de.haw.sea2.ecs;

public enum Bits {

    BIT_PLAYER(1 << 0),
    BIT_WALL(1 << 1);

    public final short value;

    Bits(int value) {
        this.value = (short) value;
    }
}
