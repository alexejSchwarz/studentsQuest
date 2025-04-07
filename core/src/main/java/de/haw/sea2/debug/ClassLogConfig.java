package de.haw.sea2.debug;

import java.util.HashMap;
import java.util.Map;

/**
 * Verwaltet die Logging-Einstellungen für einzelne Klassen.
 */
public class ClassLogConfig {

    private static final Map<Class<?>, Boolean> classLogSettings = new HashMap<>();

    // Standardmäßig ist das Logging für alle Klassen aktiviert.
    // Falls das Logging für eine bestimmte Klasse deaktiviert werden soll,
    // kann dies hier explizit festgelegt werden.
    static {
        //classLogSettings.put(MainMenuScreen.class, false);
    }

    /**
     * Aktiviert oder deaktiviert das Logging für eine bestimmte Klasse.
     *
     * @param clazz   Die Klasse, für die Logging gesteuert wird.
     * @param enabled true, wenn Logging aktiviert sein soll, sonst false.
     */
    public void setLoggingEnabled(Class<?> clazz, boolean enabled) {
        classLogSettings.put(clazz, enabled);
    }

    /**
     * Prüft, ob Logging für eine bestimmte Klasse aktiviert ist.
     *
     * @param clazz Die Klasse.
     * @return true, wenn Logging aktiviert ist, sonst false (Standard: true).
     */
    public static boolean isLoggingEnabled(Class<?> clazz) {
        return classLogSettings.getOrDefault(clazz, true);
    }
}
