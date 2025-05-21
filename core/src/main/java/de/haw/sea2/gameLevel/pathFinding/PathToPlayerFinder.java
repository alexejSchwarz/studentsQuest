package de.haw.sea2.gameLevel.pathFinding;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Pathfinder using A* algorithm and a Grid based System to guide Enemy Entities to the player position.
 * This class finds paths on a pre-built NavigationGrid.
 */
public class PathToPlayerFinder {

    // Grid-based navigation constants
    public static final float PATH_NODE_PROXIMITY = 0.2f; // Increased proximity to make waypoint transitions smoother

    // Pathfinding related objects
    private final NavigationGrid navGrid;
    private final IndexedAStarPathFinder<GridNode> pathFinder;

    // private final EnemyMovementDebugRenderer debugRenderer;


    /**
     * Constructs a PathToPlayerFinder that operates on the given NavigationGrid.
     * @param navGrid The pre-built navigation grid to use for pathfinding.
     */
    public PathToPlayerFinder(NavigationGrid navGrid) {
        if (navGrid == null) {
            throw new IllegalArgumentException("NavigationGrid cannot be null.");
        }
        this.navGrid = navGrid;
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
}
