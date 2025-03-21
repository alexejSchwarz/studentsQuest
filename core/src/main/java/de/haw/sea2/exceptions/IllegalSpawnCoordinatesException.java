package de.haw.sea2.exceptions;

/**
 * Checked Exception, die geworfen wird, wenn (x, y) nicht gesetzt sind oder außerhalb der jeweiligen Karte liegen
 */
public class IllegalSpawnCoordinatesException extends Exception {
    public IllegalSpawnCoordinatesException(String message) {
        super(message);
    }

    public IllegalSpawnCoordinatesException() {
        super();
    }
}
