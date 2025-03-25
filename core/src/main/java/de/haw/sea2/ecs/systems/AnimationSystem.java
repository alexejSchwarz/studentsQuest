package de.haw.sea2.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.ecs.components.AnimationComponent;
import de.haw.sea2.ecs.ECSEngine;

public class AnimationSystem extends IteratingSystem {

    public AnimationSystem(StudentsQuest context) {
        super(Family.all(AnimationComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        final AnimationComponent animationComp = ECSEngine.ANIMATION_COMP_MAPPER.get(entity);
        if (animationComp != null) {
            // used to proceed to next frame of the animation
            animationComp.animationTime += deltaTime;
        }
    }
}
