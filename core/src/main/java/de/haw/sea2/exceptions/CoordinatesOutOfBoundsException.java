package de.haw.sea2.exceptions;

public class CoordinatesOutOfBoundsException extends Exception {
    public CoordinatesOutOfBoundsException(String message) {
        super(message);
    }

    public CoordinatesOutOfBoundsException() {
        super();
    }
}
