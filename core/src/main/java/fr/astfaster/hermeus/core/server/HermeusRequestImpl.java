package fr.astfaster.hermeus.core.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import fr.astfaster.hermeus.api.Hermeus;
import fr.astfaster.hermeus.api.server.HermeusRequest;
import fr.astfaster.hermeus.api.server.HermeusResponse;
import fr.astfaster.hermeus.api.server.http.HttpParameter;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.*;

@ApiStatus.Internal
class HermeusRequestImpl implements HermeusRequest {

    private final FullHttpRequest handle;
    private final ByteBuf body;
    private final Map<String, HttpParameter> parameters;

    private final HermeusResponse response;

    private final Hermeus hermeus;

    public HermeusRequestImpl(Hermeus hermeus, FullHttpRequest handle, Map<String, HttpParameter> parameters, HermeusResponse response) {
        this.hermeus = hermeus;
        this.handle = handle;
        this.body = handle.content();
        this.parameters = parameters;
        this.response = response;
    }

    @Override
    public @NotNull String uri() {
        return this.handle.uri();
    }

    @Override
    public @NotNull HttpMethod method() {
        return this.handle.method();
    }

    @Override
    public @Nullable HttpParameter parameter(String key) {
        return this.parameters.get(key);
    }

    @Override
    public @NotNull Collection<HttpParameter> parameters() {
        return this.parameters.values();
    }

    @Override
    public @NotNull HttpHeaders headers() {
        return this.handle.headers();
    }

    @Override
    public @NotNull ByteBuf body() {
        return this.body;
    }

    @Override
    public <T> T jsonBody(@NotNull Gson deserializer, @NotNull Class<T> objectClass) {
        final String contentType = this.headers().get(HttpHeaderNames.CONTENT_TYPE);

        if (contentType == null || !contentType.contains("application/json")) {
            this.response.text("Bad Content-Type", HttpResponseStatus.BAD_REQUEST);
            return null;
        }

        final String json = this.body.toString(StandardCharsets.UTF_8);

        try {
            return deserializer.fromJson(json, objectClass);
        } catch (Exception e) {
            this.response.text("Bad json body!", HttpResponseStatus.BAD_REQUEST);
            return null;
        }
    }

    @Override
    public <T> T jsonBody(@NotNull Class<T> objectClass) {
        return this.jsonBody(this.hermeus.defaultSerializer(), objectClass);
    }

    @Override
    public JsonObject jsonBody(@NotNull Gson deserializer) {
       return this.jsonBody(deserializer, JsonObject.class);
    }

    @Override
    public JsonObject jsonBody() {
        return this.jsonBody(JsonObject.class);
    }

    void addParameters(List<HttpParameter> pathParameters) {
        for (HttpParameter parameter : pathParameters) {
            this.parameters.put(parameter.key(), parameter);
        }
    }
}
