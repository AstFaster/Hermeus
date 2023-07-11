package fr.astfaster.hermeus.api.server;

import com.google.gson.Gson;
import fr.astfaster.hermeus.api.Hermeus;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a response to send back to the client when a {@linkplain HermeusRequest request} is received.
 */
public interface HermeusResponse {

    /**
     * Send a classic HTTP response to the client.
     *
     * @param content The content of the response (as bytes)
     * @param contentType The type of the content in the response
     * @param status The status code to return
     */
    void classic(byte[] content, @NotNull String contentType, @NotNull HttpResponseStatus status);

    /**
     * Send a text response to the client.
     *
     * @param content The content of the response (as {@link String})
     * @param contentType The type of the content in the response
     * @param status The status code to return
     */
    void text(@NotNull String content, @NotNull String contentType, @NotNull HttpResponseStatus status);

    /**
     * Send a text response to the client.
     *
     * @param content The content of the response (as {@link String})
     * @param status The status code to return
     */
    void text(@NotNull String content, @NotNull HttpResponseStatus status);

    /**
     * Send a text response to the client.
     *
     * @param content The content of the response (as {@link String})
     */
    void text(@NotNull String content);

    /**
     * Send an HTML response to the client.
     *
     * @param html The HTML to send back
     * @param status The status code to return
     */
    void html(@NotNull String html, @NotNull HttpResponseStatus status);

    /**
     * Send an HTML response to the client.
     *
     * @param html The HTML to send back
     */
    void html(@NotNull String html);

    /**
     * Send a JSON response to the client with a custom serializer.
     *
     * @param serializer The serializer used to serialize the object
     * @param object The object to convert to JSON
     * @param status The status code to return
     * @param <T> The type of the object to convert
     */
    <T> void json(@NotNull Gson serializer, @NotNull T object, @NotNull HttpResponseStatus status);

    /**
     * Send a JSON response to the client with a custom serializer.
     *
     * @param serializer The serializer used to serialize the object
     * @param object The object to convert to JSON
     * @param <T> The type of the object to convert
     */
    <T> void json(@NotNull Gson serializer, @NotNull T object);

    /**
     * Send a JSON response to the client with the {@linkplain Hermeus#defaultSerializer() default serializer}.
     *
     * @param object The object to convert to JSON
     * @param status The status code to return
     * @param <T> The type of the object to convert
     */
    <T> void json(@NotNull T object, @NotNull HttpResponseStatus status);

    /**
     * Send a JSON response to the client with the {@linkplain Hermeus#defaultSerializer() default serializer}.
     *
     * @param object The object to convert to JSON
     * @param <T> The type of the object to convert
     */
    <T> void json(@NotNull T object);

}
