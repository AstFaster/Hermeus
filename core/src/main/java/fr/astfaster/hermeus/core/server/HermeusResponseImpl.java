package fr.astfaster.hermeus.core.server;

import com.google.gson.Gson;
import fr.astfaster.hermeus.api.Hermeus;
import fr.astfaster.hermeus.api.server.HermeusResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

@ApiStatus.Internal
class HermeusResponseImpl implements HermeusResponse {

    private final ChannelHandlerContext ctx;

    private final Hermeus hermeus;

    public HermeusResponseImpl(Hermeus hermeus, ChannelHandlerContext ctx) {
        this.hermeus = hermeus;
        this.ctx = ctx;
    }

    @Override
    public void classic(byte[] content, @NotNull String contentType, @NotNull HttpResponseStatus status) {
        final ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();

        byteBuf.writeBytes(content);

        final FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, byteBuf);

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, String.format("%s; charset=UTF-8", contentType));

        this.ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void text(@NotNull String content, @NotNull String contentType, @NotNull HttpResponseStatus status) {
        this.classic(content.getBytes(StandardCharsets.UTF_8), contentType, status);
    }

    @Override
    public void text(@NotNull String content, @NotNull HttpResponseStatus status) {
        this.text(content, HttpHeaderValues.TEXT_PLAIN.toString(), status);
    }

    @Override
    public void text(@NotNull String content) {
        this.text(content, HttpResponseStatus.OK);
    }

    @Override
    public void html(@NotNull String html, @NotNull HttpResponseStatus status) {
        this.text(html, HttpHeaderValues.TEXT_HTML.toString(), status);
    }

    @Override
    public void html(@NotNull String html) {
        this.text(html, HttpResponseStatus.OK);
    }

    @Override
    public <T> void json(@NotNull Gson serializer, @NotNull T object, @NotNull HttpResponseStatus status) {
        this.text(serializer.toJson(object), HttpHeaderValues.APPLICATION_JSON.toString(), status);
    }

    @Override
    public <T> void json(@NotNull Gson serializer, @NotNull T object) {
        this.json(serializer, object, HttpResponseStatus.OK);
    }

    @Override
    public <T> void json(@NotNull T object, @NotNull HttpResponseStatus status) {
        this.json(this.hermeus.defaultSerializer(), object, status);
    }

    @Override
    public <T> void json(@NotNull T object) {
        this.json(object, HttpResponseStatus.OK);
    }

}
