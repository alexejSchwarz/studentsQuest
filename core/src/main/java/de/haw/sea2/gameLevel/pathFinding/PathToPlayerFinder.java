package de.haw.sea2.gameLevel.pathFinding;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import de.haw.sea2.StudentsQuest;
import de.haw.sea2.debug.LogCategory;
import de.haw.sea2.debug.LoggerUtil;
import de.haw.sea2.ecs.ECSEngine;
import de.haw.sea2.ecs.components.Box2DComponent;
import de.haw.sea2.ecs.components.ObstacleComponent;
import de.haw.sea2.map.GameMap;

/**
 * Pathfinder using A* algorithm and a Grid based System to guide Enemy Entities to the player position
 */
public class PathToPlayerFinder {

    // Grid-based navigation constants
    public static final float PATH_NODE_PROXIMITY = 0.2f; // Increased proximity to make waypoint transitions smoother
    private static final float GRID_CELL_SIZE = 1.0f; // Size of each grid cell in world units

    private static GameMap MAP;
    private static ImmutableArray<Entity> WALLS;

    // Pathfinding related objects
    private NavigationGrid navGrid;
    private IndexedAStarPathFinder<GridNode> pathFinder;

    // private final EnemyMovementDebugRenderer debugRenderer;



    public PathToPlayerFinder() {
        buildNavigationGrid();
    }

    public static void SetMap(GameMap map, StudentsQuest context) {
        MAP = map;
        WALLS = context.getEngine().getEntitiesFor(Family.all(ObstacleComponent.class, Box2DComponent.class).get());

        //TODO REFACTORING? schauen bzw. init und Benachrichtigungen festhalten, ueberarbeiten / in Struktur bringen.
        context.getEngine().getBatchMovementSystem().onPathfindingInit();
    }

    /**
     * Builds the navigation grid from the current MAP's collision areas
     */
    private void buildNavigationGrid() {

        // Determine MAP boundaries
        int gridWidth = MAP.getTiledMap().getProperties().get("width", Integer.class);
        int gridHeight = MAP.getTiledMap().getProperties().get("height", Integer.class);

        LoggerUtil.log(LogCategory.DEBUG, this,
            "Creating navigation grid: " + gridWidth + "x" + gridHeight +
                " cells, worldBounds: (" + 0f + "," + 0f + ") to (" + gridWidth + "," + gridHeight + ")");

        this.navGrid = new NavigationGrid(gridWidth, gridHeight, GRID_CELL_SIZE, Vector2.Zero);

        Array<Rectangle> obstacles = new Array<>();
        for (Entity wall : WALLS) {
            Box2DComponent box2DComponent = ECSEngine.BOX2D_COMP_MAPPER.get(wall);
            Vector2 bodyCenterPos = box2DComponent.body.getPosition();
            float bodyWidth = box2DComponent.width;
            float bodyHeigth = box2DComponent.height;
            obstacles.add(new Rectangle(bodyCenterPos.x - 0.5f * bodyWidth, bodyCenterPos.y - 0.5f * bodyHeigth, bodyWidth, bodyHeigth));
        }

        // Mark obstacle cells as unwalkable and create connections between walkable paths
        this.navGrid.markObstaclesAndCreateConnections(obstacles);

        // Create the pathfinder with our navigation grid
        this.pathFinder = new IndexedAStarPathFinder<>(this.navGrid);
    }

    public Array<Vector2> getWayPoints(Vector2 enemyPos, Vector2 playerPos) {
        GridNode from = this.navGrid.getNodeAtWorldPosition(enemyPos);
        GridNode to = this.navGrid.getNodeAtWorldPosition(playerPos);
        GraphPath<GridNode> graphPath = new DefaultGraphPath<>();
        if (this.pathFinder.searchNodePath(from, to, new ManhattanDistance(), graphPath)) {
            if (graphPath.getCount() == 0) {
                return new Array<>();
            }
          return graphPathToVectorPath(graphPath);
        }
        return new Array<>();
    }

    private Array<Vector2> graphPathToVectorPath(GraphPath<GridNode> graphPath) {
        // Convert the path to world coordinates
        Array<Vector2> worldPath = new Array<>();

        for (int i = 0; i < graphPath.getCount(); i++) {
            GridNode node = graphPath.get(i);
            Vector2 worldPos = navGrid.getWorldPosition(node);

            // Skip the first node (current position)
            if (i > 0) {
                worldPath.add(new Vector2(worldPos));
            }
        }
        return worldPath;
    }

    /**
     * Get access to the navigation grid for debug visualization
     */
    public NavigationGrid getNavigationGrid() {
        return navGrid;
    }
}
