package de.haw.sea2.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.ecs.components.AnimationComponent;
import de.haw.sea2.ecs.components.Box2DComponent;
import de.haw.sea2.ecs.components.PlayerComponent;
import de.haw.sea2.ecs.components.SimpleRenderComponent;
import de.haw.sea2.view.animations.PlayerAnimation;

//TODO Faktory pattern anschauen und implementieren, wenn passend
/**
 * Klasse zum erstellen von Entities
 */
public class EntityFactory {

    private final ECSEngine engine;
    private final World world;

    public EntityFactory(StudentsQuest context) {
        this.engine = context.getEngine();
        this.world = context.getWorld();
    }

    //TODO Methoden zu Entitaets-Erstellung generalisieren bzw. das rausziehen, was parametrisiert werden kann!!!

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
        final Entity player = this.engine.createEntity();

        // Erstelle und konfiguriere die Spieler-Komponente mit Geschwindigkeit
        final PlayerComponent playerComp = this.engine.createComponent(PlayerComponent.class);
        playerComp.speed.set(3f, 3f);
        player.add(playerComp);

        // Setze die physikalischen Eigenschaften zurück und erstelle eine
        // Box2D-Komponente
        StudentsQuest.resetBodieAndFixtureDefinition();
        final Box2DComponent b2dComp = this.engine.createComponent(Box2DComponent.class);

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
        final AnimationComponent animationComp = this.engine.createComponent(AnimationComponent.class);
        animationComp.animationType = PlayerAnimation.HERO_MOVE_DOWN.animationType;
        animationComp.width = StudentsQuest.UNIT_SIZE_IN_PIXELS * StudentsQuest.UNIT_SCALE;
        animationComp.height = StudentsQuest.UNIT_SIZE_IN_PIXELS * StudentsQuest.UNIT_SCALE;
        player.add(animationComp);

        this.engine.addEntity(player);
    }

    //TODO tmp remove later
    public void createBall() {
        Entity ball = this.engine.createEntity();

        StudentsQuest.resetBodieAndFixtureDefinition();
        final Box2DComponent b2dComp = this.engine.createComponent(Box2DComponent.class);

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

        SimpleRenderComponent simpleRenderComp = this.engine.createComponent(SimpleRenderComponent.class);
        simpleRenderComp.textureFilePath = "assetsFromTut/Ball.png";
        simpleRenderComp.width = 1f;
        simpleRenderComp.height = 1f;

        ball.add(simpleRenderComp);

        this.engine.addEntity(ball);
    }
}
