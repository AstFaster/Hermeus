package fr.astfaster.hermeus.core.netty;

import io.netty.util.concurrent.FastThreadLocalThread;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@ApiStatus.Internal
public class NettyThreadFactory implements ThreadFactory {

    private final AtomicInteger threadNumber = new AtomicInteger();
    private final String threadNameFormat;

    private NettyThreadFactory(String threadNameFormat) {
        this.threadNameFormat = threadNameFormat;
    }

    @Override
    public Thread newThread(@NotNull Runnable runnable) {
        return new FastThreadLocalThread(runnable, String.format(this.threadNameFormat, this.threadNumber.getAndIncrement()));
    }

    public static ThreadFactory createThreadFactory(String name, NettyGroup group) {
        return new NettyThreadFactory("Netty Thread #%d (Transport: " + name + "; Group: " + group.name() + ")");
    }

}