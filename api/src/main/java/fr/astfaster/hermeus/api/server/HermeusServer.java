package fr.astfaster.hermeus.api.server;

import fr.astfaster.hermeus.api.util.builder.IBuilder;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;

/**
 * Represents an HTTP server listening for incoming requests.
 */
public interface HermeusServer {

    /**
     * Enable the server; it will accept incoming requests.
     */
    void enable();

    /**
     * Disable the server; it will no longer accept requests.
     */
    void disable();

    /**
     * Returns the address on which the server is bound.
     *
     * @return A {@link InetSocketAddress}
     */
    @NotNull InetSocketAddress address();

    /**
     * Returns the main router of the server.<br>
     * This router is bound on "/" path.
     *
     * @return The main {@linkplain HermeusRouter router} instance
     */
    @NotNull HermeusRouter router();

    /**
     * Check whether the server is enabled or not.
     *
     * @return <code>true</code> if the server is enabled; <code>false</code> otherwise.
     */
    boolean enabled();

    /**
     * The builder for a {@linkplain HermeusServer server}.
     */
    interface Builder extends IBuilder<HermeusServer> {

        /**
         * Sets the address of the server
         *
         * @param address An {@linkplain InetSocketAddress address}
         * @return This {@link Builder} instance
         */
        @NotNull Builder address(@NotNull InetSocketAddress address);

        /**
         * Sets the address of the server; with the hostname and the port
         *
         * @param hostname The address on which the server will be bound
         * @param port The port on which the server will be bound
         * @return This {@link Builder} instance
         */
        @NotNull Builder address(@NotNull String hostname, int port);

        /**
         * Sets the address of the server; only with the port.<br>
         * The hostname will 0.0.0.0.
         *
         * @param port The port on which the server will be bound
         * @return This {@link Builder} instance
         */
        @NotNull Builder address(int port);

    }

}
