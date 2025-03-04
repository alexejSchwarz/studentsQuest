package de.haw.sea2.map;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

import de.haw.sea2.StudentsQuest;

/**
 * Verwaltet das Laden, Aktivieren und Verwalten von Spielkarten.
 */
public class MapManager implements Disposable {

    private final StudentsQuest context;
    private final ObjectMap<String, GameMap> mapCache;

    public MapManager(StudentsQuest context) {
        this.context = context;
        this.mapCache = new ObjectMap<>();
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
            context.getTiledMapRenderer().setMap(cachedMap.getTiledMap());
            context.setMap(cachedMap);
        }

        // Prüfe, ob die Karte im AssetManager geladen ist
        if (!context.getAssetManager().isLoaded(mapPath)) {
            throw new IllegalStateException("Map " + mapPath + " wurde nicht geladen!");
        }

        // Hole die geladene Karte aus dem AssetManager
        TiledMap tiledMap = context.getAssetManager().get(mapPath, TiledMap.class);

        // Konfiguriere die Karte für die Verwendung
        context.getTiledMapRenderer().setMap(tiledMap);

        // Erstelle ein GameMap-Objekt und cache es
        GameMap gameMap = new GameMap(tiledMap);
        mapCache.put(mapPath, gameMap);

        // Setze die aktuelle Karte im Kontext
        context.setMap(gameMap);

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

    @Override
    public void dispose() {
        // Alle gecachten Maps freigeben
        for (GameMap map : mapCache.values()) {
            map.dispose();
        }
        mapCache.clear();
    }
}