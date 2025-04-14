package de.haw.sea2.contact;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;

import de.haw.sea2.debug.LogCategory;
import de.haw.sea2.debug.LoggerUtil;
import de.haw.sea2.ecs.Bits;

/**
 * WorldContactListener lauscht auf Collisions aus der Box2dWelt. Hier können neben der schon gegebenen Physik selbstdefinierte Collisions-Events an die Lauschenden gefeuert werden
 */
public class WorldContactListener implements ContactListener {

    // Category Bits, that should trigger an Interaction event with the player
    // for test purposes Ball is here. Wall is not included, as no further interactions besides the predefined physics should apply
    private static final Set<Short> INTERACTABLE_CATEGORIES = new HashSet<>(Set.of(Bits.BIT_BALL.value, Bits.BIT_GAME_ENTITY.value, Bits.BIT_TMP_ENEMY.value));

    private Array<PlayerContactListener> listeners;

    public WorldContactListener() {
        this.listeners = new Array<>();
    }

    public void addListener(PlayerContactListener listener) {
        this.listeners.add(listener);
    }

    private void notifyPlayerContact(Entity player, Entity interactable) {
        for (PlayerContactListener listener : this.listeners) {
            listener.playerContact(player, interactable);
        }
    }

    @Override
    public void beginContact(Contact contact) {

        final Entity player;
        final Entity interactable;
        final Body bodyA = contact.getFixtureA().getBody();
        final Body bodyB = contact.getFixtureB().getBody();

        final short catFixA = contact.getFixtureA().getFilterData().categoryBits;
        final short catFixB = contact.getFixtureB().getFilterData().categoryBits;

        if (catFixA == Bits.BIT_PLAYER.value) {
            player = (Entity) bodyA.getUserData();
        } else if (catFixB == Bits.BIT_PLAYER.value) {
            player = (Entity) bodyB.getUserData();
        } else {
            return;
        }

        if (INTERACTABLE_CATEGORIES.contains(catFixA)) {
            interactable = (Entity) bodyA.getUserData();
        } else if (INTERACTABLE_CATEGORIES.contains(catFixB)) {
            interactable = (Entity) bodyB.getUserData();
        } else {
            return;
        }

        LoggerUtil.log(LogCategory.GAME, this, "Player collides with Interactable");
        notifyPlayerContact(player, interactable);
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        // Hier könnte Code hinzugefügt werden, um Kollisionsverhalten zu modifizieren
        // Beispielsweise:
        // - contact.setEnabled(false); // Deaktiviert die Kollisionsreaktion
        // - contact.setFriction(0.8f); // Setzt spezielle Reibung für diese Kollision
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        // Hier könnte Code hinzugefügt werden, um auf die Kollisionsstärke zu reagieren
        // Beispielsweise:
        // float impactForce = impulse.getNormalImpulses()[0];
        // if (impactForce > 10f) {
        // // Starker Aufprall - spiele lautes Geräusch ab oder verursache Schaden
        // }
    }
}
