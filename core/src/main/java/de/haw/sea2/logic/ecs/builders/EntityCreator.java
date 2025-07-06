package de.haw.sea2.logic.ecs.builders;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import de.haw.sea2.StudentsQuest;
import de.haw.sea2.contact.InteractionType;
import de.haw.sea2.debug.LogCategory;
import de.haw.sea2.debug.LoggerUtil;
import de.haw.sea2.logic.Bits;
import de.haw.sea2.logic.ecs.ECSEngine;
import de.haw.sea2.logic.ecs.components.AnimationComponent;
import de.haw.sea2.logic.ecs.components.EnemyComponent;
import de.haw.sea2.logic.ecs.components.HearthComponent;
import de.haw.sea2.logic.ecs.components.InteractionComponent;
import de.haw.sea2.logic.ecs.components.ItemComponent;
import de.haw.sea2.logic.ecs.components.ItemType;
import de.haw.sea2.logic.ecs.components.ObstacleComponent;
import de.haw.sea2.logic.ecs.components.PlayerAttackStateComponent;
import de.haw.sea2.logic.ecs.components.PlayerComponent;
import de.haw.sea2.logic.ecs.components.SimpleRenderComponent;
import de.haw.sea2.paths.AssetPaths;
import de.haw.sea2.view.animations.BoyPlayerAnimation;
import de.haw.sea2.view.animations.CoinAnimation;
import de.haw.sea2.view.animations.EnemyAnimation;
import de.haw.sea2.view.animations.GirlPlayerAnimation;

/**
 * Diese Klasse bietet Methoden zur Erstellung von Spieler- und Ball-Entitäten
 * sowie generische Builder für flexible Konfigurationen.
 * Für weitere Entitätsarten, nach Muster von Spieler und Ball, eigene Methoden
 * implementieren.
 */
public class EntityCreator {

    public final static float HUMAN_HITBOX_WIDTH = 0.75f;
    public final static float HUMAN_HITBOX_HEIGHT = 1f;
    public final static float HUMAN_ANIMATION_WIDTH = 2f;
    public final static float HUMAN_ANIMATION_HEIGHT = 2f;

    private final ECSEngine engine;
    private final World world;
    private final StudentsQuest context;

    public EntityCreator(StudentsQuest context) {
        this.engine = context.getEngine();
        this.world = context.getWorld();
        this.context = context;
    }

    /**
     * Erstellt einen Spielercharakter in der Spielwelt.
     *
     * @param playerSpawnLocation Die Startposition des Spielers in der Welt
     * @param width               Die Breite des Spielers in Spieleinheiten
     * @param height              Die Höhe des Spielers in Spieleinheiten
     */
    public void createPlayer(final Vector2 playerSpawnLocation, final float width, final float height, float animationWidth, float animationHeight) {
        LoggerUtil.log(LogCategory.GAME, this,
                "Creating player at position: " + playerSpawnLocation + ", width: " + width + ", height: " + height);
        new EntityBuilder(engine, world, createBoxShape(width, height))
                .position(playerSpawnLocation.x, playerSpawnLocation.y)
                .size(width, height)
                .categoryBits(Bits.BIT_PLAYER.value)
                .maskBits((short) (Bits.BIT_WALL.value | Bits.BIT_BALL.value | Bits.BIT_GAME_ENTITY.value | Bits.BIT_ENEMY.value))
                .addComponents(entity -> {
                    PlayerComponent playerComp = engine.createComponent(PlayerComponent.class);
                    playerComp.speed.set(3f, 3f);
                    entity.add(playerComp);

                    HearthComponent hearthComponent = engine.createComponent(HearthComponent.class);
                    hearthComponent.currentHearths = 5;
                    hearthComponent.maxHearths = 5;
                    entity.add(hearthComponent);

                    AnimationComponent animationComp = engine.createComponent(AnimationComponent.class);

                    if (context.chosenPlayerAnimationAtlas == AssetPaths.BOY_PLAYER_ATLAS) {
                        animationComp.animationType = BoyPlayerAnimation.PLAYER_MOVE_DOWN.animationType;
                    } else {
                        animationComp.animationType = GirlPlayerAnimation.PLAYER_MOVE_DOWN.animationType;
                    }

                    animationComp.width = animationWidth;
                    animationComp.height = animationHeight;
                    entity.add(animationComp);

                    PlayerAttackStateComponent attackStateComponent = this.engine.createComponent(PlayerAttackStateComponent.class);
                    attackStateComponent.engine = this.engine;
                    entity.add(attackStateComponent);
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
        new EntityBuilder(engine, world, createCircleShape(size / 2f))
                .position(position.x, position.y)
                .size(size, size)
                .categoryBits(Bits.BIT_BALL.value)
                .maskBits((short) (Bits.BIT_WALL.value | Bits.BIT_PLAYER.value))
                .restitution(0.5f)
                .friction(0.2f)
                .addComponents(entity -> {
                    SimpleRenderComponent renderComp = engine.createComponent(SimpleRenderComponent.class);
                    renderComp.textureFilePath = AssetPaths.BALL.getPath();
                    renderComp.width = size;
                    renderComp.height = size;
                    entity.add(renderComp);

                    InteractionComponent interCopm = engine.createComponent(InteractionComponent.class);
                    interCopm.type = InteractionType.OBJECT_SHRINK;
                    entity.add(interCopm);
                })
                .build();
    }

    public void createCoin(Vector2 position) {
        float size = 0.5f;
        LoggerUtil.log(LogCategory.GAME, this, "Creating Coin at position: " + position + ", size: " + size);
        new EntityBuilder(engine, world, createCircleShape(size / 2f))
            .position(position.x, position.y)
            .size(size, size)
            .categoryBits(Bits.BIT_GAME_ENTITY.value)
            .maskBits((short) (Bits.BIT_WALL.value | Bits.BIT_PLAYER.value))
            .addComponents(entity -> {
                // Use AnimationComponent instead of SimpleRenderComponent for animated coins
                AnimationComponent animComp = engine.createComponent(AnimationComponent.class);
                animComp.animationType = CoinAnimation.GOLD_COIN_SPIN.animationType;
                animComp.width = size;
                animComp.height = size;
                entity.add(animComp);

                InteractionComponent interCopm = engine.createComponent(InteractionComponent.class);
                interCopm.type = InteractionType.OBJECT_COLLECT;
                entity.add(interCopm);

                ItemComponent itemComp = engine.createComponent(ItemComponent.class);
                itemComp.itemType = ItemType.COIN;
                entity.add(itemComp);
            })
            .build();
    }

    public void createDropedHeart(Vector2 position){
        float size = 0.5f;
        LoggerUtil.log(LogCategory.GAME, this, "Creating Heart at position: " + position + ", size: " + size);
        new EntityBuilder(engine, world, createCircleShape(size / 2f))
            .position(position.x, position.y)
            .size(size, size)
            .categoryBits(Bits.BIT_GAME_ENTITY.value)
            .maskBits((short) (Bits.BIT_WALL.value | Bits.BIT_PLAYER.value))
            .setSensor(true)
            .addComponents(entity -> {
                SimpleRenderComponent renderComp = engine.createComponent(SimpleRenderComponent.class);
                renderComp.textureFilePath = AssetPaths.HEARTH.getPath();
                renderComp.width = size;
                renderComp.height = size;
                entity.add(renderComp);

                InteractionComponent interCopm = engine.createComponent(InteractionComponent.class);
                interCopm.type = InteractionType.OBJECT_COLLECT;
                entity.add(interCopm);

                ItemComponent itemComp = engine.createComponent(ItemComponent.class);
                itemComp.itemType = ItemType.HEART;
                entity.add(itemComp);
            })
            .build();
    }

    /**
     * Creates an enemy entity in the game world.
     *
     * @param position The spawn position of the enemy in the world
     * @param width    The width of the enemy in game units
     * @param height   The height of the enemy in game units
     */
    public void createEnemy(Vector2 position, float width, float height, float animationWidth, float animationHeight) {
        LoggerUtil.log(LogCategory.GAME, this,
                "Creating enemy at position: " + position + ", width: " + width + ", height: " + height);
        new EntityBuilder(engine, world, createBoxShape(width, height))
                .position(position.x, position.y)
                .size(width, height)
                .categoryBits(Bits.BIT_ENEMY.value)
                .maskBits((short) (Bits.BIT_WALL.value  | Bits.BIT_ENEMY.value | Bits.BIT_PLAYER.value))
                .addComponents(entity -> {
                    AnimationComponent animationComp = engine.createComponent(AnimationComponent.class);
                    animationComp.animationType = EnemyAnimation.ENEMY_MOVE_DOWN.animationType;
                    animationComp.width = animationWidth;
                    animationComp.height = animationHeight;
                    entity.add(animationComp);

                // Add the enemy component for pathfinding behavior
                EnemyComponent enemyComp = engine.createComponent(EnemyComponent.class);
                // Set custom properties for enemy
                enemyComp.speed.set(2f, 2f);
                enemyComp.pathUpdateTimer = 1f;
                entity.add(enemyComp);

                InteractionComponent interCopm = engine.createComponent(InteractionComponent.class);
                interCopm.type = InteractionType.PLAYER_TAKES_DMG;
                entity.add(interCopm);

                HearthComponent hearthComponent = engine.createComponent(HearthComponent.class);
                hearthComponent.currentHearths = 2;
                hearthComponent.maxHearths = 2;
                entity.add(hearthComponent);
            })
            .build();
    }

    public void createWall(Rectangle rectangle) {
        float width = rectangle.width;
        float height = rectangle.height;
        float xCenter = rectangle.x + 0.5f * width;
        float yCenter = rectangle.y + 0.5f * height;

        LoggerUtil.log(LogCategory.GAME, this,
                "Creating wall at position: " + "(" + xCenter + " , " + xCenter + ")" + ", width: " + width
                        + ", height: " + height);
        new EntityBuilder(engine, world, createBoxShape(width, height))
                .position(xCenter, yCenter)
                .size(width, height)
                .bodyType(BodyDef.BodyType.StaticBody)
                .categoryBits(Bits.BIT_WALL.value)
                .maskBits(Bits.BIT_COLLIDES_WITH_EVERYTHING.value)
                .addComponents(entity -> entity.add(engine.createComponent(ObstacleComponent.class)))
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
