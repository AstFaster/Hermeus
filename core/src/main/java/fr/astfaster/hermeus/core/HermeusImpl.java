package fr.astfaster.hermeus.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.astfaster.hermeus.api.Hermeus;
import fr.astfaster.hermeus.api.server.HermeusServer;
import fr.astfaster.hermeus.core.server.HermeusServerImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HermeusImpl implements Hermeus {

    private static final Logger LOGGER = LogManager.getLogger(Hermeus.class);

    private Gson defaultSerializer = new GsonBuilder().create();

    private final List<HermeusServer> servers = new ArrayList<>();

    private HermeusImpl() {}

    public static HermeusImpl create() {
        return new HermeusImpl();
    }

    @Override
    public void stop() {
        for (HermeusServer server : servers) {
            server.disable();
        }

        this.servers.clear();
    }

    public void addServer(HermeusServer server) {
        this.servers.add(server);
    }

    @Override
    public @NotNull HermeusServer.Builder serverBuilder() {
        return new HermeusServerImpl.Builder(this);
    }

    @Override
    public @NotNull List<HermeusServer> servers() {
        return this.servers;
    }

    @Override
    public @NotNull Gson defaultSerializer() {
        return this.defaultSerializer;
    }

    @Override
    public void defaultSerializer(@NotNull Gson serializer) {
        this.defaultSerializer = serializer;
    }

}
