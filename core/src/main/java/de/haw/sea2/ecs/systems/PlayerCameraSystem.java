package de.haw.sea2.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.ecs.ECSEngine;
import de.haw.sea2.ecs.components.Box2DComponent;
import de.haw.sea2.ecs.components.PlayerComponent;

/**
 * Ein System, das die Kamera auf den Spieler zentriert.
 *
 * <p>
 * Als Teil des Entity-Component-Systems (ECS) verarbeitet dieses System
 * alle Entitäten, die sowohl eine PlayerComponent als auch eine Box2DComponent
 * besitzen.
 * Die Position der Spielkamera wird kontinuierlich aktualisiert, um der
 * Position
 * des Spielers zu folgen.
 * </p>
 *
 * <p>
 * Dies ermöglicht eine spielerzentrierte Ansicht der Spielwelt, bei der
 * sich der Spielercharakter stets in der Mitte des Bildschirms befindet,
 * während sich die Kamera durch die Welt bewegt.
 * </p>
 */
public class PlayerCameraSystem extends IteratingSystem {

    /**
     * Die Spielkamera, deren Position aktualisiert wird.
     * Diese orthografische Kamera wird verwendet, um die 2D-Spielwelt anzuzeigen.
     */
    private OrthographicCamera gameCamera;

    /**
     * Erstellt ein neues PlayerCameraSystem.
     *
     * @param context Der StudentsQuest-Kontext, der Zugriff auf die Spielkamera
     *                bietet.
     */
    public PlayerCameraSystem(StudentsQuest context) {
        super(Family.all(PlayerComponent.class, Box2DComponent.class).get());
        this.gameCamera = context.getGameCamera();
    }

    /**
     * Verarbeitet eine einzelne Entität und aktualisiert die Kameraposition.
     *
     * <p>
     * Diese Methode wird für jede Entität aufgerufen, die sowohl PlayerComponent
     * als auch Box2DComponent besitzt. Sie setzt die Position der Kamera auf die
     * aktuelle Position des Box2D-Körpers der Spielerentität.
     * </p>
     *
     * @param entity    Die zu verarbeitende Entität (der Spieler).
     * @param deltaTime Die Zeit seit dem letzten Update in Sekunden.
     */
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Box2DComponent box2DComponent = ECSEngine.BOX2D_COMP_MAPPER.get(entity);
        gameCamera.position.set(box2DComponent.interpolatedRenderPosition, 0);
    }
}
