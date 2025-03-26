package de.haw.sea2.contact;

import com.badlogic.ashley.core.Entity;

/**
 * Listener Interface aus ObserverPattern
 */
public interface PlayerContactListener {

    void playerContact(Entity player, Entity interactable);
}
