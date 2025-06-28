package de.haw.sea2.logic.ecs.builders;

import java.util.function.Consumer;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

import de.haw.sea2.debug.LogCategory;
import de.haw.sea2.debug.LoggerUtil;
import de.haw.sea2.logic.ecs.ECSEngine;
import de.haw.sea2.logic.ecs.components.Box2DComponent;

/**
 * Ein generischer Builder für die Erstellung von Entitäten im ECS-System.
 * Dieser Builder ermöglicht die flexible Konfiguration von Entitäten,
 * einschließlich Position, Größe, physikalischen Eigenschaften und zusätzlichen Komponenten.
 */
public class EntityBuilder {

    private final ECSEngine engine;
    private final World world;
    private final Entity entity;
    private final Box2DComponent box2dComponent;

    private Vector2 position = new Vector2(0, 0);
    private float width = 1f;
    private float height = 1f;
    private short categoryBits;
    private short maskBits;
    private boolean fixedRotation = true;
    private BodyDef.BodyType bodyType = BodyDef.BodyType.DynamicBody;
    private float restitution = 0f;
    private float friction = 0f;

    private Consumer<Entity> additionalComponents = e -> {};
    private Shape shape;
    private Body body;
    private boolean isSensor = false;

    /**
     * Konstruktor für den GenericEntityBuilder.
     *
     * Initialisiert den Builder mit der angegebenen ECS-Engine, der Box2D-Welt und einer physikalischen Form.
     * Eine neue Entität wird erstellt und eine Box2D-Komponente wird der Entität zugeordnet.
     *
     * @param engine Die ECS-Engine, die für die Verwaltung der Entitäten verwendet wird.
     * @param world  Die Box2D-Welt, in der die physikalischen Eigenschaften der Entität simuliert werden.
     * @param shape  Die physikalische Form (z. B. Kreis, Rechteck), die für die Simulation verwendet wird.
     */
    public EntityBuilder(ECSEngine engine, World world, Shape shape) {
        this.engine = engine;
        this.world = world;
        this.entity = engine.createEntity();
        this.box2dComponent = engine.createComponent(Box2DComponent.class);
        this.shape = shape; // Shape is now required in the constructor
    }

    /**
     * Setzt den Body-Typ der Entität.
     *
     * @param bodyType Der Body-Typ (z. B. Static, Dynamic, Kinematic).
     * @return Der aktuelle Builder für method chaining.
     */
    public EntityBuilder setSensor(boolean isSensor) {
        this.isSensor = isSensor;
        return this;
    }

    /**
     * Setzt die Position der Entität.
     *
     * @param x Die x-Koordinate der Position.
     * @param y Die y-Koordinate der Position.
     * @return Der aktuelle Builder für method chaining.
     */
    public EntityBuilder position(float x, float y) {
        this.position.set(x, y);
        return this;
    }

    /**
     * Setzt die Größe der Entität.
     *
     * @param width  Die Breite der Entität.
     * @param height Die Höhe der Entität.
     * @return Der aktuelle Builder für method chaining.
     */
    public EntityBuilder size(float width, float height) {
        this.width = width;
        this.height = height;
        return this;
    }

    /**
     * Setzt die Kategorie-Bits für die Kollisionsfilterung.
     *
     * @param categoryBits Die Kategorie-Bits.
     * @return Der aktuelle Builder für method chaining.
     */
    public EntityBuilder categoryBits(short categoryBits) {
        this.categoryBits = categoryBits;
        return this;
    }

    /**
     * Setzt die Masken-Bits für die Kollisionsfilterung.
     *
     * @param maskBits Die Masken-Bits.
     * @return Der aktuelle Builder für method chaining.
     */
    public EntityBuilder maskBits(short maskBits) {
        this.maskBits = maskBits;
        return this;
    }

    /**
     * Setzt den Body-Typ der Entität.
     *
     * @param bodyType Der Body-Typ (z. B. Static, Dynamic, Kinematic).
     * @return Der aktuelle Builder für method chaining.
     */
    public EntityBuilder bodyType(BodyDef.BodyType bodyType) {
        this.bodyType = bodyType;
        return this;
    }

    /**
     * Setzt die Rückpralleigenschaft der Entität.
     *
     * @param restitution Der Rückprallwert (0 = kein Rückprall, 1 = vollständiger Rückprall).
     * @return Der aktuelle Builder für method chaining.
     */
    public EntityBuilder restitution(float restitution) {
        this.restitution = restitution;
        return this;
    }

    /**
     * Setzt die Reibungseigenschaft der Entität.
     *
     * @param friction Der Reibungswert (0 = keine Reibung, 1 = maximale Reibung).
     * @return Der aktuelle Builder für method chaining.
     */
    public EntityBuilder friction(float friction) {
        this.friction = friction;
        return this;
    }

    /**
     * Fügt zusätzliche Komponenten zur Entität hinzu.
     *
     * @param componentAdder Ein Consumer, der die zusätzlichen Komponenten definiert.
     * @return Der aktuelle Builder für method chaining.
     */
    public EntityBuilder addComponents(Consumer<Entity> componentAdder) {
        this.additionalComponents = componentAdder;
        return this;
    }

    /**
     * Baut eine Entität mit einer Box2D-Komponente.
     */
    public void build() {
        LoggerUtil.log(LogCategory.GAME,this,"Building entity with position: " + position + ", size: " + width + "x" + height);
        createBox2DComponent();
        additionalComponents.accept(entity);
        engine.addEntity(entity);
        LoggerUtil.log(LogCategory.GAME,this,"Entity built and added to engine: " + entity);
    }

    /**
     * Erstellt die Box2D-Komponente für die Entität.
     */
    private void createBox2DComponent() {
        LoggerUtil.log(LogCategory.GAME,this,"Creating Box2D component with position: " + position + ", bodyType: " + bodyType);
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(position);
        bodyDef.fixedRotation = fixedRotation;
        bodyDef.type = bodyType;

        body = world.createBody(bodyDef);
        body.setUserData(entity);

        box2dComponent.body = body;
        box2dComponent.width = width;
        box2dComponent.height = height;
        box2dComponent.interpolatedRenderPosition.set(body.getPosition());

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.restitution = restitution;
        fixtureDef.friction = friction;
        fixtureDef.filter.categoryBits = categoryBits;
        fixtureDef.filter.maskBits = maskBits;

        fixtureDef.shape = shape;
        fixtureDef.isSensor = isSensor;

        body.createFixture(fixtureDef);
        shape.dispose();

        entity.add(box2dComponent);
        LoggerUtil.log(LogCategory.GAME,this,"Box2D component created and added to entity.");
    }
}
