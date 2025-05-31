package de.haw.sea2.logic.gameLevel.pathFinding;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.debug.LogCategory;
import de.haw.sea2.debug.LoggerUtil;
import de.haw.sea2.logic.ecs.ECSEngine;
import de.haw.sea2.logic.ecs.components.Box2DComponent;
import de.haw.sea2.logic.ecs.components.ObstacleComponent;
import de.haw.sea2.map.GameMap;

/**
 * Utility-Klasse zum Erstellen von NavigationGrid-Objekten aus verschiedenen Quellen.
 * Abstrahiert die Erstellung von NavigationGrids und sammelt diese Funktionalität an einem zentralen Ort.
 */
public class NavigationGridBuilder {

    private static final float DEFAULT_GRID_CELL_SIZE = 1.0f; // Size of each grid cell in world units

    /**
     * Erstellt eine neue NavigationGrid für eine gegebene GameMap.
     * Die Größe der NavigationGrid wird aus den Eigenschaften der Karte abgeleitet.
     * Hindernisse werden aus den ObstacleComponent-Entitäten extrahiert.
     *
     * @param context Der StudentsQuest-Kontext, um Zugriff auf Entitäten zu erhalten
     * @param map Die GameMap, für die eine NavigationGrid erstellt werden soll
     * @return Eine neue NavigationGrid, die für die angegebene Karte konfiguriert ist
     */
    public static NavigationGrid buildForMap(StudentsQuest context, GameMap map) {
        int gridWidth = map.getTiledMap().getProperties().get("width", Integer.class);
        int gridHeight = map.getTiledMap().getProperties().get("height", Integer.class);

        String mapIdentifier = map.getMapIdentifier();
        LoggerUtil.log(LogCategory.DEBUG, NavigationGridBuilder.class,
            "Creating new navigation grid: " + gridWidth + "x" + gridHeight +
                " cells, for map: " + mapIdentifier);

        NavigationGrid navigationGrid = new NavigationGrid(gridWidth, gridHeight, DEFAULT_GRID_CELL_SIZE, Vector2.Zero);
        Array<Rectangle> obstacles = collectObstaclesFromEntities(context);
        navigationGrid.markObstaclesAndCreateConnections(obstacles);

        return navigationGrid;
    }

    /**
     * Sammelt alle Hindernisse aus Entitäten mit ObstacleComponent und Box2DComponent.
     *
     * @param context Der StudentsQuest-Kontext, um Zugriff auf Entitäten zu erhalten
     * @return Ein Array von Rechtecken, die die Hindernisse repräsentieren
     */
    private static Array<Rectangle> collectObstaclesFromEntities(StudentsQuest context) {
        // Hole alle Entitäten mit ObstacleComponent und Box2DComponent
        ImmutableArray<Entity> currentWalls = context.getEngine().getEntitiesFor(
            Family.all(ObstacleComponent.class, Box2DComponent.class).get());

        Array<Rectangle> obstacles = new Array<>();
        for (Entity wall : currentWalls) {
            Box2DComponent box2DComponent = ECSEngine.BOX2D_COMP_MAPPER.get(wall);
            if (box2DComponent != null && box2DComponent.body != null) {
                Vector2 bodyCenterPos = box2DComponent.body.getPosition();
                float bodyWidth = box2DComponent.width;
                float bodyHeight = box2DComponent.height;
                obstacles.add(new Rectangle(
                    bodyCenterPos.x - 0.5f * bodyWidth,
                    bodyCenterPos.y - 0.5f * bodyHeight,
                    bodyWidth, bodyHeight));
            } else {
                LoggerUtil.log(LogCategory.DEBUG, NavigationGridBuilder.class,
                    "Wall entity or its Box2DComponent/body is null, skipping for navgrid generation.");
            }
        }

        return obstacles;
    }
}
