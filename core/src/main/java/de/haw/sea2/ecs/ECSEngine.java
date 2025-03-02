package de.haw.sea2.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.ecs.components.Box2DComponent;
import de.haw.sea2.ecs.components.PlayerComponent;
import de.haw.sea2.ecs.systems.PlayerMovementSystem;

/**
 * Create Engine in Main Class
 */
public class ECSEngine extends PooledEngine {

    public static final ComponentMapper<PlayerComponent> PLAYER_COMP_MAPPER = ComponentMapper.getFor(PlayerComponent.class);
    public static final ComponentMapper<Box2DComponent> BOX2D_COMP_MAPPER = ComponentMapper.getFor(Box2DComponent.class);

    private final World world;
    private final BodyDef bodyDef;
    private final FixtureDef fixtureDef;

    public ECSEngine(final StudentsQuest context) {
        super();
        this.world = context.getWorld();
        this.bodyDef = new BodyDef();
        this.fixtureDef = new FixtureDef();

        this.addSystem(new PlayerMovementSystem(context));
    }

    private void resetBodieAndFixtureDefinition() {
        this.bodyDef.position.set(0, 0);
        this.bodyDef.gravityScale = 1;
        this.bodyDef.type = BodyDef.BodyType.StaticBody;
        this.bodyDef.fixedRotation = false;

        this. fixtureDef.density = 0;
        this.fixtureDef.isSensor = false;
        this.fixtureDef.restitution = 0;
        this.fixtureDef.friction = 0.2f;
        this.fixtureDef.filter.categoryBits = 0x0001;
        this.fixtureDef.filter.maskBits = -1;
        this.fixtureDef.shape = null;
    }

    /**
     * Vector2 oder direkte Float Values. Vector2 bietet math. Operationen an etc.
     * @param playerSpawnLocation
     */
    public void createPlayer(final Vector2 playerSpawnLocation, final float width, final float height) {

        final Entity player = this.createEntity();
        final PlayerComponent playerComp = this.createComponent(PlayerComponent.class);
        playerComp.speed.set(3f,3f);
        player.add(playerComp);

        // box2d component
        resetBodieAndFixtureDefinition();
        final Box2DComponent b2dComp = this.createComponent(Box2DComponent.class);

        //TODO change that later
        this.bodyDef.position.set(playerSpawnLocation.x, playerSpawnLocation.y);

        this.bodyDef.fixedRotation = true;
        this.bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2dComp.body = this.world.createBody(this.bodyDef);
        b2dComp.body.setUserData("PLAYER");
        b2dComp.width = width;
        b2dComp.height = height;

        this.fixtureDef.filter.categoryBits = Bits.BIT_PLAYER.value;
        this.fixtureDef.filter.maskBits = Bits.BIT_WALL.value;
        final PolygonShape pShape = new PolygonShape();
        pShape.setAsBox(width * 0.5f, height * 0.5f);
        this.fixtureDef.shape = pShape;
        b2dComp.body.createFixture(this.fixtureDef);
        pShape.dispose();

        player.add(b2dComp);
        this.addEntity(player);

    }
}
