package de.haw.sea2.input;

public interface KeyInputListener {
    void keyDown(final InputManager manager, final GameKey key);

    void keyUp(final InputManager manager, final GameKey key);
}
