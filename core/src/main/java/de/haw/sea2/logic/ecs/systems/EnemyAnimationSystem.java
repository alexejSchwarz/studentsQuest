package de.haw.sea2.logic.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import de.haw.sea2.logic.ecs.ECSEngine;
import de.haw.sea2.logic.ecs.components.AnimationComponent;
import de.haw.sea2.logic.ecs.components.Box2DComponent;
import de.haw.sea2.logic.ecs.components.EnemyComponent;
import de.haw.sea2.logic.entityLogic.EnemyMovementDirection;
import de.haw.sea2.view.animations.EnemyAnimation;

public class EnemyAnimationSystem extends IteratingSystem {

    public EnemyAnimationSystem() {
        super(Family.all(AnimationComponent.class, EnemyComponent.class, Box2DComponent.class).get());
    }

    // TODO spaeter States Berücksichtigen
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        final Box2DComponent box2DComponent = ECSEngine.BOX2D_COMP_MAPPER.get(entity);
        final AnimationComponent animationComp = ECSEngine.ANIMATION_COMP_MAPPER.get(entity);

        if (box2DComponent.body.getLinearVelocity().equals(Vector2.Zero)) {
            animationComp.animationTime = 0;
            return;
        }

        EnemyMovementDirection direction = EnemyMovementDirection.getMovementDirectionFromVectorForEnemies(box2DComponent.body.getLinearVelocity());

        switch (direction) {
            case UP: {
                animationComp.animationType = EnemyAnimation.ENEMY_MOVE_UP.animationType;
                break;
            }
            case DOWN: {
                animationComp.animationType = EnemyAnimation.ENEMY_MOVE_DOWN.animationType;
                break;
            }
            case LEFT: {
                animationComp.animationType = EnemyAnimation.ENEMY_MOVE_LEFT.animationType;
                break;
            }
            case RIGHT: {
                animationComp.animationType = EnemyAnimation.ENEMY_MOVE_RIGHT.animationType;
                break;
            }
            default: {
                throw new RuntimeException("SomeTingWong, We are no longer in a 2d World.");
            }
        }
    }
}
