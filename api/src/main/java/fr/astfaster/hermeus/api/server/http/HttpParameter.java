package fr.astfaster.hermeus.api.server.http;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents an HTTP parameter.
 */
public interface HttpParameter {

    /**
     * Returns the key of the parameter
     *
     * @return A key
     */
    @NotNull String key();

    /**
     * Returns the values of the parameter
     *
     * @return A list of value
     */
    @NotNull List<String> values();

    /**
     * Returns the value of the parameter
     *
     * @return A value
     */
    @NotNull String value();

}
