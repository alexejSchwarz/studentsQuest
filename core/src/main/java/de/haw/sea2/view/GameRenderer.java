package de.haw.sea2.view;

import static de.haw.sea2.logic.ecs.builders.EntityCreator.*;

import java.util.Optional;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.debug.DebugConfig;
import de.haw.sea2.logic.ecs.builders.EntityCreator;
import de.haw.sea2.logic.ecs.components.AnimationComponent;
import de.haw.sea2.logic.ecs.components.Box2DComponent;
import de.haw.sea2.logic.ecs.components.PlayerAttackStateComponent;
import de.haw.sea2.logic.ecs.components.PlayerComponent;
import de.haw.sea2.logic.ecs.components.SimpleRenderComponent;
import de.haw.sea2.logic.EntityUtils;
import de.haw.sea2.logic.ecs.ECSEngine;
import de.haw.sea2.logic.entityLogic.PlayerAttackState;
import de.haw.sea2.map.GameMap;
import de.haw.sea2.map.MapChangeListener;
import de.haw.sea2.paths.AssetPaths;
import de.haw.sea2.view.animations.AnimationType;
import de.haw.sea2.view.animations.AnimationUtils;
import de.haw.sea2.view.animations.PlayerAttackAnimation;

/**
 * responsible for drawing the map, Character, Entities in general, light,
 * partical effects
 */
public class GameRenderer implements Disposable, MapChangeListener {

    private final OrthographicCamera gameCamera;
    private final FitViewport viewport;
    private final SpriteBatch spriteBatch;
    private final AssetManager assetManager;

    /**
     * Spezifischer Renderer für Tiled-Karten, der die Spielwelt darstellt. Dieser
     * Renderer zeichnet die Kartenkacheln und -objekte mit der richtigenSkalierung
     * und Position auf dem Bildschirm.
     */
    private final OrthogonalTiledMapRenderer mapRenderer;

    // managed and updated by the engine
    private final ImmutableArray<Entity> animatedEntities;
    private final ImmutableArray<Entity> unanimatedEntities;

    private final Box2DDebugRenderer box2Ddebugrenderer;
    private final World world;

    private final Array<TiledMapTileLayer> tiledMapLayers;

    private final ImmutableArray<Entity> players;

    private final StudentsQuest context;

    private TextureAtlas attackAtlas;

    public GameRenderer(StudentsQuest context) {
        this.context = context;
        this.assetManager = context.getAssetManager();
        this.viewport = context.viewport;
        this.gameCamera = context.getGameCamera();
        this.spriteBatch = context.getSpriteBatch();
        this.animatedEntities = context.getEngine()
                .getEntitiesFor(Family.all(AnimationComponent.class, Box2DComponent.class).get());
        this.unanimatedEntities = context.getEngine()
                .getEntitiesFor(Family.all(SimpleRenderComponent.class, Box2DComponent.class).get());

        this.players = context.getEngine().getEntitiesFor(Family.all(PlayerComponent.class).get());

        // Richtet den TiledMapRenderer für die Spielkarten ein
        this.mapRenderer = new OrthogonalTiledMapRenderer(null, StudentsQuest.UNIT_SCALE, this.spriteBatch);
        this.tiledMapLayers = new Array<>();

        if (DebugConfig.DEBUG_ENABLED) { // TODO: eventuell in debug Package auslagern
            this.box2Ddebugrenderer = new Box2DDebugRenderer();
            this.world = context.getWorld();
        } else {
            this.box2Ddebugrenderer = null;
            this.world = null;
        }
    }

    public void render(final float alpha) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        this.viewport.apply(false);

        // set view regardless if there is a map or not. Internally the passed
        // SpriteBatch gets configured batch.setProjectionMatrix(camera.combined);
        // example no mao is present, but Entities are to be rendered. So Spritebatch
        // projectionMatrix should be set
        this.mapRenderer.setView(this.gameCamera);

        this.spriteBatch.begin();
        if (this.mapRenderer.getMap() != null) {
            for (int i = 0; i < this.tiledMapLayers.size - 2; i++) {
                this.mapRenderer.renderTileLayer(this.tiledMapLayers.get(i));
            }
        }

        // nicht animierte nicht atlas texturen
        for (Entity entity : this.unanimatedEntities) {
            renderNonAnimatedEntities(entity, alpha);
        }
        Entity playerEntity = EntityUtils.checkAndGetPlayer(this.players);

        // Alle animierten Entities außer Spieler rendern
        for (Entity entity : animatedEntities) {
            if (entity != playerEntity) {
                renderAnimatedEntity(entity, alpha);
            }
        }

        // Spieler zuletzt rendern (über Herzen etc.)
        if (playerEntity != null) {
            renderAnimatedEntity(playerEntity, alpha);
        }

        // letzte forground Layer rendern (2.5D)
        this.mapRenderer.renderTileLayer(tiledMapLayers.get(tiledMapLayers.size-2));
        this.mapRenderer.renderTileLayer(tiledMapLayers.get(tiledMapLayers.size-1));


        PlayerAttackStateComponent playerAttackStateComponent = ECSEngine.PLAYER_ATTACK_STATE_COMPONENT_MAPPER.get(playerEntity);
        if (playerAttackStateComponent.stateMachine.isInState(PlayerAttackState.ATTACKING)) {
            renderAttack(playerEntity, playerAttackStateComponent);
        }

        spriteBatch.end();

        if (DebugConfig.DEBUG_ENABLED) {
            // profiler hier
            this.box2Ddebugrenderer.render(this.world, this.gameCamera.combined);
        }
    }

    private void renderNonAnimatedEntities(Entity entity, final float alpha) {
        Box2DComponent b2dComp = ECSEngine.BOX2D_COMP_MAPPER.get(entity);
        SimpleRenderComponent simpleRenderComponent = ECSEngine.SIMPLE_RENDER_COMPONENT_MAPPER.get(entity);
        Sprite sprite = new Sprite(this.assetManager.get(simpleRenderComponent.textureFilePath, Texture.class));
        sprite.setOriginCenter();
        drawInterpolatedEntity(sprite, b2dComp, alpha, simpleRenderComponent.width, simpleRenderComponent.height);
    }

    private void renderAnimatedEntity(Entity entity, float alpha) {
        Box2DComponent b2dComp = ECSEngine.BOX2D_COMP_MAPPER.get(entity);
        AnimationComponent animationComponent = ECSEngine.ANIMATION_COMP_MAPPER.get(entity);

        // throws RuntimeException if animationType is null
        AnimationType animationType = Optional.ofNullable(animationComponent.animationType)
                .orElseThrow(() -> new RuntimeException("No AnimationType found for animated Entity"));

        TextureAtlas atlas = this.assetManager.get(animationType.atlasPath(), TextureAtlas.class);
        Animation<Sprite> animation = AnimationUtils.getAnimation(animationType, atlas);
        Sprite frame = animation.getKeyFrame(animationComponent.animationTime);
        drawInterpolatedEntity(frame, b2dComp, alpha, animationComponent.width, animationComponent.height);

    }

    private void renderAttack(Entity player, PlayerAttackStateComponent playerAttackStateComponent) {
        Box2DComponent b2dComp = ECSEngine.BOX2D_COMP_MAPPER.get(player);
        switch (playerAttackStateComponent.activatedSensor) {
            case UP: {
                Sprite sprite = new Sprite(this.attackAtlas.findRegion(PlayerAttackAnimation.ATTACK_TOP.attalasKey));
                Vector2 startPosition = new Vector2(b2dComp.interpolatedRenderPosition.x - 0.5f * HUMAN_ANIMATION_HEIGHT, b2dComp.interpolatedRenderPosition.y);
                drawAttack(sprite, startPosition);
                break;
            }
            case LEFT: {
                Sprite sprite = new Sprite(this.attackAtlas.findRegion(PlayerAttackAnimation.ATTACK_LEFT.attalasKey));
                Vector2 startPosition = new Vector2(b2dComp.interpolatedRenderPosition.x - HUMAN_ANIMATION_WIDTH, b2dComp.interpolatedRenderPosition.y - 0.5f * HUMAN_ANIMATION_HEIGHT);
                drawAttack(sprite, startPosition);
                break;
            }
            case RIGHT: {
                Sprite sprite = new Sprite(this.attackAtlas.findRegion(PlayerAttackAnimation.ATTACK_RIGHT.attalasKey));
                Vector2 startPosition = new Vector2(b2dComp.interpolatedRenderPosition.x, b2dComp.interpolatedRenderPosition.y - 0.5f * HUMAN_ANIMATION_HEIGHT);
                drawAttack(sprite, startPosition);
                break;
            }
            case DOWN: {
                Sprite sprite = new Sprite(this.attackAtlas.findRegion(PlayerAttackAnimation.ATTACK_DOWN.attalasKey));
                Vector2 startPosition = new Vector2(b2dComp.interpolatedRenderPosition.x - 0.5f * HUMAN_ANIMATION_WIDTH, b2dComp.interpolatedRenderPosition.y - HUMAN_ANIMATION_HEIGHT);
                drawAttack(sprite, startPosition);
                break;
            }
            default: {
                throw new RuntimeException();
            }
        }
    }

    private void drawAttack(Sprite sprite, Vector2 startPoint) {
        sprite.setOriginCenter();
        sprite.setBounds(
            startPoint.x,
            startPoint.y,
            HUMAN_ANIMATION_WIDTH,
            HUMAN_ANIMATION_HEIGHT
        );
        sprite.draw(spriteBatch);
    }

    /**
     * Uses Interpolation for smoother in between rendering of frames.
     * For reference see:
     * https://www.youtube.com/watch?v=09z4UTdWM8M&list=PLTKHCDn5RKK-seXZveiSQuSXkLq3wBYn1&index=22
     * 11:38
     * https://www.youtube.com/watch?v=4JOqn-ZKA8Y&list=PLTKHCDn5RKK-seXZveiSQuSXkLq3wBYn1&index=30
     */
    private void drawInterpolatedEntity(Sprite frame, Box2DComponent b2dComp, float alpha, float width, float height) {
        frame.setBounds(
            b2dComp.interpolatedRenderPosition.x - width * 0.5f,
            b2dComp.interpolatedRenderPosition.y - height * 0.5f, width, height
        );
        frame.draw(spriteBatch);

        // interpolate renderposition
        b2dComp.interpolatedRenderPosition.lerp(b2dComp.body.getPosition(), alpha);

        float interpolatedX = MathUtils.lerp(b2dComp.previousX, b2dComp.body.getPosition().x, alpha);
        float interpolatedy = MathUtils.lerp(b2dComp.previousY, b2dComp.body.getPosition().y, alpha);

        b2dComp.interpolatedRenderPosition.set(interpolatedX, interpolatedy);
    }

    @Override
    public void onMapChange(GameMap map) {
        this.mapRenderer.setMap(map.getTiledMap());
        map.getTiledMap().getLayers().getByType(TiledMapTileLayer.class, tiledMapLayers);
        this.attackAtlas = context.getAssetManager().get(AssetPaths.ATTACK_ATLAS.getPath(), TextureAtlas.class);
    }

    public OrthogonalTiledMapRenderer getMapRenderer() {
        return this.mapRenderer;
    }

    @Override
    public void dispose() {
        if (this.box2Ddebugrenderer != null) {
            this.box2Ddebugrenderer.dispose();
        }
        this.mapRenderer.dispose();
    }
}
