package de.haw.sea2.Debug;

/**
 * Definiert die verschiedenen Logging-Stufen für die Anwendung.
 * Diese Enumeration ermöglicht eine hierarchische Steuerung der
 * Protokollierung,
 * wobei DEBUG die niedrigste und ERROR die höchste Prioritätsstufe darstellt.
 */
public enum LogLevel {
    /**
     * Debug-Level für ausführliche Protokollierung während der Entwicklung.
     * Enthält detaillierte Informationen, die für die Fehlersuche hilfreich sind.
     */
    DEBUG("DEBUG"),

    /**
     * Info-Level für allgemeine Informationen über den Programmablauf.
     * Protokolliert wichtige Ereignisse, die nicht als Fehler eingestuft werden.
     */
    INFO("INFO"),

    /**
     * Error-Level für kritische Probleme und Ausnahmen.
     * Protokolliert Fehler, die den normalen Programmablauf stören.
     */
    ERROR("ERROR");

    /** Der String-Wert, der dieses Log-Level repräsentiert. */
    public final String logLevel;

    /**
     * Konstruktor für die LogLevel-Enumeration.
     * 
     * @param level Der String-Wert, der dieses Log-Level repräsentiert
     */
    LogLevel(String level) {
        this.logLevel = level;
    }

    /**
     * Gibt den String-Wert zurück, der dieses Log-Level repräsentiert.
     * 
     * @return Der String-Wert des Log-Levels
     */
    public String getLogLevel() {
        return this.logLevel;
    }
}