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
public class GameScreen implements Screen {

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

    // TODO relocate into ecs later
    /**
     * Die Textur für den Spielercharakter.
     * 
     * <p>
     * Diese Textur wird temporär hier verwaltet und direkt gerendert.
     * Später soll dies in das Entity-Component-System ausgelagert werden.
     * </p>
     */
    private Texture playerTexture;

    /**
     * Erstellt einen neuen GameScreen mit dem angegebenen Spiel-Kontext.
     *
     * @param context Der StudentsQuest-Kontext, der Zugriff auf zentrale
     *                Ressourcen und Systeme bietet
     */
    public GameScreen(StudentsQuest context) {
        this.context = context;
    }

    /**
     * Wird aufgerufen, wenn dieser Screen der aktive Screen wird.
     * 
     * <p>
     * Diese Methode initialisiert die gesamte Spielwelt:
     * <ul>
     * <li>Die Box2D-Physik-Engine wird initialisiert</li>
     * <li>Die Spielkarte (TiledMap) wird geladen</li>
     * <li>Die Spielertextur wird geladen</li>
     * <li>Die Kollisionsbereiche werden aus der Karte extrahiert</li>
     * <li>Der Spielercharakter wird erstellt</li>
     * </ul>
     * </p>
     */
    @Override
    public void show() {

        // physics simulation
        Box2D.init();

        // TODO Auslagern in nen LoadingScreen
        // Lädt die TiledMap-Datei über den AssetManager
        // 1. Registriert den TmxMapLoader, um .tmx-Dateien laden zu können
        this.context.getAssetManager().setLoader(TiledMap.class,
                new TmxMapLoader(this.context.getAssetManager().getFileHandleResolver()));
        // 2. Fordert das Laden der Kartendatei "mapMitObj.tmx" an
        this.context.getAssetManager().load("mapMitObj.tmx", TiledMap.class);
        // 3. Wartet, bis der Ladevorgang abgeschlossen ist (blockiert den Thread)
        this.context.getAssetManager().finishLoading();

        // TODO only temporary remove later rendering of Entities and maybe even asset
        // loading of entities in ecs
        // Lädt die Spielertextur auf ähnliche Weise wie die Karte
        this.context.getAssetManager().setLoader(Texture.class,
                new TextureLoader(this.context.getAssetManager().getFileHandleResolver()));
        this.context.getAssetManager().load("tmpGuy.png", Texture.class);
        this.context.getAssetManager().finishLoading();

        // Holt die geladene Textur aus dem AssetManager
        this.playerTexture = this.context.getAssetManager().get("tmpGuy.png", Texture.class);
        // this.playerSprite = new Sprite(this.playerTexture);

        // TODO auslagern in Loadingscreen
        // Holt die geladene Karte aus dem AssetManager
        TiledMap tiledMap = this.context.getAssetManager().get("mapMitObj.tmx", TiledMap.class);
        // Setzt die Karte im TiledMapRenderer, damit sie gerendert werden kann
        this.context.getTiledMapRenderer().setMap(tiledMap);

        // TODO handle in map manager? dispose when no longer needed or chache it
        // someHow
        // Erstellt ein GameMap-Objekt aus der geladenen TiledMap und setzt es im
        // Kontext
        // Die GameMap-Klasse extrahiert Kollisionsbereiche aus der Karte
        this.context.setMap(new GameMap(tiledMap));

        // TODO entity and components creation in the coresponding GameScreens
        // Erstellt die Kollisionswände in der Box2D-Welt basierend auf den Bereichen
        // aus der Karte
        this.context.getEngine().createCollisionWalls(this.context.getMap().getCollisionAreas());

        // Erstellt den Spielercharakter an Position (3.5, 3.5) mit Größe 1x1 Einheiten
        // Die Position ist relativ zur Box2D-Welt und nicht zu Pixeln auf dem
        // Bildschirm
        this.context.getEngine().createPlayer(new Vector2(3.5f, 3.5f), 1f, 1f);
    }

    /**
     * Wird in jedem Frame aufgerufen, um die Spielwelt zu aktualisieren und
     * darzustellen.
     * 
     * <p>
     * Diese Methode:
     * <ul>
     * <li>Aktualisiert alle Systeme im Entity-Component-System</li>
     * <li>Führt einen Physik-Simulationsschritt aus</li>
     * <li>Rendert die Spielkarte</li>
     * <li>Rendert Debug-Informationen für die Physik</li>
     * <li>Rendert den Spieler (temporär, wird später durch ein Render-System
     * ersetzt)</li>
     * </ul>
     * </p>
     *
     * @param delta Die Zeit in Sekunden seit dem letzten Frame
     */
    @Override
    public void render(float delta) {

        // Aktualisiert alle Systeme in der ECS-Engine
        // float delta = Gdx.graphics.getDeltaTime();
        this.context.getEngine().update(delta);

        // TODO to be removed
        // Löscht den Bildschirm mit einer dunkelblau-grauen Farbe
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        // TODO look this up
        /*
         * stage.getViewport().apply();
         * stage.act();
         * stage.draw();
         */

        // Aktiviert den Viewport, der die Größe der Spielwelt definiert
        // false bedeutet, dass die Kameraposition nicht zentriert wird
        this.context.viewport.apply(false);

        // applies physics
        // Führt einen Schritt der Physik-Simulation aus
        // Parameter: Zeitschritt, Geschwindigkeits-Iterationen, Positions-Iterationen
        this.context.getWorld().step(delta, 6, 2);

        // Setzt die Sicht des TiledMapRenderers auf die aktuelle Kameraposition
        this.context.getTiledMapRenderer().setView(this.context.getGameCamera());
        // Rendert die Karte
        this.context.getTiledMapRenderer().render();
        // Rendert visuelle Debug-Informationen für die Box2D-Physikwelt
        this.context.getDebugRenderer().render(this.context.getWorld(), this.context.viewport.getCamera().combined);

        // TODO this is temporary RenderSystem to be implemented
        // Holt alle Entitäten, die sowohl PlayerComponent als auch Box2DComponent haben
        // (in diesem Fall nur der Spielercharakter)
        ImmutableArray<Entity> entities = this.context.getEngine()
                .getEntitiesFor(Family.all(PlayerComponent.class, Box2DComponent.class).get());
        Entity player = entities.get(0);
        // Holt die Box2DComponent des Spielers, die seine physikalische Position
        // enthält
        Box2DComponent box2DComponent = ECSEngine.BOX2D_COMP_MAPPER.get(player);

        // Beginnt den Batch-Zeichenmodus für Sprites
        this.context.getSpriteBatch().begin();
        // Zeichnet die Spielertextur an der Position des Box2D-Körpers
        // Die Position wird um 0.5 Einheiten in beide Richtungen verschoben,
        // damit die Textur (1x1 Einheiten) zentriert über dem Körper liegt
        this.context.getSpriteBatch().draw(this.playerTexture,
                box2DComponent.body.getPosition().x - 0.5f,
                box2DComponent.body.getPosition().y - 0.5f,
                1f, 1f);
        // Beendet den Batch-Zeichenmodus
        this.context.getSpriteBatch().end();
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

    }

    /**
     * Wird aufgerufen, wenn dieser Screen zerstört wird.
     * 
     * <p>
     * Gibt alle Ressourcen frei, die explizit für diesen Screen geladen wurden.
     * In diesem Fall nur die Spielertextur.
     * </p>
     */
    @Override
    public void dispose() {
        this.playerTexture.dispose();
    }
}