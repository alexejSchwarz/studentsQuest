package de.haw.sea2.ecs.entityLogic;

import com.badlogic.gdx.math.Vector2;

public enum EnemyMovementDirection {
    UP, DOWN, LEFT, RIGHT;

    /**
     * Vector.Zero is not allowed here, as it does not correspond to a Movement Direction
     */
    public static EnemyMovementDirection getMovementDirectionFromVectorForEnemies(Vector2 vector2) {
        if (vector2.equals(Vector2.Zero)) {
            throw new IllegalArgumentException();
        }

        Vector2 v = new Vector2(vector2);
        v.nor();
        boolean lowXValue = Math.abs(v.x) < 0.1f;

        if (lowXValue && v.y != 0) {
            return v.y < 0 ? DOWN : UP;
        } else {
            return v.x < 0 ? LEFT : RIGHT;
        }
    }
}
