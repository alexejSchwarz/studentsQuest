package de.haw.sea2.logic.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.logic.ecs.components.RemoveComponent;

public class EntityRemovalSystem extends IteratingSystem {

    private final StudentsQuest context;

    public EntityRemovalSystem(StudentsQuest context) {
        super(Family.one(RemoveComponent.class).get());
        this.context = context;
    }

    /**
     * Entfernt Entity via engine. Dies entfernt auch die bodies aus der world.
     */
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        this.context.getEngine().removeEntity(entity);
    }
}
