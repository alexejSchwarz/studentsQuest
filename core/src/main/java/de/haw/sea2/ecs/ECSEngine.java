package de.haw.sea2.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.ecs.components.Box2DComponent;
import de.haw.sea2.ecs.components.PlayerComponent;
import de.haw.sea2.ecs.systems.PlayerCameraSystem;
import de.haw.sea2.ecs.systems.PlayerMovementSystem;
import de.haw.sea2.map.CollisionArea;

/**
 * Create Engine in Main Class
 */
public class ECSEngine extends PooledEngine {

    public static final ComponentMapper<PlayerComponent> PLAYER_COMP_MAPPER = ComponentMapper.getFor(PlayerComponent.class);
    public static final ComponentMapper<Box2DComponent> BOX2D_COMP_MAPPER = ComponentMapper.getFor(Box2DComponent.class);

    private final World world;

    public ECSEngine(final StudentsQuest context) {
        super();
        this.world = context.getWorld();
        this.addSystem(new PlayerMovementSystem(context));
        this.addSystem(new PlayerCameraSystem(context));
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
        StudentsQuest.resetBodieAndFixtureDefinition();
        final Box2DComponent b2dComp = this.createComponent(Box2DComponent.class);

        StudentsQuest.BODY_DEF.position.set(playerSpawnLocation.x, playerSpawnLocation.y);

        StudentsQuest.BODY_DEF.fixedRotation = true;
        StudentsQuest.BODY_DEF.type = BodyDef.BodyType.DynamicBody;
        b2dComp.body = this.world.createBody(StudentsQuest.BODY_DEF);
        b2dComp.body.setUserData("PLAYER");
        b2dComp.width = width;
        b2dComp.height = height;

        StudentsQuest.FIXTURE_DEF.filter.categoryBits = Bits.BIT_PLAYER.value;
        StudentsQuest.FIXTURE_DEF.filter.maskBits = Bits.BIT_WALL.value;
        final PolygonShape pShape = new PolygonShape();
        pShape.setAsBox(width * 0.5f, height * 0.5f);
        StudentsQuest.FIXTURE_DEF.shape = pShape;
        b2dComp.body.createFixture(StudentsQuest.FIXTURE_DEF);
        pShape.dispose();

        player.add(b2dComp);
        this.addEntity(player);
    }

    //TODO check if done here
    public void createCollisionWalls(Array<CollisionArea> collisionAreas) {
        collisionAreas.forEach(collisionArea -> {
            Entity wall = this.createEntity();

            StudentsQuest.resetBodieAndFixtureDefinition();
            final Box2DComponent b2dComp = this.createComponent(Box2DComponent.class);

            StudentsQuest.BODY_DEF.position.set(collisionArea.getX(), collisionArea.getY());
            StudentsQuest.BODY_DEF.type = BodyDef.BodyType.StaticBody;
            b2dComp.body = this.world.createBody(StudentsQuest.BODY_DEF);
            b2dComp.body.setUserData("WALL");

            StudentsQuest.FIXTURE_DEF.filter.categoryBits = Bits.BIT_WALL.value;
            StudentsQuest.FIXTURE_DEF.filter.maskBits = -1; // TODO  "collides with everything" put into the enum

            final ChainShape cShape = new ChainShape();
            cShape.createChain(collisionArea.getVertices());

            StudentsQuest.FIXTURE_DEF.shape = cShape;
            b2dComp.body.createFixture(StudentsQuest.FIXTURE_DEF);
            cShape.dispose();

            wall.add(b2dComp);
            this.addEntity(wall);
        });
    }
}
