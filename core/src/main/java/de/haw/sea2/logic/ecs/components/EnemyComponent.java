package de.haw.sea2.logic.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import de.haw.sea2.logic.entityLogic.EnemyState;
import de.haw.sea2.logic.entityLogic.PlayerAttackState;

public class EnemyComponent implements Component, Pool.Poolable {

    public Vector2 speed = new Vector2();

    /** Time between pathfinding recalculations */
    public float pathUpdateInterval = 0.5f;

    /** Timer to track when to update path */
    public float pathUpdateTimer = 0;

    public Array<Vector2> waypoints = new Array<>();

    public float hitTimer = 1f;

    public DefaultStateMachine<EnemyComponent, EnemyState> stateMachine = new DefaultStateMachine<>(this, EnemyState.NOT_HIT);

    @Override
    public void reset() {
        this.speed.setZero();
        this.pathUpdateTimer = 0f;
        this.pathUpdateInterval = 0.5f;
        this.waypoints = null;
        this.hitTimer = 1f;
        stateMachine.changeState(EnemyState.NOT_HIT);
    }

    public void update(float deltaTime) {
        if (this.stateMachine.isInState(EnemyState.WAS_JUST_HIT)) {
            this.hitTimer -= deltaTime;
        }
        this.stateMachine.update();
    }
}
