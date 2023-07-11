package fr.astfaster.hermeus.api.server;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a handler which is executed when a request is received on an endpoint.
 */
@FunctionalInterface
public interface HermeusHandler {

    /**
     * This method is triggered when a request is received
     *
     * @param request The received request
     * @param response The response to send back
     */
    void handle(@NotNull HermeusRequest request, @NotNull HermeusResponse response);

}
