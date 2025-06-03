package de.haw.sea2.logic.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool;

/**
 * Eine Komponente, die physikalische Eigenschaften einer Entität durch
 * Box2D-Integration verwaltet.
 * Diese Komponente stellt die Verbindung zu der Box2D-Physik-Engine her und
 * ermöglicht
 * physikalische Simulationen wie Kollisionen, Bewegungen mit Trägheit und
 * physikbasierte
 * Interaktionen zwischen Entitäten.
 *
 * <p>
 * Die Komponente enthält einen Box2D-Body, der die physikalischen Eigenschaften
 * repräsentiert,
 * sowie Informationen über die Breite und Höhe der physikalischen Form.
 * </p>
 *
 * <p>
 * Als {@link Pool.Poolable} implementiert diese Komponente Object-Pooling,
 * was die Speicherverwaltung effizienter gestaltet, indem Objekte
 * wiederverwendet werden
 * statt sie neu zu erstellen.
 * </p>
 */
public class Box2DComponent implements Component, Pool.Poolable {

    /**
     * Der Box2D-Body, der die physikalische Repräsentation der Entität darstellt.
     * Dieser Body enthält Informationen zu Position, Rotation, Geschwindigkeit,
     * Masse und anderen physikalischen Eigenschaften.
     */
    public Body body;

    /**
     * Die Breite der physikalischen Form in Box2D-Einheiten.
     * Wird für Berechnungen der Kollisionsform und Visualisierung verwendet.
     */
    public float width;

    /**
     * Die Höhe der physikalischen Form in Box2D-Einheiten.
     * Wird für Berechnungen der Kollisionsform und Visualisierung verwendet.
     */
    public float height;

    /**
     * incorporates the alpha value for smoother rendering
     */
    public Vector2 interpolatedRenderPosition = new Vector2();

    public float previousX;
    public float previousY;

    /**
     * Setzt die Komponente auf ihren Standardzustand zurück.
     *
     * <p>
     * Diese Methode wird aufgerufen, wenn die Komponente in den Pool zurückgegeben
     * wird.
     * Sie entfernt den Body aus der Box2D-Welt, falls er existiert, um
     * Speicherlecks zu vermeiden
     * und Memory-Management-Probleme zu verhindern. Außerdem werden die Dimensionen
     * zurückgesetzt.
     * </p>
     *
     * <p>
     * Es ist wichtig, dass diese Methode aufgerufen wird, bevor eine Komponente
     * wiederverwendet wird,
     * um sicherzustellen, dass keine Referenzen auf alte Physik-Objekte bestehen
     * bleiben.
     * </p>
     */
    @Override
    public void reset() {
        if (body != null) {
            // removes body from Box2DWorld
            this.body.getWorld().destroyBody(this.body);
            this.body = null;
            this.interpolatedRenderPosition.set(0f, 0f);
            this.previousX = 0f;
            this.previousY = 0f;
        }

        this.width = 0f;
        this.height = 0f;
    }
}
