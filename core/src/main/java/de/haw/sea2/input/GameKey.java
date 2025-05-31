package de.haw.sea2.input;

import com.badlogic.gdx.Input;

/**
 * Definiert die im Spiel verwendeten Tasten und ihre Zuordnungen zu
 * tatsächlichen Tastaturtasten.
 *
 * <p>
 * Diese Enum-Klasse dient als zentrale Stelle für die Definition der
 * Spielsteuerung.
 * Jeder Enum-Wert repräsentiert eine Spielaktion (z.B. nach oben bewegen, nach
 * links bewegen)
 * und kann mit mehreren physischen Tastaturtasten verbunden sein, um
 * alternative Steuerungsmöglichkeiten
 * anzubieten (z.B. WASD oder Pfeiltasten).
 * </p>
 */
public enum GameKey {
    /**
     * Tastenaktion für Bewegung nach oben.
     * Kann mit der W-Taste oder der oberen Pfeiltaste ausgelöst werden.
     */
    UP(Input.Keys.W, Input.Keys.UP),

    /**
     * Tastenaktion für Bewegung nach unten.
     * Kann mit der S-Taste oder der unteren Pfeiltaste ausgelöst werden.
     */
    DOWN(Input.Keys.S, Input.Keys.DOWN),

    /**
     * Tastenaktion für Bewegung nach links.
     * Kann mit der A-Taste oder der linken Pfeiltaste ausgelöst werden.
     */
    LEFT(Input.Keys.A, Input.Keys.LEFT),

    /**
     * Tastenaktion für Bewegung nach rechts.
     * Kann mit der D-Taste oder der rechten Pfeiltaste ausgelöst werden.
     */
    RIGHT(Input.Keys.D, Input.Keys.RIGHT),

    /**
     * Taste zum Pausieren des Spiels.
     * Kann mit der ESCAPE-Taste ausgelöst werden.
     */
    PAUSE(Input.Keys.ESCAPE),

    /**
     * Debug: Taste zum Umschalten der Grid-Anzeige.
     * Wird mit der G-Taste ausgelöst.
     */
    DEBUG_GRID(Input.Keys.G),

    /**
     * Debug: Taste zum Umschalten der Path-Anzeige.
     * Wird mit der P-Taste ausgelöst.
     */
    DEBUG_PATH(Input.Keys.P);

    /**
     * Array von Tastencodes, die dieser Spielaktion zugeordnet sind.
     * Enthält die Codes aller Tasten, die die gleiche Aktion auslösen.
     *
     * <p>
     * Beispiel-Arrays für verschiedene GameKey-Werte:
     * <ul>
     * <li>UP: [51, 19] (51 ist der Code für W, 19 für UP)</li>
     * <li>BOTTOM: [47, 20] (47 ist der Code für S, 20 für BOTTOM)</li>
     * <li>BACK: [131] (131 ist der Code für ESCAPE)</li>
     * </ul>
     * Die genauen Zahlenwerte können je nach Implementierung der Input.Keys-Klasse
     * variieren.
     * </p>
     */
    final int[] keyCodes;

    /**
     * Konstruktor für die GameKey-Enum.
     *
     * <p>
     * Der Parameter "int... keyCode" ist ein sogenannter "vararg"-Parameter
     * (variable Argumente).
     * Das bedeutet, dass eine beliebige Anzahl von int-Werten übergeben werden
     * kann. Im Code
     * erscheinen diese Werte dann als Array mit dem Namen "keyCode". Dies
     * ermöglicht es,
     * einer Spielaktion mehrere Tasten zuzuordnen, ohne für jede Taste einen
     * eigenen Parameter
     * definieren zu müssen.
     * </p>
     *
     * <p>
     * Beispiele für die Verwendung:
     * <ul>
     * <li>GameKey(Input.Keys.W) - Eine einzige Taste zuordnen</li>
     * <li>GameKey(Input.Keys.W, Input.Keys.UP) - Zwei Tasten zuordnen</li>
     * <li>GameKey(Input.Keys.W, Input.Keys.UP, Input.Keys.SPACE) - Drei Tasten
     * zuordnen</li>
     * </ul>
     * </p>
     *
     * @param keyCode Eine beliebige Anzahl von Tastencodes aus der Klasse
     *                Input.Keys
     */
    GameKey(final int... keyCode) {
        this.keyCodes = keyCode;
    }

    /**
     * Gibt ein Array mit allen Tastencodes zurück, die dieser Spielaktion
     * zugeordnet sind.
     *
     * <p>
     * Diese Methode wird vom InputManager verwendet, um zu prüfen, ob eine
     * gedrückte
     * Taste einer bestimmten Spielaktion entspricht.
     * </p>
     *
     * @return Ein Array von int-Werten, die die Codes der zugeordneten Tasten
     *         darstellen
     */
    public int[] getKeyCodes() {
        return keyCodes;
    }
}
