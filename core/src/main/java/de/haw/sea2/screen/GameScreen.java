package de.haw.sea2.screen;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import de.haw.sea2.debug.LogCategory;
import de.haw.sea2.debug.LoggerUtil;
import de.haw.sea2.StudentsQuest;
import de.haw.sea2.debug.DebugConfig;
import de.haw.sea2.debug.render.GameScreenDebugRenderer;
import de.haw.sea2.ecs.ECSEngine;
import de.haw.sea2.ecs.components.Box2DComponent;
import de.haw.sea2.ecs.systems.PlayerMovementSystem;
import de.haw.sea2.input.GameKey;
import de.haw.sea2.input.InputManager;
import de.haw.sea2.input.KeyInputListener;
import de.haw.sea2.paths.MapPaths;
import de.haw.sea2.view.GameRenderer;

/**
 * Der Haupt-Spielbildschirm, der die aktive Spielwelt darstellt und verwaltet.
 *
 * <p>
 * Diese Klasse implementiert das libGDX Screen-Interface und ist verantwortlich
 * für:
 * <ul>
 * <li>Die Initialisierung der Spielwelt und Physik</li>
 * <li>Das Laden und Initialisieren der Spielkarte (TiledMap)</li>
 * <li>Das Erstellen des Spielercharakters und der Kollisionswände</li>
 * <li>Die Aktualisierung und Darstellung der Spielwelt in jedem Frame</li>
 * </ul>
 * </p>
 *
 * <p>
 * Die Klasse folgt dem libGDX-Lebenszyklus für Screens mit Methoden wie show(),
 * render(), resize(), pause(), resume(), hide() und dispose().
 * </p>
 */
public class GameScreen implements Screen, KeyInputListener {

    /**
     * Der Hauptkontext des Spiels, der Zugriff auf zentrale Ressourcen und Systeme
     * bietet.
     *
     * <p>
     * Über dieses Objekt hat der GameScreen Zugriff auf wichtige Komponenten wie:
     * <ul>
     * <li>Die Physik-Welt (World)</li>
     * <li>Das Entity-Component-System (ECSEngine)</li>
     * <li>Den Asset-Manager zum Laden von Ressourcen</li>
     * <li>Die Kamera und Viewport für die Darstellung</li>
     * <li>Render-Komponenten wie SpriteBatch und TiledMapRenderer</li>
     * </ul>
     * </p>
     */
    private final StudentsQuest context;

    private final ECSEngine engine;

    private float accumulator;

    private final GameRenderer gameRenderer;

    private final World world;

    private GameScreenDebugRenderer debugRenderer;

    public GameScreen(StudentsQuest context) {
        this.context = context;
        this.engine = this.context.getEngine();
        this.gameRenderer = this.context.getGameRenderer();
        this.world = this.context.getWorld();
        initialize();
    }

    /**
     * Wird aufgerufen, wenn dieser Screen der aktive Screen wird.
     */
    @Override
    public void show() {
        // Es sollte sichergestellt werden, dass der KeyInputListener registriert ist,
        // falls wir ihn mit hide() entfernt haben
        if (!this.context.getInputManager().getKeyInputListeners().contains(this, true)) {
            this.context.getInputManager().addKeyInputListener(this);
        }
        // Stelle sicher, dass das PlayerMovementSystem als KeyInputListener registriert
        // ist
        PlayerMovementSystem playerMovementSystem = this.context.getEngine().getSystem(PlayerMovementSystem.class);
        if (!this.context.getInputManager().getKeyInputListeners().contains(playerMovementSystem, true)) {
            this.context.getInputManager().addKeyInputListener(playerMovementSystem);
        }

        // Debug-Renderer erstellen und registrieren
        if (DebugConfig.DEBUG_ENABLED) {
            debugRenderer = new GameScreenDebugRenderer(context);
            context.getDebugSystem().addRenderer(debugRenderer);
        }

    }

    private void initialize() {
        //Erstellung von Spieler soll hier passieren und nicht im show(), da sonst Duplikate entstehen


        // Map aktivieren (nicht mehr laden!)
        this.context.getMapManager().activateMap(MapPaths.MAINMAP.getPath());

        //TODO auslagern?
        // Die Position ist relativ zur Box2D-Welt und nicht zu Pixeln auf dem
        // Bildschirm

        Vector2 playerSpawnPosition = this.context.getMapManager().getCurrentMap().getPlayerSpawnPoint();
        LoggerUtil.log(LogCategory.DEBUG, this, "player to be created at: " + playerSpawnPosition);
        this.context.getEntityFactory().createPlayer(playerSpawnPosition, 1f, 1f);
        this.context.getEntityFactory().createBall();

        // Registriere diesen Screen als KeyInputListener
        this.context.getInputManager().addKeyInputListener(this);
    }

    /**
     * Wird in jedem Frame aufgerufen, um die Spielwelt zu aktualisieren und
     * darzustellen.
     */
    @Override
    public void render(float delta) {

        final float deltaTime = Math.min(0.25f, Gdx.graphics.getRawDeltaTime());
        this.engine.update(deltaTime);

        // Fixierung fuer die Physics berechnung
        this.accumulator += deltaTime;
        while (this.accumulator >= StudentsQuest.FIXED_TIME_STEP) {

            // remembers the previous position for later interpolation
            for (Entity entity : this.engine.getEntitiesFor(Family.all(Box2DComponent.class).get())) {
                Box2DComponent b2dComp = ECSEngine.BOX2D_COMP_MAPPER.get(entity);
                b2dComp.previousX = b2dComp.body.getPosition().x;
                b2dComp.previousY = b2dComp.body.getPosition().y;
            }

            this.world.step(StudentsQuest.FIXED_TIME_STEP, 6, 2);
            this.accumulator -= StudentsQuest.FIXED_TIME_STEP;
        }

        // interpolation rendering. reduces stuttering between frames
        this.gameRenderer.render(this.accumulator / StudentsQuest.FIXED_TIME_STEP); //alpha value

        // TODO look this up
        /*
         * stage.getViewport().apply();
         * stage.act(deltaTime);
         * stage.draw(deltaTime);
         */
    }

    /**
     * Wird aufgerufen, wenn die Größe des Fensters geändert wird.
     *
     * <p>
     * Aktualisiert den Viewport, damit die Grafiken korrekt skaliert werden.
     * Der Parameter true bewirkt, dass die Kamera an der Position zentriert wird.
     * </p>
     *
     * @param width  Die neue Breite des Fensters in Pixeln
     * @param height Die neue Höhe des Fensters in Pixeln
     */
    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
        this.context.viewport.update(width, height, true);
    }

    /**
     * Wird aufgerufen, wenn das Spiel pausiert wird (z.B. wenn die App in den
     * Hintergrund wechselt).
     *
     * <p>
     * In dieser Implementierung passiert nichts, könnte aber genutzt werden,
     * um Ressourcen freizugeben oder den Spielzustand zu speichern.
     * </p>
     */
    @Override
    public void pause() {
        LoggerUtil.log(LogCategory.DEBUG,this,"GameScreen.pause() wurde aufgerufen!");
    }

    /**
     * Wird aufgerufen, wenn das Spiel fortgesetzt wird (z.B. nach einer Pause).
     *
     * <p>
     * In dieser Implementierung passiert nichts, könnte aber genutzt werden,
     * um Ressourcen neu zu laden oder den Spielzustand wiederherzustellen.
     * </p>
     */
    @Override
    public void resume() {
        LoggerUtil.log(LogCategory.DEBUG,this,"GameScreen.resume() wurde aufgerufen!");


        // Stelle sicher, dass das PlayerMovementSystem ein KeyInputListener ist
        PlayerMovementSystem playerMovementSystem = this.context.getEngine().getSystem(PlayerMovementSystem.class);
        this.context.getInputManager()
                .addKeyInputListener(playerMovementSystem);
        // wir müssen die Kamera hier nicht aktualisieren, da sie im render() aufgerufen wird
    }

    /**
     * Wird aufgerufen, wenn dieser Screen nicht mehr der aktive Screen ist.
     *
     * <p>
     * In dieser Implementierung passiert nichts, könnte aber genutzt werden,
     * um temporäre Ressourcen freizugeben oder den Zustand zu speichern.
     * </p>
     */
    @Override
    public void hide() {
        // entfernt den KeyInputListener aus dem InputManager, wenn er verstekt wird
        this.context.getInputManager().removeKeyInputListener(this);

        // Stelle sicher, dass das der KeyInputListener vom PlayerMovementSystem nicht
        // mehr aktiv ist
        this.context.getInputManager().removeKeyInputListener(this.context.getEngine().getSystem(PlayerMovementSystem.class));
        // Debug-Renderer entfernen
        if (DebugConfig.DEBUG_ENABLED && debugRenderer != null) {
            context.getDebugSystem().removeRenderer(debugRenderer);
        }
    }

    @Override
    public void keyDown(InputManager manager, GameKey key) {
        if (key == GameKey.PAUSE) {
            this.context.getScreenManager().showScreen(ScreenType.PAUSE);
        }
    }

    @Override
    public void keyUp(InputManager manager, GameKey key) {
    }

    /**
     * Wird aufgerufen, wenn dieser Screen zerstört wird.
     *
     * <p>
     * Gibt alle Ressourcen frei, die explizit für diesen Screen geladen wurden.
     * </p>
     */
    @Override
    public void dispose() {

    }

}
