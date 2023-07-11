package fr.astfaster.hermeus.core.netty;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.ThreadFactory;
import java.util.function.Function;

@ApiStatus.Internal
public enum NettyTransport {

    NIO("nio", NioServerSocketChannel.class, factory -> new NioEventLoopGroup(0, factory)),
    EPOLL("epoll", EpollServerSocketChannel.class, factory -> new EpollEventLoopGroup(0, factory));

    private final String name;
    private final Class<? extends ServerSocketChannel> serverChannelClass;
    private final Function<ThreadFactory, EventLoopGroup> eventLoopGroupFactory;

    NettyTransport(String name, Class<? extends ServerSocketChannel> serverChannelClass, Function<ThreadFactory, EventLoopGroup> eventLoopGroupFactory) {
        this.name = name;
        this.serverChannelClass = serverChannelClass;
        this.eventLoopGroupFactory = eventLoopGroupFactory;
    }


    public Class<? extends ServerSocketChannel> serverChannelClass() {
        return this.serverChannelClass;
    }

    public EventLoopGroup eventLoopGroup(NettyGroup group) {
        return this.eventLoopGroupFactory.apply(NettyThreadFactory.createThreadFactory(this.name, group));
    }

    public static NettyTransport best() {
        return Epoll.isAvailable() ? EPOLL : NIO;
    }

}
