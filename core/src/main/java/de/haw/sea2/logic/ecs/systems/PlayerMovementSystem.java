package de.haw.sea2.logic.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.logic.ecs.components.Box2DComponent;
import de.haw.sea2.logic.ecs.components.PlayerComponent;
import de.haw.sea2.logic.ecs.ECSEngine;
import de.haw.sea2.input.GameKey;
import de.haw.sea2.input.InputManager;
import de.haw.sea2.input.KeyInputListener;
import de.haw.sea2.logic.entityLogic.MovementDirection;

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

    // Alle Keys, die hier verarbeitet werden sollen (Teilmenge der definierten Keys in GameKey)
    private final ObjectSet<GameKey> keySet;

    // Map, die die aktuell guelltigen gedrueckten (also aktive) Tasten speichert. Gegenteile koennen nicht gleichzeitig aktiv sein (bsp. links rechts oder oben unten)
    private final ObjectMap<GameKey, Boolean> activeAllowedKeys;

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

    private MovementDirection facingDirection;

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
        this.facingDirection = MovementDirection.DOWN;
        this.keySet = ObjectSet.with(GameKey.UP, GameKey.DOWN, GameKey.LEFT, GameKey.RIGHT);

        this.activeAllowedKeys = new ObjectMap<>();
        for (GameKey key : this.keySet) {
            this.activeAllowedKeys.put(key, Boolean.FALSE);
        }
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

        playerComponent.curentFacing = this.facingDirection;

        // Erstellen eines Richtungsvektors basierend auf den Eingabefaktoren
        Vector2 movementDirection = new Vector2(this.xFactor, this.yFactor);

        // Falls sich das Objekt diagonal bewegt, wird die Richtung normalisiert
        if (movementDirection.x != 0 && movementDirection.y != 0) {
            movementDirection.nor(); // Normalisiert den Vektor auf Länge 1
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
                true);
    }

    /**
     * Verarbeitet Tastendruck-Ereignisse.
     * Setzt das directionChange-Flag und passt den x- bzw. y-Faktor je nach
     * gedrückter Taste an. Verwendet die GameKey-Enumeration. @see GameKey
     *
     * Links und Rechts haben Prioritaet in Setzung der FacingDirection und somit auch fuer die Animationsauswahl.
     * Dh. Diagonale Bewegung wird durch links oder rechts Animation abgebildet (sieht besser aus als oben oder unten)
     * Falls Diagonal als Facing und Animation implementiert wird, entfaellt diese Priorisierung
     *
     * @param manager Der InputManager, der dieses Ereignis verarbeitet.
     * @param key     Die spezifische Taste, die gedrückt wurde.
     */
    @Override
    public void keyDown(InputManager manager, GameKey key) {

        if (this.keySet.contains(key)) {
            handleKeyInput(key);
        }
    }

    private void handleKeyInput(GameKey key) {
        switch (key) {
            case LEFT: {
                // wenn Gegenrichtung gedrueckt ist, dann soll der Input ignoriert werden
                if (this.activeAllowedKeys.get(GameKey.RIGHT)) {
                    break;
                }
                this.xFactor = -1;
                this.facingDirection = MovementDirection.LEFT;
                this.activeAllowedKeys.put(key, true);
                break;
            }
            case RIGHT: {
                if (this.activeAllowedKeys.get(GameKey.LEFT)) {
                    break;
                }
                this.xFactor = 1;
                this.facingDirection = MovementDirection.RIGHT;
                this.activeAllowedKeys.put(key, true);
                break;
            }
            case UP: {
                if (this.activeAllowedKeys.get(GameKey.DOWN)) {
                    break;
                }
                this.activeAllowedKeys.put(key, true);
                this.yFactor = 1;

                //Priorisierung links / rechts Animation und Facing (damit auch Angriffsrichtung)
                if (this.activeAllowedKeys.get(GameKey.RIGHT) || this.activeAllowedKeys.get(GameKey.LEFT)) {
                    break;
                }
                this.facingDirection = MovementDirection.UP;
                break;
            }
            case DOWN: {
                if (this.activeAllowedKeys.get(GameKey.UP)) {
                    break;
                }
                this.activeAllowedKeys.put(key, true);
                this.yFactor = -1;

                if (this.activeAllowedKeys.get(GameKey.RIGHT) || this.activeAllowedKeys.get(GameKey.LEFT)) {
                    break;
                }
                this.facingDirection = MovementDirection.DOWN;
                break;
            }
            default: {
                throw new RuntimeException();
            }
        }

    }

    /**
     * Verarbeitet Taste losgelassen-Ereignisse.
     * Passt den Bewegungsfaktor so an, dass, wenn eine Taste losgelassen wird,
     * die Bewegung wird gestoppt. Falls bei diagonaler Bewegung links oder rechts losgelassen wird, dann wird Facing und Animation von Oben bzw. Unten uebernommen
     *
     * @param manager Der InputManager, der dieses Ereignis verarbeitet.
     * @param key     Die spezifische Taste, die losgelassen wurde.
     */
    @Override
    public void keyUp(InputManager manager, GameKey key) {

        if (!this.keySet.contains(key) || !this.activeAllowedKeys.get(key)) {
            return;
        }

        switch (key) {
            case LEFT: {
                this.xFactor = 0;
                this.activeAllowedKeys.put(key, false);

                if (manager.isKeyDown(GameKey.RIGHT)) { // um fluessig Richtung zu wechseln, kann ein vorher als nicht gueltig eingestufter Input wiederholt werden
                    handleKeyInput(GameKey.RIGHT);
                } else if (this.activeAllowedKeys.get(GameKey.UP)) {
                    this.facingDirection = MovementDirection.UP;
                } else if (this.activeAllowedKeys.get(GameKey.DOWN)) {
                    this.facingDirection = MovementDirection.DOWN;
                }
                break;
            }
            case RIGHT: {
                this.xFactor = 0;
                this.activeAllowedKeys.put(key, false);

                if (manager.isKeyDown(GameKey.LEFT)) {
                    handleKeyInput(GameKey.LEFT);
                } else if (this.activeAllowedKeys.get(GameKey.UP)) {
                    this.facingDirection = MovementDirection.UP;
                } else if (this.activeAllowedKeys.get(GameKey.DOWN)) {
                    this.facingDirection = MovementDirection.DOWN;
                }
                break;
            }
            case UP: {
                this.yFactor = 0;
                this.activeAllowedKeys.put(key, false);

                if (manager.isKeyDown(GameKey.DOWN)) {
                    handleKeyInput(GameKey.DOWN);
                }
                break;
            }
            case DOWN: {
                this.yFactor = 0;
                this.activeAllowedKeys.put(key, false);

                if (manager.isKeyDown(GameKey.UP)) {
                    handleKeyInput(GameKey.UP);
                }
                break;
            }
            default: {
                throw new RuntimeException();
            }
        }

    }
}
