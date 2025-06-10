package de.haw.sea2.input;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Array;

/**
 * Verwaltet die Spieleingaben und leitet sie an die entsprechenden Zuhörer
 * (Listeners) weiter.
 *
 * <p>
 * Diese Klasse bildet die Brücke zwischen den tatsächlichen Tastaturereignissen
 * des
 * Betriebssystems und den spielspezifischen Aktionen. Sie implementiert
 * LibGDX's InputProcessor
 * Interface, um direkt Tastatureingaben zu empfangen und wandelt diese in
 * GameKey-Ereignisse um.
 * </p>
 *
 * <p>
 * Sie verwendet das Observer-Pattern: Klassen, die auf Tasteneingaben reagieren
 * möchten,
 * können sich als KeyInputListener registrieren und werden dann über
 * Tastenereignisse informiert.
 * </p>
 */
public class InputManager implements InputProcessor {
    /**
     * Eine Zuordnungstabelle (Mapping), die jedem Tastencode eine GameKey-Aktion
     * zuweist.
     *
     * <p>
     * Der Index des Arrays entspricht dem Tastencode (z.B. 51 für 'W'),
     * der Wert an dieser Stelle ist die zugehörige GameKey-Enum-Konstante (z.B.
     * UP).
     * </p>
     */
    private final GameKey[] keyMapping;

    /**
     * Speichert den aktuellen Zustand jeder GameKey-Aktion (gedrückt oder nicht
     * gedrückt).
     *
     * <p>
     * Der Index entspricht der Position der GameKey-Konstante in der
     * Enum-Definition
     * (z.B. UP.ordinal() für den Index von UP). Der Wert an dieser Stelle ist true,
     * wenn die Taste gedrückt ist, ansonsten false.
     * </p>
     */
    private final boolean[] keyState;

    /**
     * Eine Liste aller registrierten KeyInputListener, die über Tastenereignisse
     * informiert werden sollen.
     */
    private final Array<KeyInputListener> listeners;

    /**
     * Erstellt einen neuen InputManager.
     *
     * <p>
     * Der Konstruktor initialisiert die Zuordnungstabelle für Tastencodes zu
     * GameKey-Aktionen.
     * Für jede GameKey-Konstante werden alle zugehörigen Tastencodes in der
     * keyMapping-Tabelle eingetragen.
     * </p>
     *
     * <p>
     * Beispiel: Wenn UP sowohl der W-Taste (Code 51) als auch der
     * Pfeil-nach-oben-Taste (Code 19)
     * zugeordnet ist, dann wird sowohl an Position 51 als auch an Position 19 im
     * keyMapping-Array
     * der Wert UP eingetragen.
     * </p>
     */
    public InputManager() {
        // Initialisiere ein Array für bis zu 256 verschiedene Tastencodes
        this.keyMapping = new GameKey[256];

        // Für jede GameKey-Konstante (UP, DOWN, LEFT, RIGHT, BACK)
        for (final GameKey key : GameKey.values()) {
            // Für jeden Tastencode, der dieser Konstante zugeordnet ist
            for (final int code : key.keyCodes) {
                // Trage die GameKey-Konstante an der Position des Tastencodes ein
                keyMapping[code] = key;
            }
        }

        // Initialisiere ein Array für den Zustand jeder GameKey-Konstante
        this.keyState = new boolean[GameKey.values().length];

        // Erstelle eine leere Liste für die Zuhörer
        listeners = new Array<>();
    }

    /**
     * Registriert einen neuen KeyInputListener, der über Tastenereignisse
     * informiert werden soll.
     *
     * <p>
     * Objekte, die das KeyInputListener-Interface implementieren, können sich
     * hiermit anmelden,
     * um über Tastenereignisse benachrichtigt zu werden (z.B. das
     * PlayerMovementSystem).
     * </p>
     *
     * @param listener Das Objekt, das über Tastenereignisse informiert werden soll
     */
    public void addKeyInputListener(final KeyInputListener listener) {
        listeners.add(listener);
    }

    /**
     * Entfernt einen registrierten KeyInputListener aus der Liste der zu
     * benachrichtigenden Objekte.
     *
     * <p>
     * Dies ist wichtig, um Speicherlecks zu vermeiden, wenn ein Objekt nicht mehr
     * verwendet wird.
     * </p>
     *
     * @param listener Das Objekt, das nicht mehr über Tastenereignisse informiert
     *                 werden soll
     */
    public void removeKeyInputListener(final KeyInputListener listener) {
        listeners.removeValue(listener, true);
    }

    public Array<KeyInputListener> getKeyInputListeners() {
        return listeners;
    }

    /**
     * Benachrichtigt alle registrierten Zuhörer, dass eine Taste gedrückt wurde.
     *
     * <p>
     * Diese Methode aktualisiert den Zustand der Taste auf "gedrückt" und
     * informiert dann alle registrierten KeyInputListener über das Ereignis.
     * </p>
     *
     * @param key Die GameKey-Konstante, die dem Tastendruck entspricht
     */
    public void notifyKeyDown(final GameKey key) {
        // Setze den Zustand der Taste auf "gedrückt" (true)
        keyState[key.ordinal()] = true;

        // Informiere alle registrierten Zuhörer über den Tastendruck
        for (final KeyInputListener listener : listeners) {
            listener.keyDown(this, key);
        }
    }

    /**
     * Benachrichtigt alle registrierten Zuhörer, dass eine Taste losgelassen wurde.
     *
     * <p>
     * Diese Methode aktualisiert den Zustand der Taste auf "nicht gedrückt" und
     * informiert dann alle registrierten KeyInputListener über das Ereignis.
     * </p>
     *
     * @param key Die GameKey-Konstante, die dem Loslassen der Taste entspricht
     */
    public void notifyKeyUp(final GameKey key) {
        // Setze den Zustand der Taste auf "nicht gedrückt" (false)
        keyState[key.ordinal()] = false;

        // Informiere alle registrierten Zuhörer über das Loslassen der Taste
        for (final KeyInputListener listener : listeners) {
            listener.keyUp(this, key);
        }
    }

    /**
     * Prüft, ob eine bestimmte GameKey-Taste aktuell gedrückt ist.
     *
     * <p>
     * Diese Methode wird beispielsweise im PlayerMovementSystem verwendet,
     * um zu prüfen, ob beim Loslassen einer Taste eine andere Richtungstaste noch
     * gedrückt ist.
     * </p>
     *
     * @param key Die GameKey-Konstante, deren Zustand geprüft werden soll
     * @return true, wenn die Taste gedrückt ist, sonst false
     */
    public boolean isKeyDown(final GameKey key) {
        return keyState[key.ordinal()];
    }

    /**
     * Wird aufgerufen, wenn eine physische Taste gedrückt wird.
     *
     * <p>
     * Diese Methode ist Teil des InputProcessor-Interfaces und wird automatisch
     * aufgerufen, wenn der Benutzer eine Taste drückt. Sie wandelt den Tastencode
     * in eine
     * GameKey-Konstante um (wenn eine Zuordnung existiert) und benachrichtigt dann
     * alle Zuhörer.
     * </p>
     *
     * @param keycode Der Code der gedrückten Taste
     * @return true, wenn das Ereignis verarbeitet wurde, sonst false
     */
    @Override
    public boolean keyDown(final int keycode) {
        // Finde die GameKey-Konstante für den Tastencode
        final GameKey key = keyMapping[keycode];
        if (key == null) {
            // Wenn keine Zuordnung existiert, ignoriere die Taste
            return false;
        }

        // Benachrichtige alle Zuhörer über den Tastendruck
        notifyKeyDown(key);
        return true;
    }

    /**
     * Wird aufgerufen, wenn eine physische Taste losgelassen wird.
     *
     * <p>
     * Diese Methode ist Teil des InputProcessor-Interfaces und wird automatisch
     * aufgerufen, wenn der Benutzer eine Taste loslässt. Sie wandelt den Tastencode
     * in eine
     * GameKey-Konstante um (wenn eine Zuordnung existiert) und benachrichtigt dann
     * alle Zuhörer.
     * </p>
     *
     * @param keycode Der Code der losgelassenen Taste
     * @return true, wenn das Ereignis verarbeitet wurde, sonst false
     */
    @Override
    public boolean keyUp(final int keycode) {
        // Finde die GameKey-Konstante für den Tastencode
        final GameKey key = keyMapping[keycode];
        if (key == null) {
            // Wenn keine Zuordnung existiert, ignoriere die Taste
            return false;
        }

        // Benachrichtige alle Zuhörer über das Loslassen der Taste
        notifyKeyUp(key);
        return true;
    }

    // Die folgenden Methoden sind Teil des InputProcessor-Interfaces, werden aber
    // in diesem Spiel nicht verwendet

    /**
     * Wird aufgerufen, wenn ein Zeichen eingegeben wird (wird in diesem Spiel nicht
     * verwendet).
     */
    @Override
    public boolean keyTyped(final char character) {
        return false;
    }

    /**
     * Wird aufgerufen, wenn der Bildschirm berührt wird (wird in diesem Spiel nicht
     * verwendet).
     */
    @Override
    public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
        return false;
    }

    /**
     * Wird aufgerufen, wenn eine Bildschirmberührung endet (wird in diesem Spiel
     * nicht verwendet).
     */
    @Override
    public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) {
        return false;
    }

    /**
     * Wird aufgerufen, wenn eine Bildschirmberührung abgebrochen wird (wird in
     * diesem Spiel nicht verwendet).
     */
    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    /**
     * Wird aufgerufen, wenn bei Berührung über den Bildschirm gezogen wird (wird in
     * diesem Spiel nicht verwendet).
     */
    @Override
    public boolean touchDragged(final int screenX, final int screenY, final int pointer) {
        return false;
    }

    /**
     * Wird aufgerufen, wenn die Maus bewegt wird (wird in diesem Spiel nicht
     * verwendet).
     */
    @Override
    public boolean mouseMoved(final int screenX, final int screenY) {
        return false;
    }

    /**
     * Wird aufgerufen, wenn das Mausrad gedreht wird (wird in diesem Spiel nicht
     * verwendet).
     */
    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
