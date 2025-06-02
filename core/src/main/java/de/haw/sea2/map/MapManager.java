package de.haw.sea2.map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.ecs.Bits;
import de.haw.sea2.ecs.builders.EntityCreator;
import de.haw.sea2.ecs.components.RemoveComponent;

/**
 * Verwaltet das Laden, Aktivieren und Verwalten von Spielkarten.
 */
public class MapManager implements Disposable {

    private final StudentsQuest context;
    private final ObjectMap<String, GameMap> mapCache;
    private final Array<Body> bodies;
    private final Array<MapChangeListener> mapChangeListeners;
    private final EntityCreator creator;

    private GameMap currentMap;

    public MapManager(StudentsQuest context) {
        this.context = context;
        this.mapCache = new ObjectMap<>();
        this.bodies = new Array<>();
        this.creator = context.getEntityCreator();

        // for now like this. add/remove listener methods later, if needed
        this.mapChangeListeners = new Array<>();
        this.mapChangeListeners.add(this.context.getGameRenderer());
        this.mapChangeListeners.add(this.context.getPathCalcManager());
    }

    /**
     * Bereitet das Laden einer Karte vor.
     * Diese Methode wird vom LoadingScreen aufgerufen, um den Ladevorgang zu
     * starten.
     *
     * @param mapPath Pfad zur Kartendatei
     */
    public void loadMap(String mapPath) {
        // Registriert den TmxMapLoader, falls noch nicht geschehen
        if (!context.getAssetManager().isLoaded(mapPath)) {
            context.getAssetManager().setLoader(TiledMap.class,
                new TmxMapLoader(context.getAssetManager().getFileHandleResolver()));
            context.getAssetManager().load(mapPath, TiledMap.class);
        }
    }

    /**
     * Aktiviert eine bereits geladene Karte und bereitet sie für die Verwendung
     * vor.
     * Diese Methode wird vom GameScreen aufgerufen, nachdem die Assets geladen
     * wurden.
     *
     * @param mapPath Pfad zur Kartendatei
     * @return Die aktivierte GameMap-Instanz
     */
    public void activateMap(String mapPath) {

        // Prüfe, ob die Karte bereits im Cache ist
        if (mapCache.containsKey(mapPath)) {
            GameMap cachedMap = mapCache.get(mapPath);

            if (currentMap != null && cachedMap == currentMap) {
                return;
            }

            context.getGameRenderer().getMapRenderer().setMap(cachedMap.getTiledMap());

            // alte Walls zerstoeren und neue erzeugen
            destroyCollisionWalls();
            createCollisionWalls(cachedMap);
            this.currentMap = cachedMap;
            notifyMapChange();
            return;
        }

        // Prüfe, ob die Karte im AssetManager geladen ist
        if (!context.getAssetManager().isLoaded(mapPath)) {
            throw new IllegalStateException("Map " + mapPath + " wurde nicht geladen!");
        }

        // Hole die geladene Karte aus dem AssetManager
        TiledMap tiledMap = context.getAssetManager().get(mapPath, TiledMap.class);

        // Erstelle ein GameMap-Objekt und cache es
        if(tiledMap == null) {
            throw new IllegalStateException("Map " + mapPath + " ist null!");
        }
        GameMap gameMap = new GameMap(tiledMap);
        mapCache.put(mapPath, gameMap);

        destroyCollisionWalls();
        createCollisionWalls(gameMap);

        this.currentMap = gameMap;

        notifyMapChange();
    }


    private void createCollisionWalls(GameMap gameMap) {
        for(Rectangle rectangle : gameMap.getCollisionAreas()) {
            this.creator.createWall(rectangle);
        }
    }

    /**
     * Soll bei jeder Mapchange aufgerufen werden
     */
    private void destroyCollisionWalls() {
        this.context.getWorld().getBodies(this.bodies);
        for (Body body : this.bodies) {
            for (Fixture fixture : body.getFixtureList()) {
                if (fixture.getFilterData().categoryBits == Bits.BIT_WALL.value) {
                    Entity wallEntity = (Entity) body.getUserData();
                    wallEntity.add(new RemoveComponent());
                }
            }
        }
    }

    /**
     * Gibt eine Karte frei, wenn sie nicht mehr benötigt wird.
     */
    public void disposeMap(String mapPath) {
        if (mapCache.containsKey(mapPath)) {
            mapCache.get(mapPath).dispose();
            // Hier könnten wir die Map aus dem AssetManager entfernen,
            // wenn wir sie nicht mehr benötigen
            mapCache.remove(mapPath);
        }
    }

    public GameMap getCurrentMap() {
        return this.currentMap;
    }

    @Override
    public void dispose() {
        // Alle gecachten Maps freigeben
        for (GameMap map : mapCache.values()) {
            map.dispose();
        }
        mapCache.clear();
    }

    private void notifyMapChange() {
        for (MapChangeListener listener : this.mapChangeListeners) {
            listener.onMapChange(this.currentMap);
        }
    }
}
