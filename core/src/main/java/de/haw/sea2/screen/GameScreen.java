package de.haw.sea2.screen;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.utils.ScreenUtils;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.ecs.ECSEngine;
import de.haw.sea2.ecs.components.Box2DComponent;
import de.haw.sea2.ecs.components.PlayerComponent;
import de.haw.sea2.map.GameMap;

public class GameScreen implements Screen {

    private final StudentsQuest context;

    //TODO relocate into ecs later
    private Texture playerTexture;

    public GameScreen(StudentsQuest context) {
        this.context = context;
    }

    @Override
    public void show() {

        //physics simulation
        Box2D.init();

        //TODO Auslagern in nen LoadingScreen
        this.context.getAssetManager().setLoader(TiledMap.class, new TmxMapLoader(this.context.getAssetManager().getFileHandleResolver()));
        this.context.getAssetManager().load("mapMitObj.tmx", TiledMap.class);
        this.context.getAssetManager().finishLoading();

        //TODO only temporary remove later rendering of Entities and maybe even asset loading of entities in ecs
        this.context.getAssetManager().setLoader(Texture.class, new TextureLoader(this.context.getAssetManager().getFileHandleResolver()));
        this.context.getAssetManager().load("tmpGuy.png", Texture.class);
        this.context.getAssetManager().finishLoading();

        this.playerTexture = this.context.getAssetManager().get("tmpGuy.png", Texture.class);
        //this.playerSprite = new Sprite(this.playerTexture);

        //TODO auslagern in Loadingscreen
        TiledMap tiledMap = this.context.getAssetManager().get("mapMitObj.tmx", TiledMap.class);
        this.context.getTiledMapRenderer().setMap(tiledMap);

        //TODO handle in map manager? dispose when no longet needed or chache it someHow
        this.context.setMap(new GameMap(tiledMap));

        //TODO entity and components creation in the coresponding GameScreens
        this.context.getEngine().createCollisionWalls(this.context.getMap().getCollisionAreas());

        // sets center of Player entity
        this.context.getEngine().createPlayer(new Vector2(3.5f, 3.5f), 1f, 1f);
    }

    @Override
    public void render(float delta) {


        //float delta = Gdx.graphics.getDeltaTime();
        this.context.getEngine().update(delta);

        //TODO to be removed
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        //TODO look this up
        /*
        stage.getViewport().apply();
        stage.act();
        stage.draw();
        */


        this.context.viewport.apply(false);

        //applies physics
        this.context.getWorld().step(delta, 6,2);

        this.context.getTiledMapRenderer().setView(this.context.getGameCamera());
        this.context.getTiledMapRenderer().render();
        this.context.getDebugRenderer().render(this.context.getWorld(), this.context.viewport.getCamera().combined);

        //TODO this is temporary RenderSystem to be implemented
        ImmutableArray<Entity> entities = this.context.getEngine().getEntitiesFor(Family.all(PlayerComponent.class, Box2DComponent.class).get());
        Entity player = entities.get(0);
        Box2DComponent box2DComponent = ECSEngine.BOX2D_COMP_MAPPER.get(player);

        this.context.getSpriteBatch().begin();
        this.context.getSpriteBatch().draw(this.playerTexture, box2DComponent.body.getPosition().x - 0.5f, box2DComponent.body.getPosition().y - 0.5f, 1f, 1f);
        this.context.getSpriteBatch().end();
    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
        this.context.viewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        this.playerTexture.dispose();
    }
}
