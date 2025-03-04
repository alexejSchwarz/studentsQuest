package de.haw.sea2.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import de.haw.sea2.ecs.components.ImageComponent;

public class RenderSystem extends IteratingSystem {

    public RenderSystem() {
        super(Family.all(ImageComponent.class).get());
    }
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        //TODO implement
    }
}
