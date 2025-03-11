package de.haw.sea2.view;

import java.util.Optional;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.ecs.ECSEngine;
import de.haw.sea2.ecs.components.AnimationComponent;
import de.haw.sea2.ecs.components.Box2DComponent;
import de.haw.sea2.ecs.components.SimpleRenderComponent;
import de.haw.sea2.map.GameMap;
import de.haw.sea2.map.MapChangeListener;
import de.haw.sea2.view.animations.AnimationType;

/**
 * responsible for drawing the map, Character, Entities in general, light, partical effects
 */
public class GameRenderer implements Disposable, MapChangeListener {

    private final OrthographicCamera gameCamera;
    private final FitViewport viewport;
    private final SpriteBatch spriteBatch;
    private final AssetManager assetManager;
    private final ObjectMap<AnimationType, Animation<Sprite>> animationCache;

    /**
     * Spezifischer Renderer für Tiled-Karten, der die Spielwelt darstellt. Dieser Renderer zeichnet die Kartenkacheln und -objekte mit der richtigenSkalierung und Position auf dem Bildschirm.
     */
    private final OrthogonalTiledMapRenderer mapRenderer;

    // managed and updated by the engine
    private final ImmutableArray<Entity> animatedEntities;
    private final ImmutableArray<Entity> unanimatedEntities;

    private final Box2DDebugRenderer debugRenderer;
    private final World world;

    private final Array<TiledMapTileLayer> tiledMapLayers;

    public GameRenderer(StudentsQuest context) {
        this.assetManager = context.getAssetManager();
        this.viewport = context.viewport;
        this.gameCamera = context.getGameCamera();
        this.spriteBatch = context.getSpriteBatch();
        this.animatedEntities = context.getEngine().getEntitiesFor(Family.all(AnimationComponent.class, Box2DComponent.class).get());
        this.unanimatedEntities = context.getEngine().getEntitiesFor(Family.all(SimpleRenderComponent.class, Box2DComponent.class).get());
        this.animationCache = new ObjectMap<>();

        // Richtet den TiledMapRenderer für die Spielkarten ein
        this.mapRenderer = new OrthogonalTiledMapRenderer(null, StudentsQuest.UNIT_SCALE, this.spriteBatch);
        this.tiledMapLayers = new Array<>();

        if (StudentsQuest.DEBUG) {
            this.debugRenderer = new Box2DDebugRenderer();
            this.world = context.getWorld();
        } else {
            this.debugRenderer = null;
            this.world = null;
        }
    }

    public void render(final float alpha) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        this.viewport.apply(false);

        // set view regardless if there is a map or not. Internally the passed SpriteBatch gets configured batch.setProjectionMatrix(camera.combined);
        // example no mao is present, but Entities are to be rendered. So Spritebatch projectionMatrix should be set
        this.mapRenderer.setView(this.gameCamera);

        this.spriteBatch.begin();
        if (this.mapRenderer.getMap() != null) {
            for (final TiledMapTileLayer layer : this.tiledMapLayers) {
                this.mapRenderer.renderTileLayer(layer);
            }
        }

        // nicht animierte nicht atlas texturen
        for (Entity entity : this.unanimatedEntities) {
            renderNonAnimatedEntities(entity, alpha);
        }

        for (Entity entity : animatedEntities) {
            renderAnimatedEntity(entity, alpha);
        }
        spriteBatch.end();

        if (StudentsQuest.DEBUG) {
            // profiler hier
            this.debugRenderer.render(this.world, this.gameCamera.combined);
        }
    }

    private void renderNonAnimatedEntities(Entity entity, final float alpha) {
        Box2DComponent b2dComp = ECSEngine.BOX2D_COMP_MAPPER.get(entity);
        SimpleRenderComponent simpleRenderComponent = ECSEngine.SIMPLE_RENDER_COMPONENT_COMPONENT_MAPPER.get(entity);
        Sprite sprite = new Sprite(this.assetManager.get(simpleRenderComponent.textureFilePath, Texture.class));
        sprite.setOriginCenter();
        drawInterpolatedEntity(sprite, b2dComp, alpha, simpleRenderComponent.width, simpleRenderComponent.height);
    }

    private void renderAnimatedEntity(Entity entity, float alpha) {
        Box2DComponent b2dComp = ECSEngine.BOX2D_COMP_MAPPER.get(entity);
        AnimationComponent animationComponent = ECSEngine.ANIMATION_COMP_MAPPER.get(entity);

        //throws RuntimeException if animationType is null
        Optional.ofNullable(animationComponent.animationType)
            .orElseThrow(() -> new RuntimeException("No AnimationType found for animated Entity"));

        Animation<Sprite> animation = getAnimation(animationComponent.animationType);
        Sprite frame = animation.getKeyFrame(animationComponent.animationTime);
        drawInterpolatedEntity(frame, b2dComp, alpha, animationComponent.width, animationComponent.height);

    }

    /**
     * Uses Interpolation for smoother in between rendering of frames.
     * For reference see: https://www.youtube.com/watch?v=09z4UTdWM8M&list=PLTKHCDn5RKK-seXZveiSQuSXkLq3wBYn1&index=22 11:38
     * https://www.youtube.com/watch?v=4JOqn-ZKA8Y&list=PLTKHCDn5RKK-seXZveiSQuSXkLq3wBYn1&index=30
     */
    private void drawInterpolatedEntity(Sprite frame, Box2DComponent b2dComp, float alpha, float width, float height) {
        frame.setBounds(b2dComp.interpolatedRenderPosition.x - width * 0.5f, b2dComp.interpolatedRenderPosition.y - b2dComp.height * 0.5f, width, height);
        frame.draw(spriteBatch);

        //interpolate renderposition
        b2dComp.interpolatedRenderPosition.lerp(b2dComp.body.getPosition(), alpha);

        float interpolatedX = MathUtils.lerp(b2dComp.previousX, b2dComp.body.getPosition().x, alpha);
        float interpolatedy = MathUtils.lerp(b2dComp.previousY, b2dComp.body.getPosition().y, alpha);

        b2dComp.interpolatedRenderPosition.set(interpolatedX, interpolatedy);
    }

    /**
     * retrieve Animation from Atlas. Save it in a cache and use it for later retrievals
     */
    private Animation<Sprite> getAnimation(AnimationType animationType) {
        Animation<Sprite> animation = this.animationCache.get(animationType);
        if (animation == null) {

            // if animationType

            // create Animation
            Gdx.app.debug("TAG", "Creating new animation of type: " + animationType);
            TextureAtlas.AtlasRegion atlasRegion = this.assetManager.get(animationType.atlasPath(), TextureAtlas.class).findRegion(animationType.atlasKey());

            //TODO in dem Bsp 64 x 64, spaeter evt 32 * 32??
            final TextureRegion[][] textureRegions = atlasRegion.split(64, 64);
            animation = new Animation<>(animationType.frameTime(), getKeyFrames(textureRegions[animationType.rowIndex()]), Animation.PlayMode.LOOP);
            this.animationCache.put(animationType, animation);
        }
        return animation;
    }

    private Array<? extends Sprite> getKeyFrames(TextureRegion[] textureRegion) {
        Array<Sprite> keyFrames = new Array<>();

        for (TextureRegion region : textureRegion) {
            Sprite sprite = new Sprite(region);
            sprite.setOriginCenter();
            keyFrames.add(sprite);
        }

        return keyFrames;
    }

    @Override
    public void onMapChange(GameMap map) {
        this.mapRenderer.setMap(map.getTiledMap());
        map.getTiledMap().getLayers().getByType(TiledMapTileLayer.class, tiledMapLayers);
    }

    public OrthogonalTiledMapRenderer getMapRenderer() {
        return this.mapRenderer;
    }

    @Override
    public void dispose() {
        if (this.debugRenderer != null) {
            this.debugRenderer.dispose();
        }
        this.mapRenderer.dispose();
    }
}
