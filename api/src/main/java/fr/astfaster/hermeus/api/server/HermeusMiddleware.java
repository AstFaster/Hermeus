package fr.astfaster.hermeus.api.server;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a middleware which is executed before a {@linkplain HermeusHandler handler}.<br>
 * It defines if {@linkplain HermeusHandler handler} handler will be executed or not.<br><br>
 *
 * E.g. it can be used for security (check for a token...)
 */
@FunctionalInterface
public interface HermeusMiddleware {

    /**
     * This method is executed when the middleware is triggered.
     *
     * @param request The incoming request to handle
     * @param response The response to send back to the request
     * @return <code>true</code> if the {@linkplain HermeusHandler handler} will be executed or not.
     */
    boolean process(@NotNull HermeusRequest request, @NotNull HermeusResponse response);

}
