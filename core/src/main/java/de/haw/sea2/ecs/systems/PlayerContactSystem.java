package de.haw.sea2.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import de.haw.sea2.audio.AudioType;
import de.haw.sea2.contact.PlayerContactListener;
import de.haw.sea2.StudentsQuest;
import de.haw.sea2.ecs.ECSEngine;
import de.haw.sea2.contact.InteractionType;
import de.haw.sea2.ecs.components.HearthComponent;
import de.haw.sea2.ecs.components.PlayerComponent;
import de.haw.sea2.ecs.components.RemoveComponent;
import de.haw.sea2.ecs.components.SimpleRenderComponent;

/**
 * Lauscht auf Interactions aus WorldContactListener. Iteriert uber Entities, die ein RemoveComponent besitzen
 */
public class PlayerContactSystem extends IteratingSystem implements PlayerContactListener {

    private final StudentsQuest context;

    public PlayerContactSystem(StudentsQuest context) {
        super(Family.one(RemoveComponent.class).get());
        context.getWorldContactListener().addListener(this);
        this.context = context;
    }

    /**
     * Entfernt Entity via engine. Dies entfernt auch die bodies aus der world.
     */
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        this.context.getEngine().removeEntity(entity);
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
        } else if (interactionType == InteractionType.OBJECT_COLLECT) {
            RemoveComponent removeComponent = this.context.getEngine().createComponent(RemoveComponent.class);
            interactable.add(removeComponent);
            PlayerComponent playerComponent = ECSEngine.PLAYER_COMP_MAPPER.get(player);
            playerComponent.collectedCoins++;
            context.getAudioManager().playAudio(AudioType.COIN_PICKUP_SOUND);
        } else if (interactionType == InteractionType.PLAYER_TAKES_DMG) {
            HearthComponent hearths = ECSEngine.HEARTH_COMPONENT_MAPPER.get(player);
            hearths.currentHearths--;
        }
    }
}
