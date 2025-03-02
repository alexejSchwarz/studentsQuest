package de.haw.sea2;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import de.haw.sea2.ecs.ECSEngine;
import de.haw.sea2.input.GameKey;
import de.haw.sea2.input.InputManager;
import de.haw.sea2.input.KeyInputListener;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */

//TODO wirklich implements?
public class StudentsQuest extends Game implements KeyInputListener {

    //For Physics
    private BodyDef bodyDef; // consists of Fixtures
    private FixtureDef fixtureDef;

    //TODO pass instance to all screens
    private World world;

    //TODO use in resize in Screen impl
    //evt ExtendViewPort?
    public FitViewport viewport;

    //TODO change later
    private SpriteBatch batch;
    private Texture image;

    private Stage stage;

    public InputManager getInputManager() {
        return inputManager;
    }

    private InputManager inputManager;
    private ECSEngine engine;

    public Box2DDebugRenderer getDebugRenderer() {
        return debugRenderer;
    }

    private Box2DDebugRenderer debugRenderer;

    @Override
    public void create() {

        //gravity? needed?
        //physics simulation
        Box2D.init();

        batch = new SpriteBatch();
        image = new Texture("libgdx.png");
        this.world = new World(new Vector2(0f, 0f), true);
        //TODO more world stuff

        this.bodyDef = new BodyDef();
        this.fixtureDef = new FixtureDef();
        this.debugRenderer = new Box2DDebugRenderer();

        this.viewport = new FitViewport(16f, 9f);
        this.stage = new Stage(this.viewport);
        this.inputManager = new InputManager();
        this.engine = new ECSEngine(this);

        Gdx.input.setInputProcessor(new InputMultiplexer(this.inputManager,this.stage));
        this.inputManager.addKeyInputListener(this);

        //TODO relocate to GameScreen later

        // sets center of Pplayer entity
        this.engine.createPlayer(new Vector2(0.5f, 0.5f), 1f, 1f);
    }

    public void setScreen(){
        //TODO implement
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        this.engine.update(delta);

        //TODO to be removed
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(image, 140, 210);
        batch.end();

        //TODO look this up
        /*
        stage.getViewport().apply();
        stage.act();
        stage.draw();
        */


        this.viewport.apply(true);
        //applies physics
        this.world.step(delta, 6,2);
        this.debugRenderer.render(this.world, this.viewport.getCamera().combined);
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
        world.dispose();
        debugRenderer.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
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
        this.bodyDef.position.set(5f, 5f);
        this.bodyDef.gravityScale = 1;
        this.bodyDef.type = BodyDef.BodyType.DynamicBody;
        final Body body = this.world.createBody(bodyDef);

        this.fixtureDef.isSensor = false;

        //bouncyness 1 is super bouncy
        this.fixtureDef.restitution = 0.5f;

        //stickyness
        this.fixtureDef.friction = 0.2f;


        // defines to which tcategory this fixture belongs
        this.fixtureDef.filter.categoryBits = BIT_CIRCLE;

        // defines with which fixture categories this one collides with
        this.fixtureDef.filter.maskBits = BIT_GROUND | BIT_BOX;

        CircleShape cShape = new CircleShape();
        cShape.setRadius(0.5f);

        this.fixtureDef.shape = cShape;
        body.createFixture(this.fixtureDef);
        cShape.dispose();

        //TODO HIER WEITERMACHEN
        //https://www.youtube.com/watch?v=ouPL6qYN8lQ&list=PLTKHCDn5RKK-seXZveiSQuSXkLq3wBYn1&index=6
        //28:12

    }*/
}
