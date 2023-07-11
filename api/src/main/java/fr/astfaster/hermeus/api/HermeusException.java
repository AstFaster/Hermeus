package fr.astfaster.hermeus.api;

/**
 * The exception thrown by some Hermeus tasks.
 */
public class HermeusException extends RuntimeException {

    public HermeusException(String message) {
        super(message);
    }

    public HermeusException(String message, Throwable cause) {
        super(message, cause);
    }

    public HermeusException(Throwable cause) {
        super(cause);
    }

}
