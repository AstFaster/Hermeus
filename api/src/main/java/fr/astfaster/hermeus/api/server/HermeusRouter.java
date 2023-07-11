package fr.astfaster.hermeus.api.server;

import io.netty.handler.codec.http.HttpMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a requests router.<br>
 * A router is mounted on a path (e.g. "/v1") and allows you to register {@linkplain HermeusHandler handlers} on sub-paths (e.g. "/test" -> "/v1/test").
 */
public interface HermeusRouter {

    /**
     * Creates a {@linkplain HermeusRouter sub-router} to this router.
     *
     * @param path The path of the sub-router
     * @return The created {@linkplain HermeusRouter sub-router}
     */
    @NotNull HermeusRouter subRouter(@NotNull String path);

    /**
     * Sets the default {@linkplain HermeusMiddleware middleware} that will be triggered before all registered {@linkplain HermeusHandler handlers}.<br>
     * Warning: it won't be triggered if a {@linkplain HermeusHandler handler} has a custom {@linkplain HermeusMiddleware middleware}.
     *
     * @param middleware The new default {@linkplain HermeusMiddleware middleware}
     */
    void middleware(@NotNull HermeusMiddleware middleware);

    /**
     * Register a {@linkplain HermeusHandler handler} for {@link HttpMethod#GET}
     *
     * @param path The path linked to the handler; paths like "/:id" will be dynamics, so the uri part will count as a parameter
     * @param handler The {@linkplain HermeusHandler handler} to register
     * @return A {@link RegisteredHandler} object
     */
    @NotNull RegisteredHandler get(@NotNull String path, @Nullable HermeusHandler handler);

    /**
     * Register a {@linkplain HermeusHandler handler} for {@link HttpMethod#POST}.
     *
     * @param path The path linked to the handler; paths like "/:id" will be dynamics, so the uri part will count as a parameter
     * @param handler The {@linkplain HermeusHandler handler} to register
     * @return A {@link RegisteredHandler} object
     */
    @NotNull RegisteredHandler post(@NotNull String path, @Nullable HermeusHandler handler);

    /**
     * Register a {@linkplain HermeusHandler handler} for {@link HttpMethod#PUT}.
     *
     * @param path The path linked to the handler; paths like "/:id" will be dynamics, so the uri part will count as a parameter
     * @param handler The {@linkplain HermeusHandler handler} to register
     * @return A {@link RegisteredHandler} object
     */
    @NotNull RegisteredHandler put(@NotNull String path, @Nullable HermeusHandler handler);

    /**
     * Register a {@linkplain HermeusHandler handler} for {@link HttpMethod#PATCH}.
     *
     * @param path The path linked to the handler; paths like "/:id" will be dynamics, so the uri part will count as a parameter
     * @param handler The {@linkplain HermeusHandler handler} to register
     * @return A {@link RegisteredHandler} object
     */
    @NotNull RegisteredHandler patch(@NotNull String path, @Nullable HermeusHandler handler);

    /**
     * Register a {@linkplain HermeusHandler handler} for {@link HttpMethod#DELETE}.
     *
     * @param path The path linked to the handler; paths like "/:id" will be dynamics, so the uri part will count as a parameter
     * @param handler The {@linkplain HermeusHandler handler} to register
     * @return A {@link RegisteredHandler} object
     */
    @NotNull RegisteredHandler delete(@NotNull String path, @Nullable HermeusHandler handler);

    /**
     * Register a {@linkplain HermeusHandler handler} for a given {@link HttpMethod}.
     *
     * @param method The HTTP method which the handler is waiting for
     * @param path The path linked to the handler; paths like "/:id" will be dynamics, so the uri part will count as a parameter
     * @param handler The {@linkplain HermeusHandler handler} to register
     * @return A {@link RegisteredHandler} object
     */
    @NotNull RegisteredHandler handler(@NotNull HttpMethod method, @NotNull String path, @Nullable HermeusHandler handler);

    /**
     * Returns the path on which the router is mounted on.
     *
     * @return A path (e.g. "/v1")
     */
    @NotNull String path();

    /**
     * Represents what is a registered {@linkplain HermeusHandler handler}.
     */
    interface RegisteredHandler {

        /**
         * Returns the HTTP method which the handler is accepting.
         *
         * @return A {@link HttpMethod}
         */
        @NotNull HttpMethod method();

        /**
         * Returns the path which the handler is mounted on.
         *
         * @return A path. E.g. "/test"
         */
        @NotNull String path();

        /**
         * Returns the registered handler
         *
         * @return A {@linkplain HermeusHandler handler} instance
         */
        @NotNull HermeusHandler handler();

        /**
         * Returns the middleware to trigger before the {@linkplain HermeusHandler handler}
         *
         * @return A {@linkplain HermeusMiddleware middleware}; or <code>null</code> if no middleware is set
         */
        @Nullable HermeusMiddleware middleware();

        /**
         * Sets the middleware to trigger before the {@linkplain HermeusHandler handler}
         *
         * @param middleware The {@linkplain HermeusMiddleware middleware} instance
         */
        void middleware(@Nullable HermeusMiddleware middleware);

    }

}
