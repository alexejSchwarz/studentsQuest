package de.haw.sea2.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import de.haw.sea2.debug.LogCategory;
import de.haw.sea2.debug.LoggerUtil;

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
public class GameMap implements Disposable {
    /**
     * Die geladene Tiled-Karte, die alle Ebenen und Objekte enthält.
     */
    private final TiledMap tiledMap;

    /**
     * Liste aller aus der Karte extrahierten Kollisionsbereiche.
     * Diese Liste enthält die umgewandelten und für Box2D aufbereiteten
     * Kollisionsbereiche.
     */
    private final Array<CollisionArea> collisionAreas;

    public GameMap(final TiledMap tiledMap) {
        this.tiledMap = tiledMap;
        this.collisionAreas = new Array<>();
        parseCollisionLayer();
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
            LoggerUtil.error(LogCategory.ERROR,this,"There is no collision layer!");
            return; // Methode wird abgebrochen, wenn keine Kollisionsebene vorhanden ist
        }

        final MapObjects mapObjects = collisionLayer.getObjects();

        // Überprüft, ob Objekte in der Kollisionsebene definiert sind
        if (mapObjects == null) {
            LoggerUtil.error(LogCategory.ERROR,this,"There are no collision MapObjects defined!");
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
                LoggerUtil.error(LogCategory.ERROR,this,"MoapObject of Type: " + mapObject + "is not supported!");
            }
        }
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
     *         Karte
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

    @Override
    public void dispose() {
        this.tiledMap.dispose();
    }
}
