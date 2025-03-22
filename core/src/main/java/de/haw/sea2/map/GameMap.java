package de.haw.sea2.map;

import java.util.Arrays;
import java.util.Optional;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Polyline;
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

/**
 * Verarbeitet und verwaltet die Spielkarte mit ihren Kollisionsbereichen.
 *
 * <p>
 * Diese Klasse ist verantwortlich für das Laden einer Tiled-Karte und das
 * Extrahieren
 * der Kollisionsbereiche aus der speziellen "collision"-Ebene der Karte. Die
 * extrahierten
 * Kollisionsbereiche werden als {@link CollisionArea}-Objekte gespeichert und
 * können später
 * von der ECSEngine verwendet werden, um physikalische Wände in der Box2D-Welt
 * zu erstellen.
 * </p>
 *
 * <p>
 * Die Klasse unterstützt zwei Arten von Kollisionsobjekten:
 * </p>
 * <ul>
 * <li>Rechteckige Bereiche (RectangleMapObject): Werden in geschlossene
 * Polygone umgewandelt</li>
 * <li>Polylinien (PolylineMapObject): Werden direkt als Linien übernommen</li>
 * </ul>
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

    private record EntitySpawnPoint(String type, String entityType, boolean hasSpawnProtection, Vector2 spawnPoint, int id) {}

    private Vector2 playerSpawnPoint;

    /**
     * Liste aller aus der Karte extrahierten Kollisionsbereiche.
     * Diese Liste enthält die umgewandelten und für Box2D aufbereiteten
     * Kollisionsbereiche.
     */
    private final Array<CollisionArea> collisionAreas;

    public GameMap(final TiledMap tiledMap) {
        this.tiledMap = tiledMap;
        this.collisionAreas = new Array<>();
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
                if (entitySpawnPoint.entityType.equals(MapEntityTypes.PLAYER.value)) {
                    this.playerSpawnPoint = entitySpawnPoint.spawnPoint();
                } else if (entitySpawnPoint.hasSpawnProtection) {
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
     * Extrahiert Kollisionsobjekte aus der "collision"-Ebene der Tiled-Karte und
     * wandelt sie
     * in CollisionArea-Objekte um.
     *
     * <p>
     * Diese Methode sucht nach einer Ebene mit dem Namen "collision" in der
     * Tiled-Karte.
     * Für jedes Objekt in dieser Ebene wird je nach Typ (Rechteck oder Polylinie)
     * ein
     * entsprechendes CollisionArea-Objekt erstellt und zur Liste hinzugefügt.
     * </p>
     *
     * <p>
     * Bei rechteckigen Objekten werden die vier Ecken des Rechtecks plus der
     * Startpunkt noch einmal
     * (für ein geschlossenes Polygon) als Vertices gespeichert. Bei Polylinien
     * werden die
     * vorhandenen Vertices direkt übernommen.
     * </p>
     *
     * <p>
     * Die Koordinaten werden im {@link CollisionArea}-Konstruktor mit UNIT_SCALE
     * multipliziert,
     * um von Pixel-Koordinaten der Tiled-Karte zu Box2D-Weltkoordinaten zu
     * konvertieren.
     * </p>
     */
    // TODO parse auch custom polygone
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
                // Verarbeitung von rechteckigen Objekten
                final RectangleMapObject rectangleMapObject = (RectangleMapObject) mapObject;
                final Rectangle rectangle = rectangleMapObject.getRectangle();

                // Erstellt ein Array für die 5 Punkte des geschlossenen Polygons (5 Punkte = 10
                // Werte)
                final float[] rectVertices = new float[10];

                // Definiert die vier Ecken des Rechtecks und wiederholt den ersten Punkt am
                // Ende
                // um ein geschlossenes Polygon zu erzeugen. Die Koordinaten sind relativ zur
                // Position des Rechtecks (daher alle lokalen Koordinaten).

                // Punkt 1: Linke untere Ecke (0,0)
                rectVertices[0] = 0;
                rectVertices[1] = 0;

                // Punkt 2: Linke obere Ecke (0,height)
                rectVertices[2] = 0;
                rectVertices[3] = rectangle.height;

                // Punkt 3: Rechte obere Ecke (width,height)
                rectVertices[4] = rectangle.width;
                rectVertices[5] = rectangle.height;

                // Punkt 4: Rechte untere Ecke (width,0)
                rectVertices[6] = rectangle.width;
                rectVertices[7] = 0;

                // Punkt 5: Wiederholung der linken unteren Ecke (0,0) für ein geschlossenes
                // Polygon
                // Box2D benötigt geschlossene Polygone für statische Körper
                rectVertices[8] = 0;
                rectVertices[9] = 0;

                // Erstellt ein CollisionArea-Objekt mit der Position des Rechtecks und den
                // Vertices
                // Die Skalierung mit UNIT_SCALE findet im Konstruktor der CollisionArea statt
                this.collisionAreas.add(new CollisionArea(rectangle.x, rectangle.y, rectVertices));

            } else if (mapObject instanceof PolylineMapObject) {
                // Verarbeitung von Polylinien-Objekten (offene Pfade)
                final PolylineMapObject polylineMapObject = (PolylineMapObject) mapObject;
                final Polyline polyline = polylineMapObject.getPolyline();

                // Erstellt ein CollisionArea-Objekt mit der Position der Polylinie und ihren
                // Vertices (Knotenpunkten der Geometrie)
                // Die Vertices werden direkt aus dem Polyline-Objekt übernommen
                // Die Skalierung mit UNIT_SCALE findet im Konstruktor der CollisionArea statt
                this.collisionAreas.add(new CollisionArea(polyline.getX(), polyline.getY(), polyline.getVertices()));
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
     * Gibt die Liste aller extrahierten Kollisionsbereiche zurück.
     *
     * <p>
     * Diese Methode wird von der ECSEngine verwendet, um physikalische Wände in der
     * Box2D-Welt zu erstellen. Die zurückgegebenen CollisionArea-Objekte enthalten
     * bereits
     * die mit UNIT_SCALE skalierten Koordinaten, die direkt für Box2D-Physik
     * verwendbar sind.
     * </p>
     *
     * @return Eine Liste von CollisionArea-Objekten mit den Kollisionsbereichen der
     * Karte
     */
    public Array<CollisionArea> getCollisionAreas() {
        return this.collisionAreas;
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
}
