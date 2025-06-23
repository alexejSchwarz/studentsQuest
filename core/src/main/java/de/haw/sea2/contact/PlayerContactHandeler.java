package de.haw.sea2.contact;

import com.badlogic.ashley.core.Entity;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.audio.Audio;
import de.haw.sea2.logic.ecs.ECSEngine;
import de.haw.sea2.logic.ecs.components.HearthComponent;
import de.haw.sea2.logic.ecs.components.ItemType;
import de.haw.sea2.logic.ecs.components.PlayerComponent;
import de.haw.sea2.logic.ecs.components.RemoveComponent;
import de.haw.sea2.logic.ecs.components.SimpleRenderComponent;

/**
 * Lauscht auf Interactions aus WorldContactListener und entscheidet, was daraus folgen soll
 */
public class PlayerContactHandeler implements PlayerContactListener {

    private final StudentsQuest context;

    public PlayerContactHandeler(StudentsQuest context) {
        context.getWorldContactListener().addPlayerContactListener(this);
        this.context = context;
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
            ItemType itemType = ECSEngine.ITEM_COMPONENT_MAPPER.get(interactable).itemType;
            if (itemType == ItemType.COIN) {
                RemoveComponent removeComponent = this.context.getEngine().createComponent(RemoveComponent.class);
                interactable.add(removeComponent);
                PlayerComponent playerComponent = ECSEngine.PLAYER_COMP_MAPPER.get(player);
                playerComponent.collectedCoins++;
                context.getAudioManager().playAudio(Audio.COIN_PICKUP_SOUND);
            } else if (itemType == ItemType.HEART && ECSEngine.HEARTH_COMPONENT_MAPPER.get(player).currentHearths < ECSEngine.HEARTH_COMPONENT_MAPPER.get(player).maxHearths) {
                RemoveComponent removeComponent = this.context.getEngine().createComponent(RemoveComponent.class);
                interactable.add(removeComponent);
                HearthComponent hearths = ECSEngine.HEARTH_COMPONENT_MAPPER.get(player);
                hearths.currentHearths++;
            }
        }  else if (interactionType == InteractionType.PLAYER_TAKES_DMG) {
            HearthComponent hearths = ECSEngine.HEARTH_COMPONENT_MAPPER.get(player);
            hearths.currentHearths--;
        }
    }
}
