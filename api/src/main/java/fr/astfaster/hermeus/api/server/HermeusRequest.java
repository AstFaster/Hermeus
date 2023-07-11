package fr.astfaster.hermeus.api.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import fr.astfaster.hermeus.api.Hermeus;
import fr.astfaster.hermeus.api.server.http.HttpParameter;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Represents a received HTTP request.
 */
public interface HermeusRequest {

    /**
     * Returns the URI of the request.
     *
     * @return An uri
     */
    @NotNull String uri();

    /**
     * Returns the HTTP method of the request.
     *
     * @return A {@link HttpMethod}
     */
    @NotNull HttpMethod method();

    /**
     * Find a parameter of the request by giving its key.
     *
     * @param key The key of the parameter to find
     * @return The found {@linkplain HttpParameter parameter}; or <code>null</code> if the parameter was not found in the request
     */
    @Nullable HttpParameter parameter(String key);

    /**
     * Check whether the request contains a parameter with a given key.
     *
     * @param key The key of the parameter to look for
     * @return <code>true</code> if the parameter exists; <code>false</code> otherwise
     */
    default boolean containsParameter(String key) {
        return this.parameter(key) != null;
    }

    /**
     * Returns the parameters of the request
     *
     * @return A collection of {@linkplain HttpParameter parameters}
     */
    @NotNull Collection<HttpParameter> parameters();

    /**
     * Returns the headers of the request
     *
     * @return The {@linkplain HttpHeaders headers} object
     */
    @NotNull HttpHeaders headers();

    /**
     * Returns the body of the request
     *
     * @return A {@linkplain ByteBuf buffer}
     */
    @NotNull ByteBuf body();

    /**
     * Tries to convert the body of the request to a JSON with a custom deserializer.
     *
     * @param deserializer The {@linkplain Gson deserializer} to use
     * @param objectClass The class of the object to deserialize from the body
     * @return The deserialized object from the body
     * @param <T> The type of the object to return
     */
    <T> T jsonBody(@NotNull Gson deserializer, @NotNull Class<T> objectClass);

    /**
     * Tries to convert the body of the request to a JSON with the {@linkplain Hermeus#defaultSerializer() default deserializer}.
     *
     * @param objectClass The class of the object to deserialize from the body
     * @return The deserialized object from the body
     * @param <T> The type of the object to return
     */
    <T> T jsonBody(@NotNull Class<T> objectClass);

    /**
     * Tries to convert the body of the request to a JSON with a custom deserializer
     *
     * @param deserializer The {@linkplain Gson deserializer} to use
     * @return The deserialized body as a {@link JsonObject}
     */
    JsonObject jsonBody(@NotNull Gson deserializer);

    /**
     * Tries to convert the body of the request to a JSON with the {@linkplain Hermeus#defaultSerializer() default deserializer}.
     *
     * @return The deserialized body as a {@link JsonObject}
     */
    JsonObject jsonBody();

}
