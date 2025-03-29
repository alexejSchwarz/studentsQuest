package de.haw.sea2.map;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import de.haw.sea2.debug.LogCategory;
import de.haw.sea2.debug.LoggerUtil;
import de.haw.sea2.exceptions.IllegalSpawnCoordinatesException;
import de.haw.sea2.map.mapObjectEnums.CustomMapObjectTypes;
import de.haw.sea2.map.mapObjectEnums.MapEntityTypes;
import de.haw.sea2.map.mapObjectEnums.MapLayers;

import java.util.Arrays;
import java.util.Optional;

/**
 * Verarbeitet und verwaltet die Spielkarte mit ihren Kollisionsbereichen.
 * Diese Klasse ist verantwortlich für das Laden einer Tiled-Karte und das
 * Extrahieren
 * der Kollisionsbereiche aus der speziellen "collision"-Ebene der Karte. Die
 * extrahierten
 * Kollisionsbereiche werden spaeter zu Box2dComponenten
 */
//TODO hier haben wir endlich einen geeigneten Kandidaten fuer Junit tests also Testklasse erstellen und zusammen mit Mockito testen!!! Evt auch mit extra TestMaps
public class GameMap implements Disposable {

    /**
     * Die geladene Tiled-Karte, die alle Ebenen und Objekte enthält.
     */
    private final TiledMap tiledMap;

    //fuers erste landen Item und Enemy locations hier
    private final Array<EntitySpawnPoint> entitySpawnPoints;

    private final Array<EntitySpawnPoint> entitySpawnPointsWithProtection;

    private Vector2 playerSpawnPoint;

    /**
     * Liste aller aus der Karte extrahierten Kollisionsbereiche.
     * Diese Liste enthält die umgewandelten und für Box2D aufbereiteten
     * Kollisionsbereiche.
     */
    private final Array<Rectangle> collisionWalls;

    public GameMap(final TiledMap tiledMap) {
        this.tiledMap = tiledMap;
        this.collisionWalls = new Array<>();
        this.entitySpawnPoints = new Array<>();

        //TODO wird in Zukunft gebraucht, um zu verhindern, dass Entitaeten direkt vor dem Spieler spawnen
        // solange das noch nicht implementiert ist, sollte das Array leer bleiben
        this.entitySpawnPointsWithProtection = new Array<>();
        parseCollisionLayer();
        parseEntitySpawnPoints();
    }

    /**
     * liest Spawnpunkte aus dem entitySpawnPoints layer herraus. wird benutzt fuer Items, Spieler und Enemies
     */
    private void parseEntitySpawnPoints() {
        MapLayer spawnLayer = getValidLayer(MapLayers.ENTITY_SPAWN_POINTS.value);
        MapObjects mapObjects = getValidObjects(spawnLayer, MapLayers.ENTITY_SPAWN_POINTS.value);

        if (mapObjects == null) {
            return;
        }

        for (MapObject mapObject : mapObjects) {
            MapProperties properties = mapObject.getProperties();

            // properties des MapObjects laden, wenn nicht vorhanden wird geloggt und ein "standard Wert" gesetzt
            String type = Optional.ofNullable(properties.get("type", String.class))
                .orElseGet(() -> {
                    LoggerUtil.log(LogCategory.DEBUG, this, "property: <type> not set");
                    return "undefined";
                });

            String entityType = Optional.ofNullable(properties.get("entityType", String.class))
                .orElseGet(() -> {
                    LoggerUtil.log(LogCategory.DEBUG, this, "property: <entityType> not set");
                    return "undefined";
                });

            boolean hasSpawnProtection = Optional.ofNullable(properties.get("hasSpawnProtection", Boolean.class))
                .orElseGet(() -> {
                    LoggerUtil.log(LogCategory.DEBUG, this, "property: <hasSpawnProtection> not set, will use <false> as default");
                    return false;
                });

            int id = Optional.ofNullable(properties.get("id", Integer.class))
                .orElseGet(() -> {
                    LoggerUtil.log(LogCategory.DEBUG, this, "property: <id> not set, will use <-1> as default");
                    return -1;
                });

            // ist kein entitySpawnPoint oder hat keine definierte entityType -> gehe zum naechsten mapObject
            if (!type.equals(CustomMapObjectTypes.ENTITY_SPAWN_POINT.value)
                || Arrays.stream(MapEntityTypes.values())
                .map(elem -> elem.value)
                .noneMatch(val -> val.equals(entityType))) {
                LoggerUtil.log(LogCategory.DEBUG, this,
                    "property <type> is: " + type
                        + "\n" + "expected <type> is: " + CustomMapObjectTypes.ENTITY_SPAWN_POINT.value
                        + "\n" + "property <entityType> is: " + entityType
                        + "\n" + "allowed <entityType> are: "
                        + Arrays.stream(MapEntityTypes.values())
                        .map(elem -> elem.value)
                        .reduce("", (a, b) -> a + " " + b)
                );
                continue;
            }

            try {
                Vector2 spawnCoordinates = parseEntitySpawnCoordinates(properties);
                EntitySpawnPoint entitySpawnPoint = new EntitySpawnPoint(type, entityType, hasSpawnProtection, spawnCoordinates, id);
                if (entitySpawnPoint.entityType().equals(MapEntityTypes.PLAYER.value)) {
                    this.playerSpawnPoint = entitySpawnPoint.spawnPoint();
                } else if (entitySpawnPoint.hasSpawnProtection()) {
                    this.entitySpawnPointsWithProtection.add(entitySpawnPoint);
                } else {
                    this.entitySpawnPoints.add(entitySpawnPoint);
                }
            } catch (IllegalSpawnCoordinatesException e) {
                LoggerUtil.log(LogCategory.DEBUG, this, e.getMessage());
            }
        }

    }

    /**
     * Liesst die MapObjects aus dem collision layer und speichert diese als Rectangle Objekte ab
     */
    private void parseCollisionLayer() {
        // Sucht nach einer Ebene mit dem Namen "collision" in der Tiled-Karte
        final MapLayer collisionLayer = this.tiledMap.getLayers().get("collision");

        // Überprüft, ob die Kollisionsebene existiert
        if (collisionLayer == null) {
            LoggerUtil.error(LogCategory.ERROR, this, "There is no collision layer!");
            return; // Methode wird abgebrochen, wenn keine Kollisionsebene vorhanden ist
        }

        final MapObjects mapObjects = collisionLayer.getObjects();

        // Überprüft, ob Objekte in der Kollisionsebene definiert sind
        if (mapObjects == null) {
            LoggerUtil.error(LogCategory.ERROR, this, "There are no collision MapObjects defined!");
            return; // Methode wird abgebrochen, wenn keine Objekte vorhanden sind
        }

        // Iteriert über alle Objekte in der Kollisionsebene
        for (final MapObject mapObject : mapObjects) {
            if (mapObject instanceof RectangleMapObject) {

                float x = mapObject.getProperties().get("x", Float.class);
                float y = mapObject.getProperties().get("y", Float.class);
                float width = mapObject.getProperties().get("width", Float.class);
                float height = mapObject.getProperties().get("height", Float.class);

                // im editor ist der (0,0) Punkt oben Links
                float heightInTiles = this.tiledMap.getProperties().get("height", Integer.class);
                this.collisionWalls.add(ConversionUtils.convertFromRectangleObjectInPixelToLogicWithTransformedY(x, y, width, height, heightInTiles));
            } else {
                LoggerUtil.error(LogCategory.ERROR, this, "MoapObject of Type: " + mapObject + "is not supported!");
            }
        }
    }

    /**
     * Hilfsmethode, um eine Layer aus der TiledMap zu extrahieren und auf
     * Gültigkeit zu prüfen
     *
     * @param layerName Name der Ebene
     * @return Die MapLayer oder null, wenn nicht vorhanden
     */
    private MapLayer getValidLayer(String layerName) {
        final MapLayer layer = this.tiledMap.getLayers().get(layerName);
        if (layer == null) {
            LoggerUtil.log(LogCategory.DEBUG, this, "Es gibt keine " + layerName + "-Ebene!");
        }
        return layer;
    }

    /**
     * Prüft, ob eine Ebene gültige Objekte enthält
     *
     * @param layer     Die zu prüfende Ebene
     * @param layerName Name der Ebene für Fehlermeldungen
     * @return Die MapObjects oder null, wenn keine vorhanden
     */
    private MapObjects getValidObjects(MapLayer layer, String layerName) {
        if (layer == null)
            return null;

        final MapObjects mapObjects = layer.getObjects();
        if (mapObjects == null || mapObjects.getCount() == 0) {
            LoggerUtil.log(LogCategory.DEBUG, this, "Es gibt keine Objekte in der " + layerName + "-Ebene!");
            return null;
        }
        return mapObjects;
    }

    /**
     * Liesst Property Werte zu x und y aus Tiled-editor und gibt korrekten Vector fuer SpawnPosition wirft IllegalSpawnCoordinatesException
     * wenn koordinaten nicht gesetzt sind oder die Werte ausserhalb der jeweiligen Karte ligen.
     *
     * @param properties aus Tiled MapObjekt
     * @return Vector mit skalierten und angepassten (x, y)
     * @throws IllegalSpawnCoordinatesException //TODO out of bounds check und aussagekraeftige Nachricht
     */
    private Vector2 parseEntitySpawnCoordinates(MapProperties properties) throws IllegalSpawnCoordinatesException {
        Float xInPixel = properties.get("x", Float.class);
        Float yInPixel = properties.get("y", Float.class);
        if (xInPixel == null || yInPixel == null) {
            throw new IllegalSpawnCoordinatesException();
        }
        // im editor ist der (0,0) Punkt oben Links
        float heightInTiles = this.tiledMap.getProperties().get("height", Integer.class);
        return ConversionUtils.convertFromPixelToLogicPointWithTransformedY(xInPixel, yInPixel, heightInTiles);
    }

    /**
     * Gibt die Liste aller extrahierten Kollisionsbereiche als Rectangle zurück.
     */
    public Array<Rectangle> getCollisionAreas() {
        return this.collisionWalls;
    }

    /**
     * Gibt die zugrunde liegende TiledMap zurück.
     *
     * @return Die TiledMap-Instanz dieser GameMap
     */
    public TiledMap getTiledMap() {
        return this.tiledMap;
    }

    public Vector2 getPlayerSpawnPoint() {
        return this.playerSpawnPoint;
    }

    @Override
    public void dispose() {
        this.tiledMap.dispose();
    }

    public Array<EntitySpawnPoint> getEntitySpawnPoints() {
        return this.entitySpawnPoints;
    }
}
