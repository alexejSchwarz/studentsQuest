package de.haw.sea2;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

import de.haw.sea2.ecs.ECSEngine;
import de.haw.sea2.input.GameKey;
import de.haw.sea2.input.InputManager;
import de.haw.sea2.input.KeyInputListener;
import de.haw.sea2.map.GameMap;
import de.haw.sea2.screen.MainMenuScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */

//TODO wirklich implements?
public class StudentsQuest extends Game implements KeyInputListener {

    //scale of the tiles in game from the map
    public static final float UNIT_SCALE = 1 / 64f;

    //For Physics
    public static final BodyDef BODY_DEF = new BodyDef();
    public static final FixtureDef FIXTURE_DEF = new FixtureDef();

    //should not be static
    private AssetManager assetManager;
    private InputManager inputManager;

    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private Box2DDebugRenderer debugRenderer;

    //TODO pass instance to all screens
    private World world;
    private WorldContactListener worldContactListener;

    //TODO use in resize in Screen impl
    //evt ExtendViewPort?
    public FitViewport viewport;
    private OrthographicCamera gameCamera;
    private Stage stage;

    //TODO Use MapManager
    private GameMap map;

    private SpriteBatch spriteBatch;

    private ECSEngine engine;

    @Override
    public void create() {

        this.spriteBatch = new SpriteBatch();

        this.assetManager = new AssetManager();

        this.tiledMapRenderer = new OrthogonalTiledMapRenderer(null, UNIT_SCALE, this.spriteBatch);

        this.world = new World(new Vector2(0f, 0f), true);
        this.worldContactListener = new WorldContactListener();
        this.world.setContactListener(worldContactListener);

        this.debugRenderer = new Box2DDebugRenderer();

        this.gameCamera = new OrthographicCamera();
        // 16f 9f aspect ratio plus window size in logical units
        this.viewport = new FitViewport(16f, 9f, gameCamera);
        this.stage = new Stage(this.viewport);

        this.inputManager = new InputManager();
        this.engine = new ECSEngine(this);

        Gdx.input.setInputProcessor(new InputMultiplexer(this.inputManager,this.stage));
        this.inputManager.addKeyInputListener(this);

        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        //TODO check all fields in class if there is more to dispose
        spriteBatch.dispose();
        world.dispose();
        debugRenderer.dispose();
        this.assetManager.dispose();
        this.tiledMapRenderer.dispose();
        this.tiledMapRenderer.dispose();
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
        //TODO implement or find better place, maybe GameScreen?
    }

    @Override
    public void keyUp(InputManager manager, GameKey key) {
        //TODO implement or find better place, maybe GameScreen?
    }

    public World getWorld() {
        return this.world;
    }

    public AssetManager getAssetManager() {
        return this.assetManager;
    }

    public Box2DDebugRenderer getDebugRenderer() {
        return this.debugRenderer;
    }

    public OrthographicCamera getGameCamera() {
        return this.gameCamera;
    }

    public InputManager getInputManager() {
        return this.inputManager;
    }

    public SpriteBatch getSpriteBatch() {
        return this.spriteBatch;
    }

    public OrthogonalTiledMapRenderer getTiledMapRenderer() {
        return tiledMapRenderer;
    }

    public GameMap getMap() {
        return this.map;
    }

    public void setMap(GameMap map) {
        this.map = map;
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
