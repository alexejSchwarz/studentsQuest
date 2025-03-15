package de.haw.sea2.debug;

/**
 * Definiert die verschiedenen Kategorien für die Protokollierung in der Anwendung.
 * Jede Kategorie repräsentiert eine unterschiedliche Art von Log-Nachricht.
 * Die hierarchische Struktur ermöglicht eine gezielte Steuerung der Protokollierung,
 * von allgemeinen Debug-Nachrichten bis hin zu Fehlern.
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
