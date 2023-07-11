package fr.astfaster.hermeus.api.util.builder;

/**
 * Represents a builder of an object.
 *
 * @param <T> The object to build
 */
public interface IBuilder<T> {

    /**
     * Build the object
     *
     * @return The built object
     */
    T build();

}
