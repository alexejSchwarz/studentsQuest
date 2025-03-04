package de.haw.sea2.map;

import de.haw.sea2.StudentsQuest;

/**
 * Repräsentiert einen Kollisionsbereich in der Spielwelt.
 * 
 * <p>
 * Diese Klasse speichert die Position und Form eines Kollisionsbereichs,
 * der für die Box2D-Physik-Engine verwendet wird. Kollisionsbereiche werden
 * typischerweise aus den Objektebenen von Tiled-Karten extrahiert und als
 * Wände oder andere feste Hindernisse in der Spielwelt platziert.
 * </p>
 * 
 * <p>
 * Alle Koordinaten werden im Konstruktor dieser Klasse automatisch mit dem
 * UNIT_SCALE-Faktor skaliert, um von Pixel-Koordinaten der Karte in die
 * Einheiten der Box2D-Welt zu konvertieren. Alle Getter-Methoden geben daher
 * bereits umgerechnete Werte zurück.
 * </p>
 */
public class CollisionArea {
    /**
     * Die X-Koordinate des Ursprungs des Kollisionsbereichs in der Box2D-Welt.
     * Dieser Wert wurde bereits im Konstruktor mit UNIT_SCALE skaliert und ist
     * daher direkt als Box2D-Weltkoordinate verwendbar.
     */
    private final float x;

    /**
     * Die Y-Koordinate des Ursprungs des Kollisionsbereichs in der Box2D-Welt.
     * Dieser Wert wurde bereits im Konstruktor mit UNIT_SCALE skaliert und ist
     * daher direkt als Box2D-Weltkoordinate verwendbar.
     */
    private final float y;

    /**
     * Die Eckpunkte des Kollisionsbereichs als flaches Array in der Form [x1, y1,
     * x2, y2, ...].
     * Alle Werte in diesem Array wurden bereits im Konstruktor mit UNIT_SCALE
     * skaliert
     * und sind relativ zum Ursprung (x, y) gespeichert. Sie sind daher direkt als
     * Box2D-Weltkoordinaten verwendbar.
     */
    private final float[] vertices;

    /**
     * Erstellt einen neuen Kollisionsbereich an der angegebenen Position mit den
     * angegebenen Eckpunkten.
     * 
     * <p>
     * Dieser Konstruktor nimmt Pixel-Koordinaten aus der Tiled-Karte entgegen und
     * konvertiert sie automatisch in Box2D-Weltkoordinaten durch Multiplikation mit
     * UNIT_SCALE. Die Umrechnung erfolgt hier direkt bei der Zuweisung zu den
     * Instanzvariablen und bei den Vertices in der for-Schleife.
     * </p>
     *
     * @param x        Die X-Koordinate des Ursprungs des Kollisionsbereichs in
     *                 Pixel (wird hier mit UNIT_SCALE multipliziert)
     * @param y        Die Y-Koordinate des Ursprungs des Kollisionsbereichs in
     *                 Pixel (wird hier mit UNIT_SCALE multipliziert)
     * @param vertices Die Eckpunkte des Kollisionsbereichs als flaches Array [x1,
     *                 y1, x2, y2, ...] in Pixel (alle Werte werden hier mit
     *                 UNIT_SCALE multipliziert)
     */
    public CollisionArea(final float x, final float y, float[] vertices) {
        // Hier erfolgt die Umrechnung von Pixel in Box2D-Weltkoordinaten
        this.x = x * StudentsQuest.UNIT_SCALE;
        this.y = y * StudentsQuest.UNIT_SCALE;
        this.vertices = vertices;

        // Hier werden alle Vertices-Koordinaten mit UNIT_SCALE skaliert
        for (int i = 0; i < vertices.length; i += 2) {
            vertices[i] = vertices[i] * StudentsQuest.UNIT_SCALE;
            vertices[i + 1] = vertices[i + 1] * StudentsQuest.UNIT_SCALE;
        }
    }

    /**
     * Gibt die skalierten Eckpunkte des Kollisionsbereichs zurück.
     * 
     * <p>
     * Das zurückgegebene Array ist ein flaches Array von Koordinaten in der Form
     * [x1, y1, x2, y2, ...], wobei jedes Koordinatenpaar einen Eckpunkt des
     * Kollisionsbereichs darstellt. Alle Werte wurden bereits im Konstruktor mit
     * UNIT_SCALE
     * skaliert und können daher direkt von der Box2D-Physik-Engine verwendet
     * werden.
     * </p>
     * 
     * <p>
     * Diese Methode wird von der ECSEngine verwendet, um ChainShape-Objekte für
     * die Box2D-Physik zu erstellen.
     * </p>
     *
     * @return Die bereits mit UNIT_SCALE skalierten Eckpunkte des
     *         Kollisionsbereichs
     */
    public float[] getVertices() {
        return vertices;
    }

    /**
     * Gibt die X-Koordinate des Ursprungs des Kollisionsbereichs zurück.
     * 
     * <p>
     * Der zurückgegebene Wert wurde bereits im Konstruktor mit UNIT_SCALE in
     * Box2D-Weltkoordinaten umgerechnet und kann daher direkt von der
     * Box2D-Physik-Engine verwendet werden.
     * </p>
     *
     * @return Die bereits mit UNIT_SCALE umgerechnete X-Koordinate in
     *         Box2D-Weltkoordinaten
     */
    public float getX() {
        return x;
    }

    /**
     * Gibt die Y-Koordinate des Ursprungs des Kollisionsbereichs zurück.
     * 
     * <p>
     * Der zurückgegebene Wert wurde bereits im Konstruktor mit UNIT_SCALE in
     * Box2D-Weltkoordinaten umgerechnet und kann daher direkt von der
     * Box2D-Physik-Engine verwendet werden.
     * </p>
     *
     * @return Die bereits mit UNIT_SCALE umgerechnete Y-Koordinate in
     *         Box2D-Weltkoordinaten
     */
    public float getY() {
        return y;
    }
}