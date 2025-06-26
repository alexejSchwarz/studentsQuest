package de.haw.sea2.logic.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.utils.Pool;

import de.haw.sea2.logic.ecs.ECSEngine;
import de.haw.sea2.logic.entityLogic.PlayerAttackState;

/**
 * Component fuer libgdxai Statemachine des Spielers. Weitere Infos siehe: <a href="https://github.com/libgdx/gdx-ai/wiki/State-Machine">...</a>
 */
public class PlayerAttackStateComponent implements Component, Pool.Poolable {

    public StateMachine<PlayerAttackStateComponent, PlayerAttackState> stateMachine = new DefaultStateMachine<>(this, PlayerAttackState.NOT_ATTACKING);
    public ECSEngine engine;
    public float attackCoolDown = 0.75f;
    public float attackDuration = 0.25f;
    public PlayerComponent.Sensors activatedSensor = PlayerComponent.Sensors.NONE;

    @Override
    public void reset() {
        this.attackCoolDown = 1f;
        this.attackDuration = 0.25f;
        this.activatedSensor = PlayerComponent.Sensors.NONE;
    }

    public void update(float deltaTime) {
        if (stateMachine.isInState(PlayerAttackState.ATTACKING)) {
            this.attackDuration -= deltaTime;
        } else if (stateMachine.isInState(PlayerAttackState.COOL_DOWN)) {
            this.attackCoolDown -= deltaTime;
        }
        this.stateMachine.update();
    }
}
