package de.haw.sea2.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.ecs.components.AnimationComponent;
import de.haw.sea2.ecs.components.Box2DComponent;
import de.haw.sea2.ecs.components.PlayerComponent;
import de.haw.sea2.ecs.systems.AnimationSystem;
import de.haw.sea2.ecs.systems.PlayerAnimationSystem;
import de.haw.sea2.ecs.systems.PlayerCameraSystem;
import de.haw.sea2.ecs.systems.PlayerMovementSystem;
import de.haw.sea2.view.AnimationType;

/**
 * Die zentrale Engine des Entity-Component-Systems (ECS) für das Spiel.
 *
 * <p>
 * Diese Klasse verwaltet alle Spielentitäten (wie Spieler und Wände) und ihre
 * Komponenten.
 * Sie erbt von PooledEngine, was bedeutet, dass sie Objekte wiederverwendet, um
 * Speicher
 * zu sparen und die Leistung zu verbessern.
 * </p>
 *
 * <p>
 * Ein Entity-Component-System ist ein Architekturmuster, das Spielobjekte
 * (Entities)
 * aus austauschbaren Bausteinen (Components) zusammensetzt, anstatt eine tiefe
 * Vererbungshierarchie
 * zu verwenden. Die Engine koordiniert diese Entities und führt Systeme aus,
 * die
 * die Komponenten verarbeiten.
 * </p>
 *
 * <p>
 * Diese Engine ist verantwortlich für:
 * <ul>
 * <li>Erstellung und Verwaltung von Spielobjekten (z.B. Spieler)</li>
 * <li>Erstellung der physikalischen Welt und Kollisionsobjekte</li>
 * <li>Verwaltung der Systeme, die Spiellogik verarbeiten</li>
 * </ul>
 * </p>
 */
public class ECSEngine extends PooledEngine {

    /**
     * ComponentMapper für schnellen Zugriff auf PlayerComponent-Objekte.
     *
     * <p>
     * Ein ComponentMapper ist wie ein Schlüssel, der sehr schnell eine bestimmte
     * Komponente aus einer Entity holen kann - viel schneller als die normale
     * Methode.
     * Das ist wichtig für Spiele, die flüssig laufen sollen.
     * </p>
     */
    public static final ComponentMapper<PlayerComponent> PLAYER_COMP_MAPPER = ComponentMapper.getFor(PlayerComponent.class);

    /**
     * ComponentMapper für schnellen Zugriff auf Box2DComponent-Objekte.
     *
     * <p>
     * Diese Komponenten enthalten alle physikalischen Eigenschaften einer Entity,
     * wie Position, Körperform und Kollisionsinformationen.
     * </p>
     */
    public static final ComponentMapper<Box2DComponent> BOX2D_COMP_MAPPER = ComponentMapper.getFor(Box2DComponent.class);


    public static final ComponentMapper<AnimationComponent> ANIMATION_COMP_MAPPER = ComponentMapper.getFor(AnimationComponent.class);

    /**
     * Die Box2D-Welt, in der die Physik-Simulation stattfindet.
     *
     * <p>
     * Box2D ist eine Physik-Engine, die Bewegungen, Kollisionen und andere
     * physikalische Effekte simuliert. Alle beweglichen oder kollidierenden Objekte
     * müssen Teil dieser Welt sein.
     * </p>
     */
    private final World world;

    /**
     * Erstellt eine neue ECS-Engine für das Spiel.
     *
     * <p>
     * Der Konstruktor richtet die Engine ein und fügt die benötigten Systeme hinzu,
     * die für die Spiellogik verantwortlich sind.
     * </p>
     *
     * @param context Der Hauptkontext des Spiels (StudentsQuest), der wichtige
     *                Ressourcen enthält, wie die physikalische Welt und den
     *                InputManager
     */
    public ECSEngine(final StudentsQuest context) {
        super();
        this.world = context.getWorld();
        this.addSystem(new PlayerMovementSystem(context));
        this.addSystem(new PlayerCameraSystem(context));
        this.addSystem(new AnimationSystem(context));
        this.addSystem(new PlayerAnimationSystem());
    }

    /**
     * Erstellt einen Spielercharakter in der Spielwelt.
     *
     * <p>
     * Diese Methode:
     * <ul>
     * <li>Erstellt eine neue Entität für den Spieler</li>
     * <li>Fügt eine PlayerComponent hinzu, die spielerspezifische Daten
     * enthält</li>
     * <li>Fügt eine Box2DComponent hinzu, die den physikalischen Körper des
     * Spielers definiert</li>
     * <li>Konfiguriert die Kollisionsfilter, damit der Spieler nur mit Wänden
     * kollidiert</li>
     * <li>Definiert die Form des Spielers als Rechteck</li>
     * </ul>
     * </p>
     *
     * @param playerSpawnLocation Die Startposition des Spielers in der Welt
     * @param width               Die Breite des Spielers in Spieleinheiten
     * @param height              Die Höhe des Spielers in Spieleinheiten
     */
    public void createPlayer(final Vector2 playerSpawnLocation, final float width, final float height) {
        // Erstelle eine neue Spieler-Entität
        final Entity player = this.createEntity();

        // Erstelle und konfiguriere die Spieler-Komponente mit Geschwindigkeit
        final PlayerComponent playerComp = this.createComponent(PlayerComponent.class);
        playerComp.speed.set(3f, 3f);
        player.add(playerComp);

        // Setze die physikalischen Eigenschaften zurück und erstelle eine
        // Box2D-Komponente
        StudentsQuest.resetBodieAndFixtureDefinition();
        final Box2DComponent b2dComp = this.createComponent(Box2DComponent.class);

        // Konfiguriere die Position und Art des physikalischen Körpers
        StudentsQuest.BODY_DEF.position.set(playerSpawnLocation.x, playerSpawnLocation.y);
        StudentsQuest.BODY_DEF.fixedRotation = true; // Verhindert Rotation des Spielers
        StudentsQuest.BODY_DEF.type = BodyDef.BodyType.DynamicBody; // Beweglicher Körper

        // Erstelle den physikalischen Körper in der Welt
        b2dComp.body = this.world.createBody(StudentsQuest.BODY_DEF);
        b2dComp.body.setUserData("PLAYER"); // Markiert den Körper als Spieler
        b2dComp.width = width;
        b2dComp.height = height;
        b2dComp.interpolatedRenderPosition.set(b2dComp.body.getPosition());

        // Setze die Kollisionsfilter (Spieler kollidiert nur mit Wänden)
        StudentsQuest.FIXTURE_DEF.filter.categoryBits = Bits.BIT_PLAYER.value;
        //TODO tmp remove later
        StudentsQuest.FIXTURE_DEF.filter.maskBits = (short) (Bits.BIT_WALL.value | Bits.BIT_BALL.value);

        // Erstelle eine rechteckige Form für den Spieler
        final PolygonShape pShape = new PolygonShape();
        pShape.setAsBox(width * 0.5f, height * 0.5f); // Hälfte der Breite/Höhe, da vom Zentrum gemessen
        StudentsQuest.FIXTURE_DEF.shape = pShape;

        // Füge die Form dem Körper hinzu
        b2dComp.body.createFixture(StudentsQuest.FIXTURE_DEF);
        pShape.dispose(); // Wichtig: Ressourcen freigeben

        // Füge die Box2D-Komponente zur Entität hinzu und registriere sie in der Engine
        player.add(b2dComp);

        //animation
        final AnimationComponent animationComp = this.createComponent(AnimationComponent.class);
        animationComp.animationType = AnimationType.HERO_MOVE_DOWN;
        //TODO im moment 64*64 dummy texture. spaeter 32 * UnitScale und letztere anpassen
        animationComp.width = 64 * StudentsQuest.UNIT_SCALE;
        animationComp.height = 64 * StudentsQuest.UNIT_SCALE;
        player.add(animationComp);

        this.addEntity(player);
    }

    //TODO tmp remove later
    public void createBall() {
        Entity ball = this.createEntity();

        StudentsQuest.resetBodieAndFixtureDefinition();
        final Box2DComponent b2dComp = this.createComponent(Box2DComponent.class);

        // Konfiguriere die Position und Art des physikalischen Körpers
        StudentsQuest.BODY_DEF.position.set(2f, 2f);
        StudentsQuest.BODY_DEF.fixedRotation = true; // Verhindert Rotation des Spielers
        StudentsQuest.BODY_DEF.type = BodyDef.BodyType.DynamicBody; // Beweglicher Körper

        // Erstelle den physikalischen Körper in der Welt
        b2dComp.body = this.world.createBody(StudentsQuest.BODY_DEF);
        b2dComp.body.setUserData("BALL"); // Markiert den Körper als Spieler
        b2dComp.width = 1f;
        b2dComp.height = 1f;
        b2dComp.interpolatedRenderPosition.set(b2dComp.body.getPosition());

        StudentsQuest.FIXTURE_DEF.restitution = 0.5f;
        StudentsQuest.FIXTURE_DEF.friction = 0.2f;

        StudentsQuest.FIXTURE_DEF.filter.categoryBits = Bits.BIT_BALL.value;
        StudentsQuest.FIXTURE_DEF.filter.maskBits = (short) (Bits.BIT_WALL.value | Bits.BIT_PLAYER.value);

        // Erstelle eine rechteckige Form für den Spieler
        final CircleShape circleShape = new CircleShape();
        circleShape.setRadius(0.5f);
        StudentsQuest.FIXTURE_DEF.shape = circleShape;

        // Füge die Form dem Körper hinzu
        b2dComp.body.createFixture(StudentsQuest.FIXTURE_DEF);
        circleShape.dispose(); // Wichtig: Ressourcen freigeben

        // Füge die Box2D-Komponente zur Entität hinzu und registriere sie in der Engine
        ball.add(b2dComp);
        this.addEntity(ball);
    }
}
