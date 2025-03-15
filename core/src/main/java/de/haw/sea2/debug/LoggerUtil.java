package de.haw.sea2.debug;

import com.badlogic.gdx.Gdx;

/**
 * Hilfsklasse für die Protokollierung von Nachrichten in der Anwendung.
 */
public class LoggerUtil {

    private static final LogConfig logConfig = new LogConfig();
    private static final ClassLogConfig classLogConfig = new ClassLogConfig();

    /**
     * Gibt Zugriff auf die Log-Konfiguration.
     */
    public static LogConfig getLogConfig() {
        return logConfig;
    }

    /**
     * Gibt Zugriff auf die Klassen-Log-Konfiguration.
     */
    public static ClassLogConfig getClassLogConfig() {
        return classLogConfig;
    }

    public static void log(LogCategory category, Object source, String message) {
        if (logConfig.isLoggingEnabled(category) && ClassLogConfig.isLoggingEnabled(source.getClass())) {
            Gdx.app.log(category.getLogCategory() + ": " + source.getClass().getSimpleName(), message);
        }
    }

    public static void error(LogCategory category, Object source, String message) {
        if (logConfig.isLoggingEnabled(category) && ClassLogConfig.isLoggingEnabled(source.getClass())) {
            Gdx.app.error(category.getLogCategory() + ": " + source.getClass().getSimpleName(), message);
        }
    }

    public static void debug(LogCategory category, Object source, String message) {
        if (logConfig.isLoggingEnabled(category) && ClassLogConfig.isLoggingEnabled(source.getClass())) {
            Gdx.app.debug(category.getLogCategory() + ": " + source.getClass().getSimpleName(), message);
        }
    }
}
