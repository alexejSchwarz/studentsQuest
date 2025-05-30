package de.haw.sea2.gameLevel.pathFinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * A grid-based navigation system for A* pathfinding
 */
public class NavigationGrid implements IndexedGraph<GridNode> {
    private final int width, height;
    private final float cellSize;
    private final Vector2 worldOrigin;
    private final GridNode[][] nodes;
    private final ObjectMap<GridNode, Array<GridConnection>> fromNodesConnectionMap;

    public NavigationGrid(int width, int height, float cellSize, Vector2 worldOrigin) {
        this.width = width;
        this.height = height;
        this.cellSize = cellSize;
        this.worldOrigin = new Vector2(worldOrigin);
        this.fromNodesConnectionMap = new ObjectMap<>();

        // Initialize all grid nodes as walkable
        nodes = new GridNode[width][height];
        int index = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                nodes[x][y] = new GridNode(x, y, index, true);
                index++;
            }
        }
    }

    public void markObstaclesAndCreateConnections(Array<Rectangle> obstacles) {
        for (Rectangle obstacle: obstacles) {
            markObstacle(obstacle);
        }
        createConnections();
    }

    /**
     * Mark an obstacle in the grid
     */
    private void markObstacle(Rectangle obstacle) {
        // Convert obstacle world bounds to grid coordinates
        //TODO bei casting wird abgerundet. Mehr Toleranz zwischen Editor und Obstacle?
        //TODO was wenn Obstacle in mitte von (0,0) und (1,0) liegt. Werden bei besetzt?
        int minX = Math.max(0, (int)((obstacle.x - worldOrigin.x) / cellSize));
        int minY = Math.max(0, (int)((obstacle.y - worldOrigin.y) / cellSize));
        int maxX = Math.min(width-1, (int)((obstacle.x + obstacle.width - worldOrigin.x) / cellSize));
        int maxY = Math.min(height-1, (int)((obstacle.y + obstacle.height - worldOrigin.y) / cellSize));

        // Mark all grid cells that overlap with the obstacle as unwalkable
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                nodes[x][y].walkable = false;
            }
        }
    }

    /**
     * Create connections between adjacent grid nodes
     */
    private void createConnections() {
        // Create connections for 8-way movement (including diagonals)
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                GridNode node = nodes[x][y];

                if (!node.walkable) continue; // Skip unwalkable nodes

                // Add connections to adjacent nodes if they're walkable
                // Orthogonal connections (4-way)
                boolean canMoveLeft = x > 0 && nodes[x-1][y].walkable;
                boolean canMoveRight = x < width-1 && nodes[x+1][y].walkable;
                boolean canMoveDown = y > 0 && nodes[x][y-1].walkable;
                boolean canMoveUp = y < height-1 && nodes[x][y+1].walkable;

                if (canMoveLeft) {
                    getOrCreateGridConnections(node).add(new GridConnection(node, nodes[x-1][y]));
                }

                if (canMoveRight) {
                    getOrCreateGridConnections(node).add(new GridConnection(node, nodes[x+1][y]));
                }

                if (canMoveDown) {
                    getOrCreateGridConnections(node).add(new GridConnection(node,nodes[x][y-1]));
                }

                if (canMoveUp) {
                    getOrCreateGridConnections(node).add(new GridConnection(node, nodes[x][y+1]));
                }

                // Diagonal connections - only add if the path isn't blocked
                // Bottom-left diagonal is only possible if both left and down are walkable
                if (x > 0 && y > 0 && nodes[x-1][y-1].walkable && canMoveLeft && canMoveDown) {
                    getOrCreateGridConnections(node).add(new GridConnection(node, nodes[x-1][y-1]));
                }

                // Bottom-right diagonal is only possible if both right and down are walkable
                if (x < width-1 && y > 0 && nodes[x+1][y-1].walkable && canMoveRight && canMoveDown) {
                    getOrCreateGridConnections(node).add(new GridConnection(node, nodes[x+1][y-1]));
                }

                // Top-left diagonal is only possible if both left and up are walkable
                if (x > 0 && y < height-1 && nodes[x-1][y+1].walkable && canMoveLeft && canMoveUp) {
                    getOrCreateGridConnections(node).add(new GridConnection(node, nodes[x-1][y+1]));
                }

                // Top-right diagonal is only possible if both right and up are walkable
                if (x < width-1 && y < height-1 && nodes[x+1][y+1].walkable && canMoveRight && canMoveUp) {
                    getOrCreateGridConnections(node).add(new GridConnection(node, nodes[x+1][y+1]));
                }
            }
        }
    }

    private Array<GridConnection> getOrCreateGridConnections(GridNode from) {
        if (this.fromNodesConnectionMap.containsKey(from)) {
            return this.fromNodesConnectionMap.get(from);
        }
        Array<GridConnection> connections = new Array<>();
        this.fromNodesConnectionMap.put(from, connections);
        return connections;
    }

    /**
     * Find the grid node at a world position
     */
    public GridNode getNodeAtWorldPosition(Vector2 worldPos) {
        int gridX = (int)((worldPos.x - worldOrigin.x) / cellSize);
        int gridY = (int)((worldPos.y - worldOrigin.y) / cellSize);

        if (gridX >= 0 && gridX < width && gridY >= 0 && gridY < height) {
            return nodes[gridX][gridY];
        }
        return null;
    }

    /**
     * Get a grid node at specific coordinates
     */
    public GridNode getNode(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return nodes[x][y];
        }
        return null;
    }

    /**
     * Convert a grid node to a world position (centre of the node)
     */
    public Vector2 getWorldPosition(GridNode node) {
        return new Vector2(
            worldOrigin.x + (node.x + 0.5f) * cellSize,
            worldOrigin.y + (node.y + 0.5f) * cellSize
        );
    }

    /**
     * Get the width of the grid in cells
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the height of the grid in cells
     */
    public int getHeight() {
        return height;
    }

    @Override
    public int getIndex(GridNode node) {
        return node.index;
    }

    @Override
    public int getNodeCount() {
        return width * height;
    }

    @Override
    public Array<Connection<GridNode>> getConnections(GridNode fromNode) {
        Array<Connection<GridNode>> nodeConnections = new Array<>();
        nodeConnections.addAll(getOrCreateGridConnections(fromNode));
        return nodeConnections;
    }
}
