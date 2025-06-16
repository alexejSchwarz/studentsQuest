package de.haw.sea2.input;

public interface ResettableInputListener extends ScreenKeyInputListener {

    /**
     * Setzt den Zustand des ResettableInputListener zurück.
     *
     * <p>
     * Diese Methode wird aufgerufen, um den Zustand des ResettableInputListener
     * zurückzusetzen,
     * z.B. wenn es einen ScreenWechsel gibt oder wenn der Listener
     * nicht mehr benötigt wird.
     * </p>
     *
     * In hide() der Klasse, wo ScreenKeyInputListeners gehalten werden (hier GameScreen) soll dann auch reset aufgerufen werden
     */
    void reset();

}
