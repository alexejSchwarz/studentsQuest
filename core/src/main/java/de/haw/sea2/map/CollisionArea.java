package de.haw.sea2.map;

import com.badlogic.gdx.math.Vector2;

import de.haw.sea2.StudentsQuest;

public class CollisionArea {
    private final float x;
    private final float y;
    private final float[] vertices;

    public CollisionArea(final float x, final float y, float[] vertices) {
        this.x = x * StudentsQuest.UNIT_SCALE;
        this.y = y * StudentsQuest.UNIT_SCALE;
        this.vertices = vertices;
        for(int i = 0; i < vertices.length; i += 2) {
            vertices[i] = vertices[i] * StudentsQuest.UNIT_SCALE;
            vertices[i + 1] = vertices[i + 1] * StudentsQuest.UNIT_SCALE;
        }
    }


    public float[] getVertices() {
        return vertices;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
