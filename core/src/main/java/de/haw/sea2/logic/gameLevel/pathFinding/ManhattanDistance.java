package de.haw.sea2.logic.gameLevel.pathFinding;

/**
 * Heuristic for A* pathfinding that calculates Manhattan distance
 */
public class ManhattanDistance implements com.badlogic.gdx.ai.pfa.Heuristic<GridNode> {
    @Override
    public float estimate(GridNode node, GridNode endNode) {
        return Math.abs(endNode.x - node.x) + Math.abs(endNode.y - node.y);
    }
}
