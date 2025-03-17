package de.haw.sea2.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import com.badlogic.gdx.math.Vector2;
import de.haw.sea2.StudentsQuest;
import de.haw.sea2.ecs.ECSEngine;
import de.haw.sea2.ecs.components.Box2DComponent;
import de.haw.sea2.ecs.components.PlayerComponent;
import de.haw.sea2.input.GameKey;
import de.haw.sea2.input.InputManager;
import de.haw.sea2.input.KeyInputListener;

/**
 * Dieses System verwaltet die Bewegung des Spielers basierend auf
 * Tasteneingaben.
 * Es verarbeitet alle Entitäten, die einen PlayerComponent und einen
 * Box2DComponent besitzen.
 * Durch Tastendruck (keyDown) und das Loslassen von Tasten (keyUp) werden
 * Messwerte gesammelt,
 * die eine Änderung der Bewegungsrichtung anzeigen. Im Rahmen des
 * IteratingSystems wird dann
 * mittels eines linearen Impulses die Bewegung der Entität angepasst.
 */
public class PlayerMovementSystem extends IteratingSystem implements KeyInputListener {

    /**
     * Horizontaler Richtungsfaktor (-1 für links, 1 für rechts, 0 für keine
     * Bewegung).
     */
    private int xFactor;
    /**
     * Vertikaler Richtungsfaktor (1 für nach oben, -1 für nach unten, 0 für keine
     * Bewegung).
     */
    private int yFactor;

    /**
     * Konstruktor des PlayerMovementSystem.
     * Registriert das System als KeyInputListener im InputManager des Kontextes.
     *
     * @param context Der StudentsQuest Kontext, der das InputManager-Objekt
     *                enthält.
     */
    public PlayerMovementSystem(final StudentsQuest context) {
        super(Family.all(PlayerComponent.class, Box2DComponent.class).get());
        context.getInputManager().addKeyInputListener(this);
        this.xFactor = this.yFactor = 0;
    }

    /**
     * Diese Methode wird für jede Entität innerhalb der zu verarbeitenden Familie
     * aufgerufen.
     * Wenn eine Richtungsänderung (directionChange) registriert wurde, wird ein
     * linearer Impuls
     * auf den Box2D-Körper der Entität angewendet. Dieser Impuls basiert auf dem
     * aktuellen
     * Geschwindigkeitsfaktor und der Differenz zwischen der gewünschten und der
     * aktuellen Geschwindigkeit.
     *
     * @param entity    Die aktuelle Entität, die verarbeitet wird.
     * @param deltaTime Die vergangene Zeit seit dem letzten Aufruf (in Sekunden).
     */
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        // Zugriff auf die Komponenten der Entität mittels schneller Mappers
        final PlayerComponent playerComponent = ECSEngine.PLAYER_COMP_MAPPER.get(entity);
        final Box2DComponent physicsBox2dComponent = ECSEngine.BOX2D_COMP_MAPPER.get(entity);

        // Erstellen eines Richtungsvektors basierend auf den Eingabefaktoren
        Vector2 movementDirection = new Vector2(this.xFactor, this.yFactor);

        // Falls sich das Objekt diagonal bewegt, wird die Richtung normalisiert
        if (movementDirection.x != 0 && movementDirection.y != 0) {
            movementDirection.nor();  // Normalisiert den Vektor auf Länge 1
        }

        // Berechnung des Impulses unter Berücksichtigung der aktuellen Geschwindigkeit
        float impulseX = movementDirection.x * playerComponent.speed.x
            - physicsBox2dComponent.body.getLinearVelocity().x * physicsBox2dComponent.body.getMass();

        float impulseY = movementDirection.y * playerComponent.speed.y
            - physicsBox2dComponent.body.getLinearVelocity().y * physicsBox2dComponent.body.getMass();

        // Anwenden des Impulses auf den Körper für physikalische Bewegung
        physicsBox2dComponent.body.applyLinearImpulse(
            impulseX,
            impulseY,
            physicsBox2dComponent.body.getWorldCenter().x,
            physicsBox2dComponent.body.getWorldCenter().y,
            true
        );
    }


    /**
     * Verarbeitet Tastendruck-Ereignisse.
     * Setzt das directionChange-Flag und passt den x- bzw. y-Faktor je nach
     * gedrückter Taste an. Verwendet die GameKey-Enumeration. @see GameKey
     *
     * @param manager Der InputManager, der dieses Ereignis verarbeitet.
     * @param key     Die spezifische Taste, die gedrückt wurde.
     */
    @Override
    public void keyDown(InputManager manager, GameKey key) {
        switch (key) {
            case LEFT: {
                this.xFactor = -1;
                break;
            }
            case RIGHT: {
                this.xFactor = 1;
                break;
            }
            case UP: {
                this.yFactor = 1;
                break;
            }
            case DOWN: {
                this.yFactor = -1;
                break;
            }
            default: {
                break;
            }
        }
    }

    /**
     * Verarbeitet Tast losgelassen-Ereignisse.
     * Passt den Bewegungsfaktor so an, dass, wenn eine Taste losgelassen wird,
     * die Bewegung entweder gestoppt oder auf den Zustand der gegenüberliegenden
     * Taste
     * (falls gedrückt) umgestellt wird.
     *
     * @param manager Der InputManager, der dieses Ereignis verarbeitet.
     * @param key     Die spezifische Taste, die losgelassen wurde.
     */
    @Override
    public void keyUp(InputManager manager, GameKey key) {
        switch (key) {
            case LEFT: {
                this.xFactor = manager.isKeyDown(GameKey.RIGHT) ? 1 : 0;
                break;
            }
            case RIGHT: {
                this.xFactor = manager.isKeyDown(GameKey.LEFT) ? -1 : 0;
                break;
            }
            case UP: {
                this.yFactor = manager.isKeyDown(GameKey.DOWN) ? -1 : 0;
                break;
            }
            case DOWN: {
                this.yFactor = manager.isKeyDown(GameKey.UP) ? 1 : 0;
                break;
            }
            default: {
                break;
            }
        }
    }
}
