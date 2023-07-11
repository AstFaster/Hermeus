package fr.astfaster.hermeus.api;

import com.google.gson.Gson;
import fr.astfaster.hermeus.api.server.HermeusServer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The main class of Hermeus HTTP server library.<br>
 * It contains all the methods to manage the library.
 */
public interface Hermeus {

    /**
     * Stop Hermeus process.<br>
     * It will close every server connection.
     */
    void stop();

    /**
     * Returns a new builder of a {@linkplain HermeusServer server}
     *
     * @return A {@link HermeusServer.Builder}
     */
    @NotNull HermeusServer.Builder serverBuilder();

    /**
     * Returns all the registered servers
     *
     * @return A list of {@linkplain HermeusServer servers}
     */
    @NotNull List<HermeusServer> servers();

    /**
     * Returns the default JSON serializer used by Hermeus.
     *
     * @return A {@link Gson} instance
     */
    @NotNull Gson defaultSerializer();

    /**
     * Set the default JSON serializer which Hermeus will use.
     *
     * @param serializer The new {@link Gson} serializer instance
     */
    void defaultSerializer(@NotNull Gson serializer);

}
