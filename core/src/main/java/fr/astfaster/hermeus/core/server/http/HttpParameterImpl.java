package fr.astfaster.hermeus.core.server.http;

import fr.astfaster.hermeus.api.server.http.HttpParameter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@ApiStatus.Internal
public record HttpParameterImpl(String key, List<String> values) implements HttpParameter {

    @Override
    public @NotNull String key() {
        return this.key;
    }

    @Override
    public @NotNull List<String> values() {
        return this.values;
    }

    @Override
    public @NotNull String value() {
        return this.values.get(0);
    }

}
