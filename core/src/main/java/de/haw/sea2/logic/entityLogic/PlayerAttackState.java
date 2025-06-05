package de.haw.sea2.logic.entityLogic;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;

import de.haw.sea2.debug.LogCategory;
import de.haw.sea2.debug.LoggerUtil;
import de.haw.sea2.logic.EntityUtils;
import de.haw.sea2.logic.ecs.ECSEngine;
import de.haw.sea2.logic.ecs.builders.FixtureBuilder;
import de.haw.sea2.logic.ecs.components.PlayerAttackStateComponent;

/**
 * State Enum fuer den Spieler. Hier werden die States definiert, die dann von StateMachine verwaltet werden. Es gibt globale Methoden, sowie die Moeglichkeit
 * in den States selbst (den Enum Values), eigene Logik zu definieren, indem man die Methoden der State<T> Interface ueberschreibt.
 * Weitere Infos zu Statemachine siehe in wiki von Libgdx ai: <a href="https://github.com/libgdx/gdx-ai/wiki/State-Machine">...</a>
 */
public enum PlayerAttackState implements State<PlayerAttackStateComponent> {

    ATTACKING() {
        @Override
        public void update(PlayerAttackStateComponent playerStateComponent) {
            if (playerStateComponent.attackDuration <= 0f) {
                playerStateComponent.stateMachine.changeState(COOL_DOWN);
            }
        }

        @Override
        public void enter(PlayerAttackStateComponent playerAttackStateComponent) {
            LoggerUtil.log(LogCategory.GAME, this, "Player entered State: " + this.name() + " with attack direction: " + playerAttackStateComponent.activatedSensor);
        }

        @Override
        public void exit(PlayerAttackStateComponent playerAttackStateComponent) {
            Entity player = EntityUtils.checkAndGetPlayer(playerAttackStateComponent.engine.getEntitiesFor(Family.all(PlayerAttackStateComponent.class).get()));
            FixtureBuilder.destroyPlayerSensor(ECSEngine.BOX2D_COMP_MAPPER.get(player));
            playerAttackStateComponent.reset();
        }
    },
    COOL_DOWN() {
        @Override
        public void update(PlayerAttackStateComponent playerStateComponent) {
            if (playerStateComponent.attackCoolDown <= 0f) {
                playerStateComponent.stateMachine.changeState(NOT_ATTACKING);
            }
        }
    },
    NOT_ATTACKING() {
        @Override
        public void update(PlayerAttackStateComponent playerAttackStateComponent) {}
    };

    @Override
    public void enter(PlayerAttackStateComponent playerAttackStateComponent) {
        LoggerUtil.log(LogCategory.GAME, this, "Player entered State: " + this.name());
    }


    @Override
    public void exit(PlayerAttackStateComponent playerAttackStateComponent) {
        //playerAttackStateComponent.reset();
    }

    @Override
    public boolean onMessage(PlayerAttackStateComponent playerAttackStateComponent, Telegram telegram) {
        return false;
    }
}
