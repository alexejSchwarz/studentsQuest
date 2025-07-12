package de.haw.sea2.logic.entityLogic;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;

import de.haw.sea2.logic.ecs.components.EnemyComponent;

public enum EnemyState implements State<EnemyComponent> {
    WAS_JUST_HIT() {
        @Override
        public void update(EnemyComponent enemyComponent) {
            if (enemyComponent.hitTimer <= 0) {
                enemyComponent.hitTimer = 1f;
                enemyComponent.stateMachine.changeState(NOT_HIT);
            }
        }
    },
    NOT_HIT;

    @Override
    public void enter(EnemyComponent enemyComponent) {

    }

    @Override
    public void update(EnemyComponent enemyComponent) {

    }

    @Override
    public void exit(EnemyComponent enemyComponent) {

    }

    @Override
    public boolean onMessage(EnemyComponent enemyComponent, Telegram telegram) {
        return false;
    }
}
