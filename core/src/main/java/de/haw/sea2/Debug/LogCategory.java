package de.haw.sea2.Debug;

/**
 * Definiert die verschiedenen Logging-Stufen für die Anwendung.
 * Diese Enumeration ermöglicht eine hierarchische Steuerung der
 * Protokollierung,
 * wobei DEBUG die niedrigste und ERROR die höchste Prioritätsstufe darstellt.
 */
public enum LogCategory {
    GAME("Game"),
    PHYSICS("Physics"),
    UI("UI"),
    DEBUG("Debug");

    private final String logCategory;


    LogCategory(String category) {
        this.logCategory = category;
    }

    public String getLogCategory() {
        return logCategory;
    }

}

