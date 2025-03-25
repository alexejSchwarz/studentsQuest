package de.haw.sea2.ecs.builders;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.CircleShape;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.debug.LogCategory;
import de.haw.sea2.debug.LoggerUtil;
import de.haw.sea2.ecs.ECSEngine;
import de.haw.sea2.ecs.Bits;
import de.haw.sea2.ecs.components.PlayerComponent;
import de.haw.sea2.ecs.components.AnimationComponent;
import de.haw.sea2.ecs.components.SimpleRenderComponent;
import de.haw.sea2.view.animations.PlayerAnimation;

/**
 * Diese Klasse bietet Methoden zur Erstellung von Spieler- und Ball-Entitäten
 * sowie generische Builder für flexible Konfigurationen.
 * Für weitere Entitätsarten, nach Muster von Spieler und Ball, eigene Methoden implementieren.
 */
public class EntityCreator {

    private final ECSEngine engine;
    private final World world;

    public EntityCreator(StudentsQuest context) {
        this.engine = context.getEngine();
        this.world = context.getWorld();
    }

    /**
     * Erstellt einen Spielercharakter in der Spielwelt.
     *
     * @param playerSpawnLocation Die Startposition des Spielers in der Welt
     * @param width               Die Breite des Spielers in Spieleinheiten
     * @param height              Die Höhe des Spielers in Spieleinheiten
     */
    public void createPlayer(final Vector2 playerSpawnLocation, final float width, final float height) {
        LoggerUtil.log(LogCategory.GAME, this,
                "Creating player at position: " + playerSpawnLocation + ", width: " + width + ", height: " + height);
        new EntityBuilder(engine, world, createBoxShape(width, height))
                .position(playerSpawnLocation.x, playerSpawnLocation.y)
                .size(width, height)
                .categoryBits(Bits.BIT_PLAYER.value)
                .maskBits((short) (Bits.BIT_WALL.value | Bits.BIT_BALL.value))
                .addComponents(entity -> {
                    PlayerComponent playerComp = engine.createComponent(PlayerComponent.class);
                    playerComp.speed.set(3f, 3f);
                    entity.add(playerComp);

                    AnimationComponent animationComp = engine.createComponent(AnimationComponent.class);
                    animationComp.animationType = PlayerAnimation.HERO_MOVE_DOWN.animationType;
                    animationComp.width = width;
                    animationComp.height = height;
                    entity.add(animationComp);
                })
                .build();
    }

    /**
     * Erstellt einen Ball in der Spielwelt mit angegebener Position.
     *
     * @param position Die Position des Balls
     * @param size     Die Größe des Balls
     */
    public void createBall(Vector2 position, float size) {
        LoggerUtil.log(LogCategory.GAME, this, "Creating ball at position: " + position + ", size: " + size);
        new EntityBuilder(engine, world, createCircleShape(size / 2))
                .position(position.x, position.y)
                .size(size, size)
                .categoryBits(Bits.BIT_BALL.value)
                .maskBits((short) (Bits.BIT_WALL.value | Bits.BIT_PLAYER.value))
                .restitution(0.5f)
                .friction(0.2f)
                .addComponents(entity -> {
                    SimpleRenderComponent renderComp = engine.createComponent(SimpleRenderComponent.class);
                    renderComp.textureFilePath = "assetsFromTut/Ball.png";
                    renderComp.width = size;
                    renderComp.height = size;
                    entity.add(renderComp);
                })
                .build();
    }

    private Shape createBoxShape(float width, float height) {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);
        return shape;
    }

    private Shape createCircleShape(float radius) {
        CircleShape shape = new CircleShape();
        shape.setRadius(radius);
        return shape;
    }
}