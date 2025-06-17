package de.haw.sea2.screen;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ObjectSet;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.audio.Audio;
import de.haw.sea2.debug.DebugConfig;
import de.haw.sea2.debug.LogCategory;
import de.haw.sea2.debug.LoggerUtil;
import de.haw.sea2.debug.render.EnemyMovementDebugRenderer;
import de.haw.sea2.debug.render.GameScreenDebugRenderer;
import de.haw.sea2.input.GameKey;
import de.haw.sea2.input.InputManager;
import de.haw.sea2.input.ResettableInputListener;
import de.haw.sea2.input.ScreenKeyInputListener;
import de.haw.sea2.lifeCicle.GameState;
import de.haw.sea2.logic.EntityUtils;
import de.haw.sea2.logic.ecs.ECSEngine;
import de.haw.sea2.logic.ecs.components.Box2DComponent;
import de.haw.sea2.logic.ecs.components.HearthComponent;
import de.haw.sea2.logic.ecs.components.PlayerComponent;
import de.haw.sea2.logic.ecs.systems.PlayerAttackSystem;
import de.haw.sea2.logic.ecs.systems.PlayerMovementSystem;
import de.haw.sea2.logic.gameLevel.SpawnLogic;
import de.haw.sea2.map.GameMap;
import de.haw.sea2.paths.MapPaths;
import de.haw.sea2.view.GameRenderer;
import de.haw.sea2.view.ui.GameUI;

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
public class
GameScreen implements Screen, ScreenKeyInputListener {

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

    private final SpawnLogic spawnLogic;

    private GameScreenDebugRenderer debugRenderer;

    private EnemyMovementDebugRenderer enemyMovementDebugRenderer;

    private final GameUI gameUI;

    private ImmutableArray<Entity> players;

    private PlayerComponent playerComponent;

    private HearthComponent playerHearthComp;

    // Verwende ObjectSet anstelle von normalen Arrays
    private final ObjectSet<ResettableInputListener> resettableInputListener;

    public GameScreen(StudentsQuest context) {
        this.context = context;
        this.engine = this.context.getEngine();
        this.gameRenderer = this.context.getGameRenderer();
        this.world = this.context.getWorld();
        this.spawnLogic = new SpawnLogic(context);
        this.gameUI = new GameUI(context);

        // Initialisiere das ObjectSet für die Listener
        this.resettableInputListener = new ObjectSet<>();
        this.context.stateMachine.changeState(GameState.RUNNING);

        initialize();
    }

    /**
     * Wird aufgerufen, wenn dieser Screen der aktive Screen wird.
     */
    @Override
    public void show() {

        // Registriere diesen Screen als KeyInputListener
        this.context.getInputManager().addKeyInputListener(this);
        LoggerUtil.log(LogCategory.DEBUG, this, "Registering GameScreen as KeyInputListener");

        // Registriere alle Game-System KeyInputListener
        for (ScreenKeyInputListener listener : resettableInputListener) {
            this.context.getInputManager().addKeyInputListener(listener);
            LoggerUtil.log(LogCategory.DEBUG, this, "Registering game system listener: " + listener.getClass().getSimpleName());
        }

        if (DebugConfig.DEBUG_ENABLED) {
            this.context.getDebugSystem().addRenderer(this.enemyMovementDebugRenderer);
            this.context.getInputManager().addKeyInputListener(this.enemyMovementDebugRenderer);
        }

        players = context.getEngine().getEntitiesFor(Family.all(PlayerComponent.class).get());
        Entity player = EntityUtils.checkAndGetPlayer(players);

        playerComponent = player.getComponent(PlayerComponent.class);
        playerHearthComp = player.getComponent(HearthComponent.class);

        context.getAudioManager().playAudio(Audio.LEVEL_1_MUSIC);
    }

    private void initialize() {
        // Erstellung von Spieler soll hier passieren und nicht im show(), da sonst
        // Duplikate entstehen

        // Map aktivieren (nicht mehr laden!)
        this.context.getMapManager().activateMap(MapPaths.MAINMAP.getPath());

        GameMap map = this.context.getMapManager().getCurrentMap();
        // Übergebe sowohl die Spawnpunkte als auch die Kollisionswände an SpawnLogic
        this.spawnLogic.prepareLevelStart(map.getEntitySpawnPoints());

        // Füge alle nicht-Screen Listener hinzu
        this.resettableInputListener.add(this.context.getEngine().getSystem(PlayerMovementSystem.class));
        this.resettableInputListener.add(this.context.getEngine().getSystem(PlayerAttackSystem.class));

        // Debug-Renderer erstellen
        if (DebugConfig.DEBUG_ENABLED) {
            this.debugRenderer = new GameScreenDebugRenderer(this.context);
            this.context.getDebugSystem().addRenderer(this.debugRenderer);

            // EnemyMovementDebugRenderer hinzufügen
            this.enemyMovementDebugRenderer = new EnemyMovementDebugRenderer(this.context);
            this.context.getDebugSystem().addRenderer(this.enemyMovementDebugRenderer);
            this.resettableInputListener.add(this.enemyMovementDebugRenderer);
        }

    }

    /**
     * Wird in jedem Frame aufgerufen, um die Spielwelt zu aktualisieren und
     * darzustellen.
     */
    @Override
    public void render(float delta) {

        final float deltaTime = Math.min(0.25f, Gdx.graphics.getDeltaTime());
        this.engine.update(deltaTime);

        // naehcstes Tick fuer SpawnLogik
        this.spawnLogic.update(deltaTime);

        // Fixierung fuer die Physics berechnung
        this.accumulator += deltaTime;

        if (playerHearthComp.currentHearths <= 0) {
            //TODO ersetze durch gameOverScreen
            this.context.stateMachine.changeState(GameState.OVER);
            this.context.getScreenManager().showScreen(ScreenType.SUCCESS);
        }

        if (playerComponent.collectedCoins == playerComponent.neededCoins) {
            this.context.stateMachine.changeState(GameState.OVER);
            context.getScreenManager().showScreen(ScreenType.SUCCESS);
        }

        while (this.accumulator >= StudentsQuest.PHYSICS_TIME_STEP) {

            // remembers the previous position for later interpolation
            for (Entity entity : this.engine.getEntitiesFor(Family.all(Box2DComponent.class).get())) {
                Box2DComponent b2dComp = ECSEngine.BOX2D_COMP_MAPPER.get(entity);
                b2dComp.previousX = b2dComp.body.getPosition().x;
                b2dComp.previousY = b2dComp.body.getPosition().y;
            }

            this.world.step(StudentsQuest.PHYSICS_TIME_STEP, 6, 2);
            this.accumulator -= StudentsQuest.PHYSICS_TIME_STEP;
        }

        // interpolation rendering. reduces stuttering between frames
        this.gameRenderer.render(this.accumulator / StudentsQuest.PHYSICS_TIME_STEP); // alpha value
        gameUI.render();
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
        LoggerUtil.log(LogCategory.DEBUG, this, "GameScreen.pause() wurde aufgerufen!");
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
        LoggerUtil.log(LogCategory.DEBUG, this, "GameScreen.resume() wurde aufgerufen!");
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
        // Entferne diesen Screen als Listener
        this.context.getInputManager().removeKeyInputListener(this);

        // Stoppe die Musik
        context.getAudioManager().stopCurrentMusic();

        // Entferne alle Game-System KeyInputListener und setze sie zurück
        for (ResettableInputListener listener : resettableInputListener) {
            this.context.getInputManager().removeKeyInputListener(listener);
            listener.reset();
        }

        // Debug-Renderer entfernen
        if (DebugConfig.DEBUG_ENABLED) {
            if (debugRenderer != null) {
                context.getDebugSystem().removeRenderer(debugRenderer);
            }
            if (enemyMovementDebugRenderer != null) {
                context.getDebugSystem().removeRenderer(enemyMovementDebugRenderer);
            }
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
