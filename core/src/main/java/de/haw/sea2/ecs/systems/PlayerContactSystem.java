package de.haw.sea2.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import de.haw.sea2.contact.PlayerContactListener;
import de.haw.sea2.StudentsQuest;
import de.haw.sea2.ecs.ECSEngine;
import de.haw.sea2.contact.InteractionType;
import de.haw.sea2.ecs.components.SimpleRenderComponent;

/**
 * Lauscht auf Interactions aus WorldContactListener
 */
public class PlayerContactSystem extends IteratingSystem implements PlayerContactListener {

    public PlayerContactSystem(StudentsQuest context) {
        super(Family.all().get());
        context.getWorldContactListener().addListener(this);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }

    /**
     * verarbeitet Interaktionen von player- und interactable-entity
     */
    @Override
    public void playerContact(Entity player, Entity interactable) {
        InteractionType interactionType = ECSEngine.INTERACTION_COMPONENT_MAPPER.get(interactable).type;
        if (interactionType == InteractionType.OBJECT_SHRINK) {
            SimpleRenderComponent simpleRenderComponent = ECSEngine.SIMPLE_RENDER_COMPONENT_MAPPER.get(interactable);
            simpleRenderComponent.width = simpleRenderComponent.width * 0.9f;
            simpleRenderComponent.height = simpleRenderComponent.height * 0.9f;
        }
    }
}
