package de.haw.sea2.lifeCicle;

public interface Restartable {

    /**
     * Vorbereitung für Neustart des Spiels
     */
    void restart();

    /**
     * soll immer im Ctor der Impl aufgerufen werden. Registriert das Restartable im Studentquest Context
     */
    void registerAsListenerAfterCreation();
}
