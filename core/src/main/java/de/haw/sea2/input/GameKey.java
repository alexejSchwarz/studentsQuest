package de.haw.sea2.input;

import com.badlogic.gdx.Input;

public enum GameKey {
    UP(Input.Keys.W, Input.Keys.UP),
    DOWN(Input.Keys.S,Input.Keys.DOWN),
    LEFT(Input.Keys.A,Input.Keys.LEFT),
    RIGHT(Input.Keys.D,Input.Keys.RIGHT),
    BACK(Input.Keys.ESCAPE);

    final int[] keyCodes;

    GameKey(final int... keyCode) {
        this.keyCodes = keyCode;
    }

    public int[] getKeyCodes() {
        return keyCodes;
    }
}
