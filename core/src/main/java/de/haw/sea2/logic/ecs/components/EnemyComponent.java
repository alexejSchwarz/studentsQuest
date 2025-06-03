package de.haw.sea2.logic.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class EnemyComponent implements Component, Pool.Poolable {

    public Vector2 speed = new Vector2();

    /** Time between pathfinding recalculations */
    public float pathUpdateInterval = 0.5f;

    /** Timer to track when to update path */
    public float pathUpdateTimer = 0;

    public Array<Vector2> waypoints = new Array<>();

    @Override
    public void reset() {
        this.speed.setZero();
        this.pathUpdateTimer = 0f;
        this.pathUpdateInterval = 0.5f;
        this.waypoints = null;
    }
}
