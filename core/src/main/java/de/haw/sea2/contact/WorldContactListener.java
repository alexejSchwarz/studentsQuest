package de.haw.sea2.contact;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;

import de.haw.sea2.debug.LogCategory;
import de.haw.sea2.debug.LoggerUtil;
import de.haw.sea2.logic.Bits;
import de.haw.sea2.logic.ecs.components.PlayerComponent;

/**
 * WorldContactListener lauscht auf Collisions aus der Box2dWelt. Hier können neben der schon gegebenen Physik selbstdefinierte Collisions-Events an die Lauschenden gefeuert werden
 */
public class WorldContactListener implements ContactListener {

    // Category Bits, that should trigger an Interaction event with the player
    // for test purposes Ball is here. Wall is not included, as no further interactions besides the predefined physics should apply
    private static final Set<Short> PLAYER_INTERACTABLE_CATEGORIES = new HashSet<>(Set.of(
        Bits.BIT_BALL.value, Bits.BIT_GAME_ENTITY.value, Bits.BIT_ENEMY.value
    ));

    private static final Set<PlayerComponent.Sensors> SENSORS = Arrays.stream(PlayerComponent.Sensors.values()).collect(Collectors.toSet());

    private Array<PlayerContactListener> listeners;
    private Array<SensorEnemyContactListener> sensorListeners;

    public WorldContactListener() {
        this.listeners = new Array<>();
        this.sensorListeners = new Array<>();
    }

    public void addPlayerContactListener(PlayerContactListener listener) {
        this.listeners.add(listener);
    }

    public void addSensorListener(SensorEnemyContactListener listener) {
        this.sensorListeners.add(listener);
    }

    private void notifyPlayerContact(Entity player, Entity interactable) {
        for (PlayerContactListener listener : this.listeners) {
            listener.playerContact(player, interactable);
        }
    }

    private void notifySensorEnemyContact(Entity enemy, PlayerComponent.Sensors sensor) {
        LoggerUtil.log(LogCategory.GAME, WorldContactListener.class, "enemy contact with" + sensor.name());
        for (SensorEnemyContactListener listener : this.sensorListeners) {
            listener.onSensorContactWithEnemy(enemy);
        }
    }

    /**
     * wird aufgerufen, wenn Kontakt in World vermerkt wird. Bei fortlaufendem Kontakt ohne Unterbrechungen wird beginContact() nicht aufgerufen
     */
    @Override
    public void beginContact(Contact contact) {

        final Entity player;
        final Entity interactable;
        final Body bodyA = contact.getFixtureA().getBody();
        final Body bodyB = contact.getFixtureB().getBody();

        final short catFixA = contact.getFixtureA().getFilterData().categoryBits;
        final short catFixB = contact.getFixtureB().getFilterData().categoryBits;

        // wenn true, dann wurde Event schon weitergeleitet
        if (isSensorEnemyContact(contact.getFixtureA(), contact.getFixtureB())) {
            return;
        }

        if (isSensorHeartContact(contact.getFixtureA(), contact.getFixtureB())) {
            return;
        }

        // Falls einer der Beiden ein Sensor ist, es aber kein Sensor Gegner Kontakt, dann ignorieren.
        // sonst koennte man man Angriff Münzen einsammeln
        if (contact.getFixtureA().isSensor() || contact.getFixtureB().isSensor()) {
            return;
        }

        if (catFixA == Bits.BIT_PLAYER.value) {
            player = (Entity) bodyA.getUserData();
        } else if (catFixB == Bits.BIT_PLAYER.value) {
            player = (Entity) bodyB.getUserData();
        } else {
            return;
        }

        if (PLAYER_INTERACTABLE_CATEGORIES.contains(catFixA)) {
            interactable = (Entity) bodyA.getUserData();
        } else if (PLAYER_INTERACTABLE_CATEGORIES.contains(catFixB)) {
            interactable = (Entity) bodyB.getUserData();
        } else {
            return;
        }

        LoggerUtil.log(LogCategory.GAME, this, "Player collides with Interactable");
        notifyPlayerContact(player, interactable);
    }

    /**
     * Gegner Entitaeten haben Referenz auf die eigene Entitaet als UserData des Box2dBodies. Spieler Sensoren haben Sensor Enum als UserData des Fixtures.
     * Hier wird nicht nur gecheckt, sondern auch ueber Kontakt benachrichtet, da der Aufrufer ein Boolean braucht fuer seinen Methodenablauf.
     * notifySensorEnemyContact() braucht dagegen das jeweilige Entity und den Sensor.
     */
    private boolean isSensorEnemyContact(Fixture a, Fixture b) {
        if (a.getUserData() instanceof PlayerComponent.Sensors && SENSORS.contains((PlayerComponent.Sensors) a.getUserData()) && b.getFilterData().categoryBits == Bits.BIT_ENEMY.value) {
            LoggerUtil.log(LogCategory.DEBUG, this, "" + b.getFilterData().categoryBits + " "  + Bits.BIT_ENEMY + " " + Bits.BIT_ENEMY.value);
            notifySensorEnemyContact((Entity) b.getBody().getUserData(), (PlayerComponent.Sensors) a.getUserData());
            return true;
        } else if (b.getUserData() instanceof PlayerComponent.Sensors && SENSORS.contains((PlayerComponent.Sensors) b.getUserData()) && a.getFilterData().categoryBits == Bits.BIT_ENEMY.value) {
            notifySensorEnemyContact((Entity) a.getBody().getUserData(), (PlayerComponent.Sensors) b.getUserData());
            return true;
        }
        return false;
    }

    private boolean isSensorHeartContact(Fixture a, Fixture b) {
        // Prüfe, ob einer der beiden ein Spieler ist und der andere ein Sensor-Item (Herz/Coin) und ignoriere Sensoren, die nicht zu den Items gehören
        boolean aIsPlayer = a.getFilterData().categoryBits == Bits.BIT_PLAYER.value && !(a.getUserData() instanceof PlayerComponent.Sensors);
        boolean bIsPlayer = b.getFilterData().categoryBits == Bits.BIT_PLAYER.value && !(b.getUserData() instanceof PlayerComponent.Sensors);
        boolean aIsHeartSensor = a.isSensor() && (a.getFilterData().categoryBits == Bits.BIT_GAME_ENTITY.value);
        boolean bIsHeartSensor = b.isSensor() && (b.getFilterData().categoryBits == Bits.BIT_GAME_ENTITY.value);
    
        if ((aIsPlayer && bIsHeartSensor) || (bIsPlayer && aIsHeartSensor)) {
            // Hier kannst du direkt notifyPlayerContact aufrufen oder true zurückgeben
            Entity player = aIsPlayer ? (Entity) a.getBody().getUserData() : (Entity) b.getBody().getUserData();
            Entity interactable = aIsHeartSensor ? (Entity) a.getBody().getUserData() : (Entity) b.getBody().getUserData();
            notifyPlayerContact(player, interactable);
            return true;
        }
        return false;
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
