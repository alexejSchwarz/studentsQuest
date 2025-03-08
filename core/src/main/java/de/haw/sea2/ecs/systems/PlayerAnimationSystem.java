package de.haw.sea2.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.ecs.ECSEngine;
import de.haw.sea2.ecs.components.AnimationComponent;
import de.haw.sea2.ecs.components.Box2DComponent;
import de.haw.sea2.ecs.components.PlayerComponent;
import de.haw.sea2.view.AnimationType;

public class PlayerAnimationSystem extends IteratingSystem {

    public PlayerAnimationSystem() {
        super(Family.all(AnimationComponent.class, PlayerComponent.class, Box2DComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        final Box2DComponent box2DComponent = ECSEngine.BOX2D_COMP_MAPPER.get(entity);
        final AnimationComponent animationComp = ECSEngine.ANIMATION_COMP_MAPPER.get(entity);
        if (box2DComponent.body.getLinearVelocity().equals(Vector2.Zero)) {
            // player does not move
            animationComp.animationTime = 0;
        } else if (box2DComponent.body.getLinearVelocity().x > 0) {
            // player moves to the right
            animationComp.animationType = AnimationType.HERO_MOVE_RIGHT;
        } else if (box2DComponent.body.getLinearVelocity().x < 0) {
            // player moves to the left
            animationComp.animationType = AnimationType.HERO_MOVE_LEFT;
        } else if (box2DComponent.body.getLinearVelocity().y > 0) {
            // player moves up
            animationComp.animationType = AnimationType.HERO_MOVE_UP;
        } else if (box2DComponent.body.getLinearVelocity().y < 0) {
            // player moves down
            animationComp.animationType = AnimationType.HERO_MOVE_DOWN;
        }
    }
}
