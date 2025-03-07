package de.haw.sea2;

import java.util.List;

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
import de.haw.sea2.map.MapManager;
import de.haw.sea2.screen.MainMenuScreen;
import de.haw.sea2.screen.ScreenManager;
import de.haw.sea2.screen.ScreenType;

/**
 * Die Hauptklasse des Spiels, die alle grundlegenden Komponenten initialisiert
 * und verwaltet.
 *
 * <p>
 * Diese Klasse erbt von libGDX's Game-Klasse und implementiert das
 * KeyInputListener-Interface.
 * Sie fungiert als zentrale Kontrollinstanz und Ressourcenmanager für das
 * gesamte Spiel.
 * </p>
 *
 * <p>
 * Zu den Hauptaufgaben dieser Klasse gehören:
 * <ul>
 * <li>Initialisierung der Grafik-Engine (SpriteBatch, Camera, Viewport)</li>
 * <li>Einrichtung der Physik-Engine (Box2D World)</li>
 * <li>Verwaltung des Entity-Component-Systems (ECS)</li>
 * <li>Verwaltung des Asset-Ladens (Texturen, Karten, usw.)</li>
 * <li>Koordination der Ein- und Ausgabe (Input Management)</li>
 * <li>Verwaltung des aktuell angezeigten Screens (Hauptmenü, Spielbildschirm,
 * usw.)</li>
 * </ul>
 * </p>
 */
// TODO wirklich implements?
public class StudentsQuest extends Game {

    /**
     * Der Skalierungsfaktor für die Umrechnung von Pixeln zu Physik-Welteinheiten.
     *
     * <p>
     * Ein Pixel in der Spielwelt entspricht 1/64 Einheiten in der Physik-Welt.
     * Dies ist wichtig für die korrekte Skalierung von visuellen Elementen und
     * Kollisionsbereichen.
     * Beispiel: Ein 64x64 Pixel großes Bild wird als 1x1 Einheit in der Physik-Welt
     * dargestellt.
     * </p>
     */
    public static final float UNIT_SCALE = 1 / 64f; // TODO ich denke dass muss dann 1/32f sein, wenn wir 32x32 Grafiken
                                                    // haben

    /**
     * Wiederverwendbare Definitionen für physikalische Körper und ihre
     * Eigenschaften.
     *
     * <p>
     * Diese statischen Objekte dienen als Vorlagen für die Erstellung von
     * physikalischen
     * Körpern und Fixtures in der Box2D-Physik-Engine. Sie werden wiederverwendet,
     * um Speicher
     * zu sparen und die Erstellung von Physik-Objekten zu vereinfachen.
     * </p>
     *
     * <p>
     * BODY_DEF definiert grundlegende Eigenschaften eines Körpers wie Position und
     * Typ (statisch/dynamisch).
     * </p>
     */
    public static final BodyDef BODY_DEF = new BodyDef();

    /**
     * Definition für Fixtures, die einem Körper zugeordnet werden und dessen
     * physikalische
     * Eigenschaften bestimmen.
     *
     * <p>
     * FIXTURE_DEF definiert Eigenschaften wie Reibung, Elastizität und
     * Kollisionsfilter.
     * </p>
     */
    public static final FixtureDef FIXTURE_DEF = new FixtureDef();

    /**
     * Manager für das Laden und Verwalten von Spiel-Assets wie Texturen, Sounds und
     * Karten.
     *
     * <p>
     * Der AssetManager ermöglicht das asynchrone Laden von Ressourcen und hilft,
     * Speicher
     * effizient zu verwalten, indem er Ressourcen wiederverwendet und bei Bedarf
     * freigibt.
     * </p>
     */
    // should not be static
    private AssetManager assetManager;

    /**
     * Verwaltet die Spieleingaben und konvertiert Tastaturereignisse in
     * spielspezifische Aktionen.
     *
     * <p>
     * Der InputManager nimmt Tastaturereignisse entgegen und informiert
     * registrierte Listener
     * über Spielaktionen wie "nach oben bewegen" oder "zurück".
     * </p>
     */
    private InputManager inputManager;

    /**
     * Spezifischer Renderer für Tiled-Karten, der die Spielwelt darstellt.
     *
     * <p>
     * Dieser Renderer zeichnet die Kartenkacheln und -objekte mit der richtigen
     * Skalierung
     * und Position auf dem Bildschirm.
     * </p>
     */
    private OrthogonalTiledMapRenderer tiledMapRenderer;

    /**
     * Hilfsklasse zum Visualisieren der Box2D-Physik für Debugging-Zwecke.
     *
     * <p>
     * Dieser Renderer zeichnet die Umrisse der Kollisionskörper, was bei der
     * Entwicklung
     * hilft, Probleme mit der Physik zu erkennen.
     * </p>
     */
    private Box2DDebugRenderer debugRenderer;

    /**
     * Die physikalische Welt, in der alle Kollisionen und Bewegungen simuliert
     * werden.
     *
     * <p>
     * Die Box2D World enthält alle physikalischen Körper (wie Spieler und Wände)
     * und
     * simuliert deren Interaktionen nach den Gesetzen der Physik.
     * </p>
     */
    // TODO pass instance to all screens
    private World world;

    /**
     * Überwacht Kollisionsereignisse in der physikalischen Welt und reagiert
     * darauf.
     *
     * <p>
     * Dieser Listener wird bei jedem Kontakt zwischen physikalischen Objekten
     * benachrichtigt
     * und kann dann entsprechende Aktionen auslösen (z.B. Schaden verursachen,
     * Gegenstände aufsammeln).
     * </p>
     *
     * <p>
     * Die Klasse implementiert das ContactListener-Interface von Box2D und hat vier
     * Hauptmethoden:
     * <ul>
     * <li>beginContact: Wird aufgerufen, wenn zwei Objekte anfangen zu
     * kollidieren</li>
     * <li>endContact: Wird aufgerufen, wenn zwei Objekte aufhören zu
     * kollidieren</li>
     * <li>preSolve: Wird vor der Kollisionslösung aufgerufen und kann diese
     * modifizieren</li>
     * <li>postSolve: Wird nach der Kollisionslösung aufgerufen mit den
     * resultierenden Kräften</li>
     * </ul>
     * </p>
     */
    private WorldContactListener worldContactListener;

    /**
     * Bestimmt, wie die Spielwelt auf dem Bildschirm angezeigt wird.
     *
     * <p>
     * Der FitViewport passt die Spielwelt so an den Bildschirm an, dass das
     * Seitenverhältnis erhalten bleibt und schwarze Balken hinzugefügt werden, wenn
     * nötig.
     * Dies stellt sicher, dass das Spiel auf verschiedenen Bildschirmgrößen gleich
     * aussieht.
     * </p>
     */
    // TODO use in resize in Screen impl
    // evt ExtendViewPort?
    public FitViewport viewport;

    /**
     * Die virtuelle Kamera, die den sichtbaren Bereich der Spielwelt bestimmt.
     *
     * <p>
     * Diese orthografische (2D) Kamera kann bewegt, rotiert und gezoomt werden, um
     * verschiedene Teile der Spielwelt zu zeigen.
     * </p>
     */
    private OrthographicCamera gameCamera;

    /**
     * Container für UI-Elemente und Schauspieler in der Spielwelt.
     *
     * <p>
     * Die Stage verwaltet eine Hierarchie von Akteuren (wie UI-Buttons oder
     * animierte Sprites) und leitet Eingabeereignisse an sie weiter.
     * </p>
     */
    private Stage stage;

    /**
     * Repräsentation der Spielkarte mit ihren Kollisionsbereichen.
     *
     * <p>
     * Die GameMap enthält die aus einer Tiled-Karte geladenen Ebenen und
     * Kollisionsobjekte, die für die Navigation und Physik verwendet werden.
     * </p>
     */
    private GameMap map;

    /**
     * Hauptwerkzeug zum Zeichnen von 2D-Grafiken auf dem Bildschirm.
     *
     * <p>
     * Der SpriteBatch bündelt viele Zeichenoperationen in einem Stapel, was
     * die Grafikleistung drastisch verbessert, da weniger GPU-Befehle gesendet
     * werden müssen.
     * </p>
     */
    private SpriteBatch spriteBatch;

    /**
     * Die Entity-Component-System-Engine, die alle Spielentitäten und ihre Systeme
     * verwaltet.
     *
     * <p>
     * Diese Engine ist das Herzstück der Spiellogik und verarbeitet die Bewegung
     * von Entitäten,
     * Kollisionen, KI und andere Spielmechaniken über verschiedene Systeme.
     * </p>
     */
    private ECSEngine engine;

    /**
     * Verwaltet alle Screens des Spiels und den Übergang zwischen ihnen.
     */
    private ScreenManager screenManager;

    private MapManager mapManager;

    /**
     * Wird einmalig beim Start des Spiels aufgerufen und initialisiert alle
     * notwendigen Komponenten.
     *
     * <p>
     * Diese Methode richtet die grundlegende Spielinfrastruktur ein:
     * <ul>
     * <li>Erstellt den SpriteBatch zum Zeichnen von Grafiken</li>
     * <li>Initialisiert den AssetManager zum Laden von Ressourcen</li>
     * <li>Richtet den TiledMapRenderer für die Spielkarten ein</li>
     * <li>Erstellt die Box2D-Physikwelt und den ContactListener</li>
     * <li>Konfiguriert Kamera und Viewport für die Bildschirmdarstellung</li>
     * <li>Initialisiert den InputManager für Benutzereingaben</li>
     * <li>Erstellt die ECS-Engine für die Spiellogik</li>
     * <li>Setzt den MainMenuScreen als ersten aktiven Bildschirm</li>
     * </ul>
     * </p>
     */
    @Override
    public void create() {
        // Erstellt den SpriteBatch zum Zeichnen von Grafiken
        this.spriteBatch = new SpriteBatch();

        // Initialisiert den AssetManager zum Laden von Ressourcen
        this.assetManager = new AssetManager();

        // Richtet den TiledMapRenderer für die Spielkarten ein
        this.tiledMapRenderer = new OrthogonalTiledMapRenderer(null, UNIT_SCALE, this.spriteBatch);

        // Erstellt die Box2D-Physikwelt mit Schwerkraft (0,0) und aktiviertem Sleep für
        // Objekte
        this.world = new World(new Vector2(0f, 0f), true);
        // Erstellt und registriert den ContactListener für Kollisionsereignisse
        this.worldContactListener = new WorldContactListener();
        this.world.setContactListener(worldContactListener);

        // Erstellt den Debug-Renderer für die Box2D-Physik
        this.debugRenderer = new Box2DDebugRenderer();

        // Konfiguriert Kamera und Viewport für die Bildschirmdarstellung
        this.gameCamera = new OrthographicCamera();
        // 16f 9f aspect ratio plus window size in logical units
        this.viewport = new FitViewport(16f, 9f, gameCamera);
        this.stage = new Stage(this.viewport);

        // Initialisiert den InputManager für Benutzereingaben
        this.inputManager = new InputManager();
        // Erstellt die ECS-Engine für die Spiellogik
        this.engine = new ECSEngine(this);

        // Konfiguriert den InputProcessor mit einem Multiplexer für mehrere Quellen
        Gdx.input.setInputProcessor(new InputMultiplexer(this.inputManager, this.stage));

        // Initialisiert den ScreenManager
        this.screenManager = new ScreenManager(this);

        // Setzt den MainMenuScreen als ersten aktiven Bildschirm
        this.screenManager.showScreen(ScreenType.MAIN_MENU);

        this.mapManager = new MapManager(this);
    }

    /**
     * Wird in jedem Frame aufgerufen, um das Spiel zu aktualisieren und zu rendern.
     *
     * <p>
     * Diese Methode delegiert die Render-Aufgabe an den aktuell aktiven Screen
     * (wie MainMenuScreen oder GameScreen), der dann seine eigene render-Methode
     * ausführt.
     * </p>
     */
    @Override
    public void render() {
        // Ruft die render-Methode der Elternklasse auf, was wiederum die
        // render-Methode des aktuell aktiven Screens aufruft
        super.render();
    }

    /**
     * Wird aufgerufen, wenn das Spiel beendet wird, um alle Ressourcen freizugeben.
     *
     * <p>
     * Diese Methode sorgt dafür, dass alle von diesem Spiel verwendeten Ressourcen
     * ordnungsgemäß freigegeben werden, um Speicherlecks zu vermeiden.
     * </p>
     */
    @Override
    public void dispose() {
        // TODO check all fields in class if there is more to dispose
        spriteBatch.dispose();
        world.dispose();
        debugRenderer.dispose();
        this.assetManager.dispose();
        this.tiledMapRenderer.dispose();
        this.tiledMapRenderer.dispose();
        mapManager.dispose();
    }

    /**
     * Wird aufgerufen, wenn die Fenstergröße geändert wird, um den Viewport
     * anzupassen.
     *
     * <p>
     * Diese Methode stellt sicher, dass das Spiel bei Änderungen der Fenstergröße
     * korrekt dargestellt wird, indem der Viewport entsprechend aktualisiert wird.
     * </p>
     *
     * @param width  Die neue Breite des Fensters in Pixeln
     * @param height Die neue Höhe des Fensters in Pixeln
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    /**
     * Setzt die Standardwerte für BodyDef und FixtureDef zurück.
     *
     * <p>
     * Diese statische Methode wird aufgerufen, bevor neue physikalische Körper
     * erstellt werden,
     * um sicherzustellen, dass alle Eigenschaften auf ihre Standardwerte
     * zurückgesetzt sind
     * und keine unerwünschten Werte von vorherigen Körpern übernommen werden.
     * </p>
     */
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

    /**
     * Gibt die ECS-Engine zurück, die für die Verwaltung von Spielentitäten und
     * -systemen zuständig ist.
     *
     * @return Die ECSEngine-Instanz dieses Spiels
     */
    public ECSEngine getEngine() {
        return engine;
    }

    /**
     * Gibt die Box2D-Physikwelt zurück, in der alle Kollisionen und Bewegungen
     * simuliert werden.
     *
     * @return Die World-Instanz dieses Spiels
     */
    public World getWorld() {
        return this.world;
    }

    /**
     * Gibt den AssetManager zurück, der für das Laden und Verwalten von
     * Spiel-Assets zuständig ist.
     *
     * @return Die AssetManager-Instanz dieses Spiels
     */
    public AssetManager getAssetManager() {
        return this.assetManager;
    }

    /**
     * Gibt den Debug-Renderer für die Box2D-Physik zurück.
     *
     * @return Die Box2DDebugRenderer-Instanz dieses Spiels
     */
    public Box2DDebugRenderer getDebugRenderer() {
        return this.debugRenderer;
    }

    /**
     * Gibt die Spielkamera zurück, die den sichtbaren Bereich der Spielwelt
     * bestimmt.
     *
     * @return Die OrthographicCamera-Instanz dieses Spiels
     */
    public OrthographicCamera getGameCamera() {
        return this.gameCamera;
    }

    /**
     * Gibt den InputManager zurück, der für die Verarbeitung von Benutzereingaben
     * zuständig ist.
     *
     * @return Die InputManager-Instanz dieses Spiels
     */
    public InputManager getInputManager() {
        return this.inputManager;
    }

    /**
     * Gibt den SpriteBatch zurück, der zum Zeichnen von 2D-Grafiken verwendet wird.
     *
     * @return Die SpriteBatch-Instanz dieses Spiels
     */
    public SpriteBatch getSpriteBatch() {
        return this.spriteBatch;
    }

    /**
     * Gibt den Renderer für Tiled-Karten zurück, der die Spielwelt darstellt.
     *
     * @return Die OrthogonalTiledMapRenderer-Instanz dieses Spiels
     */
    public OrthogonalTiledMapRenderer getTiledMapRenderer() {
        return tiledMapRenderer;
    }

    /**
     * Gibt die aktuelle Spielkarte zurück, die die Spielwelt repräsentiert.
     *
     * @return Die GameMap-Instanz dieses Spiels
     */
    public GameMap getMap() {
        return this.map;
    }

    /**
     * Setzt eine neue Spielkarte als die aktuelle Karte.
     *
     * @param map Die neue GameMap-Instanz, die als aktuelle Karte gesetzt werden
     *            soll
     */
    public void setMap(GameMap map) {
        this.map = map;
    }

    /**
     * Gibt den ScreenManager zurück, der für die Verwaltung der verschiedenen
     * Spielbildschirme zuständig ist.
     *
     * @return Die ScreenManager-Instanz dieses Spiels
     */
    public ScreenManager getScreenManager() {
        return this.screenManager;
    }

    public MapManager getMapManager() {
        return mapManager;
    }

    /*
     * public static final short BIT_CIRCLE = 1 << 0;
     * public static final short BIT_BOX = 1 << 1;
     * public static final short BIT_GROUND = 1 << 2;
     */

    /**
     * physics example for circle falling onto a plattform
     */
    /*
     * private void exampleBodyFixtureStuff() {
     *
     * // you can reuse body and fixture for other objects. createFixture should
     * overrite previous configs
     *
     * //--circle
     * //this is center of body
     * this.BODY_DEF.position.set(5f, 5f);
     * this.BODY_DEF.gravityScale = 1;
     * this.BODY_DEF.type = BodyDef.BodyType.DynamicBody;
     * final Body body = this.world.createBody(BODY_DEF);
     *
     * this.FIXTURE_DEF.isSensor = false;
     *
     * //bouncyness 1 is super bouncy
     * this.FIXTURE_DEF.restitution = 0.5f;
     *
     * //stickyness
     * this.FIXTURE_DEF.friction = 0.2f;
     *
     *
     * // defines to which tcategory this fixture belongs
     * this.FIXTURE_DEF.filter.categoryBits = BIT_CIRCLE;
     *
     * // defines with which fixture categories this one collides with
     * this.FIXTURE_DEF.filter.maskBits = BIT_GROUND | BIT_BOX;
     *
     * CircleShape cShape = new CircleShape();
     * cShape.setRadius(0.5f);
     *
     * this.FIXTURE_DEF.shape = cShape;
     * body.createFixture(this.FIXTURE_DEF);
     * cShape.dispose();
     * }
     */
}
