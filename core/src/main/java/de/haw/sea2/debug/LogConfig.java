package de.haw.sea2.debug;

import java.util.EnumMap;
import java.util.Map;

/**
 * Verwaltet die Logging-Konfiguration für verschiedene Kategorien.
 */
public class LogConfig {
    private final Map<LogCategory, Boolean> logSettings;

    /**
     * Erstellt eine neue LogConfig mit Standardwerten.
     */
    public LogConfig() {
        logSettings = new EnumMap<>(LogCategory.class);

        //false = es wird nicht geloggt, true = es wird geloggt
        logSettings.put(LogCategory.GAME, true);
        logSettings.put(LogCategory.PHYSICS, false);
        logSettings.put(LogCategory.UI, false);
        logSettings.put(LogCategory.DEBUG, true);
    }

    /**
     * Gibt zurück, ob Logging für eine bestimmte Kategorie aktiviert ist.
     *
     * @param category Die Log-Kategorie.
     * @return true, wenn Logging aktiviert ist, sonst false.
     */
    public boolean isLoggingEnabled(LogCategory category) {
        return logSettings.getOrDefault(category, true);
    }

    /**
     * Aktiviert oder deaktiviert das Logging für eine bestimmte Kategorie.
     *
     * @param category Die Log-Kategorie.
     * @param enabled  true, um das Logging zu aktivieren, false, um es zu deaktivieren.
     */
    public void setLoggingEnabled(LogCategory category, boolean enabled) {
        logSettings.put(category, enabled);
    }
}
