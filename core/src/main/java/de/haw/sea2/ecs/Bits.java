package de.haw.sea2.ecs;

/**
 * Definiert Bit-Masken für die Box2D-Kollisionsfilterung.
 *
 * <p>
 * In der Box2D-Physik-Engine werden Bit-Masken verwendet, um zu bestimmen,
 * welche
 * physikalischen Objekte miteinander kollidieren können. Jedes Objekt gehört zu
 * einer
 * bestimmten Kategorie (representiert durch categoryBits) und kann mit anderen
 * Kategorien
 * interagieren (definiert durch maskBits).
 * </p>
 *
 * <p>
 * Die Bit-Masken werden durch Bitoperationen erstellt, wobei jede Kategorie ein
 * eigenes
 * Bit in einer 16-Bit-Maske belegt (daher der short-Typ). Durch diese Strategie
 * können
 * bis zu 16 verschiedene Kollisionskategorien definiert werden.
 * </p>
 *
 * <p>
 * Beispiel für die Verwendung:
 *
 * <pre>
 * // Definiere ein Spielerobjekt
 * fixtureDef.filter.categoryBits = Bits.BIT_PLAYER.value;
 * // Spieler kollidiert nur mit Wänden
 * fixtureDef.filter.maskBits = Bits.BIT_WALL.value;
 * </pre>
 * </p>
 */
public enum Bits {

    /**
     * Bit-Maske für Spieler-Objekte.
     * Entspricht binär 0001 (dezimal 1).
     */
    BIT_PLAYER(1 << 0),

    /**
     * Bit-Maske für Wand-Objekte.
     * Entspricht binär 0010 (dezimal 2).
     */
    BIT_WALL(1 << 1),

    //TODO tmp
    BIT_BALL(1 << 2);

    /**
     * Der numerische Wert dieser Bit-Maske als short.
     * Box2D verwendet in seinen Filtern short-Werte.
     */
    public final short value;

    /**
     * Konstruktor für die Bits-Enumeration.
     *
     * @param value Der int-Wert der Bit-Maske, wird zu short konvertiert
     */
    Bits(int value) {
        this.value = (short) value;
    }
}
