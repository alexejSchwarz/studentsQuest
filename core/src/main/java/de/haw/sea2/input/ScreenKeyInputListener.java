package de.haw.sea2.input;

/**
 * Ein Interface für Objekte, die auf Tasteneingaben reagieren möchten.
 *
 * <p>
 * Dieses Interface verwendet das Observer-Pattern (Beobachter-Muster), bei dem
 * der InputManager der "Beobachtbare" (Observable) ist und die Klassen, die
 * dieses
 * Interface implementieren, die "Beobachter" (Observers) sind. Der InputManager
 * informiert alle registrierten Beobachter, wenn eine Taste gedrückt oder
 * losgelassen wird.
 * </p>
 *
 * <p>
 * Wenn eine Klasse auf Tastatureingaben reagieren möchte (wie z.B. das
 * PlayerMovementSystem
 * für die Spielersteuerung), muss sie:
 * <ol>
 * <li>Dieses Interface implementieren</li>
 * <li>Sich beim InputManager registrieren mit
 * manager.addKeyInputListener(this)</li>
 * <li>Die keyDown- und keyUp-Methoden implementieren, um auf Tastenereignisse
 * zu reagieren</li>
 * </ol>
 * </p>
 *
 * <p>
 * Beispiel für eine Implementierung:
 *
 * <pre>
 * public class PlayerMovementSystem implements KeyInputListener {
 *     public PlayerMovementSystem(StudentsQuest context) {
 *         context.getInputManager().addKeyInputListener(this);
 *     }
 *
 *     &#64;Override
 *     public void keyDown(InputManager manager, GameKey key) {
 *         if (key == GameKey.UP) {
 *             // Spieler nach oben bewegen
 *         }
 *     }
 *
 *     &#64;Override
 *     public void keyUp(InputManager manager, GameKey key) {
 *         if (key == GameKey.UP) {
 *             // Aufwärtsbewegung stoppen
 *         }
 *     }
 * }
 * </pre>
 * </p>
 */
public interface ScreenKeyInputListener {

    /**
     * Wird aufgerufen, wenn eine Taste gedrückt wird.
     *
     * <p>
     * Diese Methode wird vom InputManager aufgerufen, wenn der Benutzer eine Taste
     * drückt,
     * die einer GameKey-Konstante zugeordnet ist. Die Implementierung dieser
     * Methode
     * sollte die gewünschte Reaktion auf den Tastendruck definieren.
     * </p>
     *
     * @param manager Der InputManager, der das Ereignis ausgelöst hat. Kann
     *                verwendet werden,
     *                um den Status anderer Tasten abzufragen (z.B.
     *                manager.isKeyDown(GameKey.LEFT))
     * @param key     Die GameKey-Konstante, die der gedrückten Taste entspricht
     */
    void keyDown(final InputManager manager, final GameKey key);

    /**
     * Wird aufgerufen, wenn eine Taste losgelassen wird.
     *
     * <p>
     * Diese Methode wird vom InputManager aufgerufen, wenn der Benutzer eine Taste
     * loslässt,
     * die einer GameKey-Konstante zugeordnet ist. Die Implementierung dieser
     * Methode
     * sollte die gewünschte Reaktion auf das Loslassen der Taste definieren.
     * </p>
     *
     * @param manager Der InputManager, der das Ereignis ausgelöst hat. Kann
     *                verwendet werden,
     *                um den Status anderer Tasten abzufragen (z.B.
     *                manager.isKeyDown(GameKey.LEFT))
     * @param key     Die GameKey-Konstante, die der losgelassenen Taste entspricht
     */
    void keyUp(final InputManager manager, final GameKey key);

    
}
