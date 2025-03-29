package de.haw.sea2.gameLevel.pathFinding;

/**
 * A node in the navigation grid
 */
public class GridNode {
    int x, y;           // Grid coordinates
    int index;          // Unique index for this node
    boolean walkable;   // Whether this node can be walked on

    public GridNode(int x, int y, int index, boolean walkable) {
        this.x = x;
        this.y = y;
        this.index = index;
        this.walkable = walkable;
    }

    public boolean isWalkable() {
        return walkable;
    }

    @Override
    public String toString() {
        return "GridNode[" + x + "," + y + ",walkable=" + walkable + "]";
    }
}
