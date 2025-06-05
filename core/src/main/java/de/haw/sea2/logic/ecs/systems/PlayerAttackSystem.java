package de.haw.sea2.logic.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.contact.SensorEnemyContactListener;
import de.haw.sea2.debug.LogCategory;
import de.haw.sea2.debug.LoggerUtil;
import de.haw.sea2.input.GameKey;
import de.haw.sea2.input.InputManager;
import de.haw.sea2.input.KeyInputListener;
import de.haw.sea2.logic.ecs.ECSEngine;
import de.haw.sea2.logic.ecs.builders.FixtureBuilder;
import de.haw.sea2.logic.ecs.components.Box2DComponent;
import de.haw.sea2.logic.ecs.components.HearthComponent;
import de.haw.sea2.logic.ecs.components.PlayerComponent;
import de.haw.sea2.logic.ecs.components.PlayerAttackStateComponent;
import de.haw.sea2.logic.ecs.components.RemoveComponent;
import de.haw.sea2.logic.entityLogic.PlayerAttackState;

//TODO refactoring von KeyInputListener. Eine zenztrale Klasse die auf Input lauscht.
// Diese zusammen mit Game State (bsp. mit libgdx ai statemachoine fuer States, wie Pausiert, GameLevel, Hauptmenue etc) entscheidet
// an wen Input weitergeleitet wird

/**
 * Lauscht auf Spieler Input und leitet die Erstellung von AngriffsSensoren bei attackRequests. Nutzung von Libgdx ai Statemachine ist hier vorhanden.
 */
public class PlayerAttackSystem extends IteratingSystem implements KeyInputListener, SensorEnemyContactListener {

    private boolean attackRequested = false;

    public PlayerAttackSystem(StudentsQuest context) {
        super(Family.all(PlayerComponent.class, PlayerAttackStateComponent.class).get());
        context.getWorldContactListener().addSensorListener(this);
        context.getInputManager().addKeyInputListener(this);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PlayerAttackStateComponent playerAttackStateComponent = ECSEngine.PLAYER_ATTACK_STATE_COMPONENT_MAPPER.get(entity);

        if (this.attackRequested) {
            if (playerAttackStateComponent.stateMachine.isInState(PlayerAttackState.NOT_ATTACKING)) {
                PlayerComponent playerComponent = ECSEngine.PLAYER_COMP_MAPPER.get(entity);
                PlayerComponent.Sensors sensorDircetion = PlayerComponent.Sensors.fromFacingDirection(playerComponent.curentFacing);
                playerAttackStateComponent.activatedSensor = sensorDircetion;
                Box2DComponent playerBox2dComp = ECSEngine.BOX2D_COMP_MAPPER.get(entity);
                FixtureBuilder.createAPlayerAttackSensor(sensorDircetion, playerBox2dComp);
                playerAttackStateComponent.stateMachine.changeState(PlayerAttackState.ATTACKING);
            }
        }
        playerAttackStateComponent.update(deltaTime);
    }

    @Override
    public void keyDown(InputManager manager, GameKey key) {
        if (key == GameKey.ATTACK) {
            this.attackRequested = true;
        }
    }

    @Override
    public void keyUp(InputManager manager, GameKey key) {
        if (key == GameKey.ATTACK) {
            this.attackRequested = false;
        }
    }

    @Override
    public void onSensorContactWithEnemy(Entity enemy) {
        HearthComponent enemyHearthComponent = ECSEngine.HEARTH_COMPONENT_MAPPER.get(enemy);
        enemyHearthComponent.currentHearths--;
        LoggerUtil.log(LogCategory.GAME, this, "Enemy hit, Enemy HP = " + enemyHearthComponent.currentHearths);
        if (enemyHearthComponent.currentHearths <= 0) {
            enemy.add(new RemoveComponent());
        }
    }
}
