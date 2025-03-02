package de.haw.sea2;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import de.haw.sea2.ecs.ECSEngine;
import de.haw.sea2.ecs.components.Box2DComponent;
import de.haw.sea2.ecs.components.PlayerComponent;
import de.haw.sea2.input.GameKey;
import de.haw.sea2.input.InputManager;
import de.haw.sea2.input.KeyInputListener;
import de.haw.sea2.map.GameMap;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */

//TODO wirklich implements?
public class StudentsQuest extends Game implements KeyInputListener {

    //scale of the tiles in game from the map
    public static final float UNIT_SCALE = 1 / 64f;

    //should not be static
    private AssetManager assetManager;

    private OrthogonalTiledMapRenderer tiledMapRenderer;
    //TODO find more fitting name
    private GameMap map;

    //For Physics
    public static final BodyDef BODY_DEF = new BodyDef();
    public static final FixtureDef FIXTURE_DEF = new FixtureDef();

    //TODO pass instance to all screens
    private World world;
    private WorldContactListener worldContactListener;

    //TODO use in resize in Screen impl
    //evt ExtendViewPort?
    public FitViewport viewport;
    private OrthographicCamera gameCamera;

    //TODO change later
    private SpriteBatch batch;
    private Texture image;

    private Stage stage;

    private InputManager inputManager;
    private ECSEngine engine;

    private Box2DDebugRenderer debugRenderer;

    //TODO relocate into ecs later
    private Texture playerTexture;
    private Sprite playerSprite;

    @Override
    public void create() {

        //gravity? needed?
        //physics simulation
        Box2D.init();

        batch = new SpriteBatch();
        image = new Texture("libgdx.png");

        //TODO Auslagern in nen LoadingScreen

        this.assetManager = new AssetManager();
        this.assetManager.setLoader(TiledMap.class, new TmxMapLoader(this.assetManager.getFileHandleResolver()));
        this.assetManager.load("mapMitObj.tmx", TiledMap.class);
        this.assetManager.finishLoading();

        //TODO only temporary remove later
        this.assetManager.load("tmpGuy.png", Texture.class);
        this.assetManager.finishLoading();
        this.playerTexture = this.assetManager.get("tmpGuy.png", Texture.class);
        this.playerSprite = new Sprite(this.playerTexture);

        this.tiledMapRenderer = new OrthogonalTiledMapRenderer(null, UNIT_SCALE, this.batch);

        // auslagern in screen
        TiledMap tiledMap = this.assetManager.get("mapMitObj.tmx", TiledMap.class);
        this.tiledMapRenderer.setMap(tiledMap);
        this.map = new GameMap(tiledMap);

        this.world = new World(new Vector2(0f, 0f), true);
        this.worldContactListener = new WorldContactListener();
        this.world.setContactListener(worldContactListener);
        //TODO more world stuff

        this.debugRenderer = new Box2DDebugRenderer();

        this.gameCamera = new OrthographicCamera();
        this.viewport = new FitViewport(16f, 9f, gameCamera);
        this.stage = new Stage(this.viewport);

        this.inputManager = new InputManager();
        this.engine = new ECSEngine(this);

        Gdx.input.setInputProcessor(new InputMultiplexer(this.inputManager,this.stage));
        this.inputManager.addKeyInputListener(this);


        //TODO makle it work
        this.engine.createCollisionWalls(map.getCollisionAreas());

        // sets center of Pplayer entity
        this.engine.createPlayer(new Vector2(3.5f, 3.5f), 1f, 1f);
    }



    public void setScreen(){
        //TODO implement
    }

    @Override
    public void render() {

        /*
        assetManager.getProgress()
        if (this.assetManager.update()) {
            // change to next screen
        }*/

        float delta = Gdx.graphics.getDeltaTime();
        this.engine.update(delta);

        //TODO to be removed
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        //TODO look this up
        /*
        stage.getViewport().apply();
        stage.act();
        stage.draw();
        */


        this.viewport.apply(false);

        //applies physics
        this.world.step(delta, 6,2);

        this.tiledMapRenderer.setView(this.gameCamera);
        this.tiledMapRenderer.render();

        this.debugRenderer.render(this.world, this.viewport.getCamera().combined);

        //TODO this is temporary RenderSystem to be implemented
        ImmutableArray<Entity> entities = this.engine.getEntitiesFor(Family.all(PlayerComponent.class, Box2DComponent.class).get());
        Entity player = entities.get(0);
        Box2DComponent box2DComponent = ECSEngine.BOX2D_COMP_MAPPER.get(player);

        this.batch.begin();
        this.batch.draw(this.playerTexture, box2DComponent.body.getPosition().x - 0.5f, box2DComponent.body.getPosition().y - 0.5f, 1f, 1f);
        this.batch.end();

    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
        world.dispose();
        debugRenderer.dispose();
        this.assetManager.dispose();
        this.tiledMapRenderer.dispose();
        this.tiledMapRenderer.dispose();
        //TODO only tmp render
        this.playerTexture.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    public static void resetBodieAndFixtureDefinition() {
        BODY_DEF.position.set(0, 0);
        BODY_DEF.gravityScale = 1;
        BODY_DEF.type = BodyDef.BodyType.StaticBody;
        BODY_DEF.fixedRotation = false;

        FIXTURE_DEF.density = 0;
        FIXTURE_DEF.isSensor = false;
        FIXTURE_DEF.restitution = 0;
        FIXTURE_DEF.friction = 0.2f;
        FIXTURE_DEF.filter.categoryBits = 0x0001;
        FIXTURE_DEF.filter.maskBits = -1;
        FIXTURE_DEF.shape = null;
    }

    public ECSEngine getEngine() {
        return engine;
    }

    @Override
    public void keyDown(InputManager manager, GameKey key) {
        //TODO implement
    }

    @Override
    public void keyUp(InputManager manager, GameKey key) {
        //TODO implement
    }

    public World getWorld() {
        return world;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public Box2DDebugRenderer getDebugRenderer() {
        return debugRenderer;
    }

    public OrthographicCamera getGameCamera() {
        return gameCamera;
    }

    public InputManager getInputManager() {
        return inputManager;
    }


    /*public static final short BIT_CIRCLE = 1 << 0;
    public static final short BIT_BOX = 1 << 1;
    public static final short BIT_GROUND = 1 << 2;*/

    /**
     * physics example for circle falling onto a plattform
     */
    /*private void exampleBodyFixtureStuff() {

        // you can reuse body and fixture for other objects. createFixture should overrite previous configs

        //--circle
        //this is center of body
        this.BODY_DEF.position.set(5f, 5f);
        this.BODY_DEF.gravityScale = 1;
        this.BODY_DEF.type = BodyDef.BodyType.DynamicBody;
        final Body body = this.world.createBody(BODY_DEF);

        this.FIXTURE_DEF.isSensor = false;

        //bouncyness 1 is super bouncy
        this.FIXTURE_DEF.restitution = 0.5f;

        //stickyness
        this.FIXTURE_DEF.friction = 0.2f;


        // defines to which tcategory this fixture belongs
        this.FIXTURE_DEF.filter.categoryBits = BIT_CIRCLE;

        // defines with which fixture categories this one collides with
        this.FIXTURE_DEF.filter.maskBits = BIT_GROUND | BIT_BOX;

        CircleShape cShape = new CircleShape();
        cShape.setRadius(0.5f);

        this.FIXTURE_DEF.shape = cShape;
        body.createFixture(this.FIXTURE_DEF);
        cShape.dispose();
    }*/
}
