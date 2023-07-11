package fr.astfaster.hermeus.core.server;

import fr.astfaster.hermeus.api.HermeusException;
import fr.astfaster.hermeus.api.server.HermeusRequest;
import fr.astfaster.hermeus.api.server.HermeusResponse;
import fr.astfaster.hermeus.api.server.HermeusRouter;
import fr.astfaster.hermeus.api.server.HermeusServer;
import fr.astfaster.hermeus.api.server.http.HttpParameter;
import fr.astfaster.hermeus.api.util.builder.BuilderException;
import fr.astfaster.hermeus.core.HermeusImpl;
import fr.astfaster.hermeus.core.netty.NettyGroup;
import fr.astfaster.hermeus.core.netty.NettyTransport;
import fr.astfaster.hermeus.core.server.http.HttpParameterImpl;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@ChannelHandler.Sharable
@ApiStatus.Internal
public class HermeusServerImpl extends SimpleChannelInboundHandler<FullHttpRequest> implements HermeusServer {

    private static final Logger LOGGER = LogManager.getLogger(HermeusServer.class);

    private boolean enabled;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel channel;
    private HermeusRouterImpl router;

    private final InetSocketAddress address;

    private final HermeusImpl hermeus;

    private HermeusServerImpl(HermeusImpl hermeus, InetSocketAddress address) {
        this.hermeus = hermeus;
        this.address = address;
    }

    @Override
    public void enable() {
        if (this.enabled) {
            throw new HermeusException("Server already enabled!");
        }

        final NettyTransport transport = NettyTransport.best();

        this.bossGroup = transport.eventLoopGroup(NettyGroup.BOSS);
        this.workerGroup = transport.eventLoopGroup(NettyGroup.WORKER);

        this.router = new HermeusRouterImpl("/");

        final ServerBootstrap bootstrap = new ServerBootstrap()
                .group(this.bossGroup, this.workerGroup)
                .channel(transport.serverChannelClass())
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.IP_TOS, 0x18)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(@NotNull SocketChannel ch) {
                        final ChannelPipeline pipeline = ch.pipeline();

                        pipeline.addLast("codec", new HttpServerCodec());
                        pipeline.addLast("aggregator", new HttpObjectAggregator(Integer.MAX_VALUE));
                        pipeline.addLast("handler", HermeusServerImpl.this);
                    }
                });

        if (transport == NettyTransport.EPOLL) {
            bootstrap.option(ChannelOption.TCP_FASTOPEN_CONNECT, true);
        }

        bootstrap.bind(this.address).addListener((ChannelFutureListener) future -> {
            this.channel = future.channel();

            if (!future.isSuccess()) {
                LOGGER.error("Couldn't bind server to {}!", this.address, future.cause());
            } else {
                this.enabled = true;
            }
        });
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {
        final Map<String, HttpParameter> parameters = this.queryParameters(msg.uri());
        final HermeusResponse response = new HermeusResponseImpl(this.hermeus, ctx);
        final HermeusRequestImpl request = new HermeusRequestImpl(this.hermeus, msg, parameters, response);
        final int parametersIndex = request.uri().indexOf("?");

        String uri = request.uri().substring(0, parametersIndex == -1 ? request.uri().length() : parametersIndex);

        if (uri.endsWith("/")) {
            uri = uri.substring(0, uri.length() - 1);
        }

        final String[] path = uri.split("((?=/))");

        this.router.dispatch(path, request, response);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (!(cause instanceof SocketException)) {
            LOGGER.error("{} encountered an error", this, cause);
        }

        ctx.close();
    }

    @Override
    public void disable() {
        if (!this.enabled) {
            throw new HermeusException("Server not enabled!");
        }

        this.enabled = false;

        this.channel.close().syncUninterruptibly();
        this.bossGroup.shutdownGracefully();
        this.workerGroup.shutdownGracefully();
    }

    @Override
    public boolean enabled() {
        return this.enabled;
    }

    @Override
    public @NotNull InetSocketAddress address() {
        return this.address;
    }

    @Override
    public @NotNull HermeusRouter router() {
        return this.router;
    }

    private Map<String, HttpParameter> queryParameters(String uri) {
        final QueryStringDecoder queryDecoder = new QueryStringDecoder(uri, StandardCharsets.UTF_8);
        final Map<String, List<String>> uriParameters = queryDecoder.parameters();
        final Map<String, HttpParameter> result = new HashMap<>();

        for (Map.Entry<String, List<String>> parameter : uriParameters.entrySet()) {
            result.put(parameter.getKey(), new HttpParameterImpl(parameter.getKey(), parameter.getValue()));
        }
        return result;
    }

    @Override
    public String toString() {
        return "HermeusServer[" + this.address.toString() + "]";
    }

    public static class Builder implements HermeusServer.Builder {

        private InetSocketAddress address;

        private final HermeusImpl hermeus;

        public Builder(HermeusImpl hermeus) {
            this.hermeus = hermeus;
        }

        @Override
        public @NotNull HermeusServer.Builder address(@NotNull InetSocketAddress address) {
            this.address = address;
            return this;
        }

        @Override
        public @NotNull HermeusServer.Builder address(@NotNull String hostname, int port) {
            return this.address(new InetSocketAddress(hostname, port));
        }

        @Override
        public @NotNull HermeusServer.Builder address(int port) {
            return this.address("0.0.0.0", port);
        }

        @Override
        public HermeusServer build() {
            if (this.address == null) {
                throw new BuilderException(this.getClass(), "address");
            }

            final HermeusServer server = new HermeusServerImpl(this.hermeus, this.address);

            this.hermeus.addServer(server);

            return server;
        }

    }

}
