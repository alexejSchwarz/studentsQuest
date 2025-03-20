package de.haw.sea2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import de.haw.sea2.debug.LogCategory;
import de.haw.sea2.debug.LoggerUtil;

/**
 * Überwacht und verarbeitet Kollisionsereignisse zwischen physikalischen
 * Objekten in der Spielwelt.
 *
 * <p>
 * Diese Klasse implementiert das ContactListener-Interface von Box2D und wird
 * automatisch
 * benachrichtigt, wenn physikalische Objekte in der Box2D-Welt miteinander in
 * Kontakt kommen
 * oder sich wieder voneinander trennen.
 * </p>
 *
 * <p>
 * Der WorldContactListener spielt eine entscheidende Rolle im Spiel, da er:
 * <ul>
 * <li>Spiellogik auslösen kann, wenn bestimmte Objekte kollidieren (z.B.
 * Schaden nehmen)</li>
 * <li>Spielereignisse erkennen kann (z.B. wenn ein Spieler ein Sammelobjekt
 * berührt)</li>
 * <li>Spezielle Physikeffekte implementieren kann (z.B. rutschige oder klebrige
 * Oberflächen)</li>
 * <li>Sensorbereiche überwachen kann, um zu erkennen, wenn Objekte bestimmte
 * Zonen betreten</li>
 * </ul>
 * </p>
 */
public class WorldContactListener implements ContactListener {

    /**
     * Wird aufgerufen, wenn zwei Fixtures anfangen, miteinander in Kontakt zu
     * stehen.
     *
     * <p>
     * Diese Methode wird beim ersten Auftreten einer Kollision zwischen zwei
     * Objekten aufgerufen.
     * Sie ist ideal für das Auslösen von einmaligen Ereignissen wie Sammeln von
     * Gegenständen,
     * Auslösen von Schaltern oder Betreten von Zonen.
     * </p>
     *
     * <p>
     * In der aktuellen Implementierung werden die Informationen der kollidierenden
     * Fixtures
     * (Benutzerdaten und Sensor-Status) nur zu Debug-Zwecken ausgegeben.
     * </p>
     *
     * @param contact Das Contact-Objekt, das Informationen über die Kollision
     *                enthält
     */
    @Override
    public void beginContact(Contact contact) {
        // Hole die beiden an der Kollision beteiligten Fixtures
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        // Gib Debug-Informationen über die erste Fixture aus
        // getUserData() kann verwendet werden, um benutzerdefinierte Objekte zu
        // speichern
        // isSensor() gibt an, ob die Fixture nur als Sensor fungiert (keine
        // physikalische Reaktion)
        LoggerUtil.debug(LogCategory.DEBUG,this,fixtureA.getUserData() + " " + fixtureA.isSensor());

        // Gib Debug-Informationen über die zweite Fixture aus
        LoggerUtil.debug(LogCategory.DEBUG,this,fixtureB.getUserData() + " " + fixtureB.isSensor());
    }

    /**
     * Wird aufgerufen, wenn zwei Fixtures aufhören, miteinander in Kontakt zu
     * stehen.
     *
     * <p>
     * Diese Methode wird aufgerufen, wenn eine Kollision zwischen zwei Objekten
     * endet.
     * Sie ist nützlich für das Auslösen von Ereignissen beim Verlassen von
     * Bereichen oder
     * beim Loslassen von Objekten.
     * </p>
     *
     * <p>
     * In der aktuellen Implementierung werden die Informationen der kollidierenden
     * Fixtures
     * (Benutzerdaten und Sensor-Status) nur zu Debug-Zwecken ausgegeben.
     * </p>
     *
     * @param contact Das Contact-Objekt, das Informationen über die beendete
     *                Kollision enthält
     */
    @Override
    public void endContact(Contact contact) {
        // Hole die beiden an der beendeten Kollision beteiligten Fixtures
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        // Gib Debug-Informationen über die Fixtures aus
        LoggerUtil.debug(LogCategory.DEBUG,this,fixtureA.getUserData() + " " + fixtureA.isSensor());
        LoggerUtil.debug(LogCategory.DEBUG,this,fixtureB.getUserData() + " " + fixtureB.isSensor());
    }

    /**
     * Wird aufgerufen, bevor die Kollision zwischen zwei Fixtures gelöst wird.
     *
     * <p>
     * Diese Methode ermöglicht es, die Art der Kollision zu modifizieren, bevor die
     * physikalische Reaktion berechnet wird. Hier könnten beispielsweise bestimmte
     * Kollisionen deaktiviert werden oder spezielle Physikeffekte implementiert
     * werden.
     * </p>
     *
     * <p>
     * In der aktuellen Implementierung wird keine spezielle Aktion ausgeführt.
     * </p>
     *
     * @param contact     Das Contact-Objekt, das Informationen über die Kollision
     *                    enthält
     * @param oldManifold Das Manifold-Objekt, das die vorherige
     *                    Kollisionsinformation enthält
     */
    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        // Hier könnte Code hinzugefügt werden, um Kollisionsverhalten zu modifizieren
        // Beispielsweise:
        // - contact.setEnabled(false); // Deaktiviert die Kollisionsreaktion
        // - contact.setFriction(0.8f); // Setzt spezielle Reibung für diese Kollision
    }

    /**
     * Wird aufgerufen, nachdem die Kollision zwischen zwei Fixtures gelöst wurde.
     *
     * <p>
     * Diese Methode gibt Zugriff auf die Impulse, die durch die Kollision
     * entstanden sind.
     * Sie ist nützlich, um auf die Stärke einer Kollision zu reagieren, z.B. um
     * Schaden
     * basierend auf der Aufprallstärke zu berechnen oder um Geräusche mit
     * unterschiedlicher
     * Lautstärke je nach Kollisionsstärke abzuspielen.
     * </p>
     *
     * <p>
     * In der aktuellen Implementierung wird keine spezielle Aktion ausgeführt.
     * </p>
     *
     * @param contact Das Contact-Objekt, das Informationen über die Kollision
     *                enthält
     * @param impulse Das ContactImpulse-Objekt, das Informationen über die bei der
     *                Kollision entstandenen Kräfte enthält
     */
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
