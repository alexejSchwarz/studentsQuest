package de.haw.sea2.contact;

import com.badlogic.ashley.core.Entity;

/**
 * Observer Pattern, um Collision Event zwischen Spielersensor (Angriff Flaeche) und Gegner zu vermitteln
 */
public interface SensorEnemyContactListener {

    void onSensorContactWithEnemy(Entity enemy);
}
