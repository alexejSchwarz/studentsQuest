package de.haw.sea2.debug;

import com.badlogic.gdx.graphics.Color;

/**
 * Zentrale Konfigurationsklasse für das Debug-System.
 * Enthält alle Konstanten für Schriftgrößen, Abstände, Farben und andere
 * Parameter des Debug-UI.
 */
public class DebugConfig {

    /** Aktivierung des gesamten Debug-Systems */
    public static final boolean DEBUG_ENABLED = true;

    /** Aktualisierungsintervall der Debug-Informationen in Sekunden */
    public static final float UPDATE_INTERVAL = 0.5f; // TODO: research if Fixed timestep is a better approach see
                                                      // Studentsquest

    /** X-Position für die Anzeige (vom rechten Rand aus) */
    public static final float POSITION_X_OFFSET = 220f;

    /** Anfängliche Y-Position für die Anzeige (vom oberen Rand aus) */
    public static final float POSITION_Y_OFFSET = 10f;

    /** Vertikaler Abstand zwischen den Debug-Zeilen */
    public static final float LINE_SPACING = 20f;

    /** Skalierung der Debug-Schrift */
    public static final float FONT_SCALE = 0.8f;

    /** Farbe der Standard-Debug-Informationen */
    public static final Color DEFAULT_COLOR = Color.YELLOW;

    /** Farbe für Warnungen */
    public static final Color WARNING_COLOR = Color.ORANGE;

    /** Farbe für Fehler */
    public static final Color ERROR_COLOR = Color.RED;

    /** Maximale Länge für gekürzte Strings */
    public static final int MAX_STRING_LENGTH = 20;

    /** Anzahl der Nachkommastellen für Float-Werte */
    public static final int DECIMAL_PLACES = 2;

    /** Format-String für Positionsangaben */
    public static final String POSITION_FORMAT = "Player Pos: %." + DECIMAL_PLACES + "f, %." + DECIMAL_PLACES + "f";

    /** Format-String für Geschwindigkeitsangaben */
    public static final String VELOCITY_FORMAT = "Player Vel: %." + DECIMAL_PLACES + "f, %." + DECIMAL_PLACES + "f";

    /** Format-String für Zeit */
    public static final String TIME_FORMAT = "Zeit: %02d:%02d:%02d";
}
