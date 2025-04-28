package de.haw.sea2.gameLevel.pathFinding;

import java.util.Objects;

import com.badlogic.gdx.ai.pfa.Connection;

/**
 * A connection between two grid nodes for A* pathfinding
 */
public class GridConnection implements Connection<GridNode> {
    private final GridNode fromNode;
    private final GridNode toNode;
    private final float cost;

    public GridConnection(GridNode fromNode, GridNode toNode) {
        this.fromNode = fromNode;
        this.toNode = toNode;

        // Use Manhattan distance as cost (all horizontal/vertical moves cost 1)
        this.cost = 1;
    }

    @Override
    public float getCost() {
        return cost;
    }

    @Override
    public GridNode getFromNode() {
        return fromNode;
    }

    @Override
    public GridNode getToNode() {
        return toNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GridConnection that)) return false;
        return Float.compare(cost, that.cost) == 0 && Objects.equals(fromNode, that.fromNode) && Objects.equals(toNode, that.toNode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromNode, toNode, cost);
    }
}
