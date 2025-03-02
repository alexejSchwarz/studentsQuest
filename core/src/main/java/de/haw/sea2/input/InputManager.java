package de.haw.sea2.input;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Array;

public class InputManager implements InputProcessor {
    private final GameKey[] keyMapping;
    private final boolean[] keyState;
    private final Array<KeyInputListener> listeners;

    public InputManager() {
        this.keyMapping = new GameKey[256];
        for (final GameKey key : GameKey.values()) {
            for (final int code : key.keyCodes) {
                keyMapping[code] = key;
            }
        }
        this.keyState = new boolean[GameKey.values().length];
        listeners = new Array<>();
    }

    public void addKeyInputListener(final KeyInputListener listener) {
        listeners.add(listener);
    }

    public void removeKeyInputListener(final KeyInputListener listener) {
        listeners.removeValue(listener, true);
    }

    public void notifyKeyDown(final GameKey key) {
        keyState[key.ordinal()] = true;
        for (final KeyInputListener listener : listeners) {
            listener.keyDown(this, key);
        }
    }

    public void notifyKeyUp(final GameKey key) {
        keyState[key.ordinal()] = false;
        for (final KeyInputListener listener : listeners) {
            listener.keyUp(this, key);
        }
    }

    public boolean isKeyDown(final GameKey key) {
        return keyState[key.ordinal()];
    }

    @Override
    public boolean keyDown(final int keycode) {
        final GameKey key = keyMapping[keycode];
        if (key == null) {
            // no relevant key for game
            return false;
        }

        notifyKeyDown(key);
        return true;
    }

    @Override
    public boolean keyUp(final int keycode) {
        final GameKey key = keyMapping[keycode];
        if (key == null) {
            // no relevant key for game
            return false;
        }

        notifyKeyUp(key);
        return true;
    }

    //Not in use
    @Override
    public boolean keyTyped(final char character) {
        return false;
    }

    @Override
    public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
        return false;
    }

    @Override
    public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(final int screenX, final int screenY, final int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(final int screenX, final int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
