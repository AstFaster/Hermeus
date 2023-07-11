package fr.astfaster.hermeus.core.server;

import fr.astfaster.hermeus.api.HermeusException;
import fr.astfaster.hermeus.api.server.*;
import fr.astfaster.hermeus.api.server.http.HttpParameter;
import fr.astfaster.hermeus.core.server.http.HttpParameterImpl;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ApiStatus.Internal
class HermeusRouterImpl implements HermeusRouter {

    private final Map<String, HermeusRouter> subRouters = new ConcurrentHashMap<>();
    private final Map<HttpMethod, Map<String, RegisteredHandler>> handlers = new ConcurrentHashMap<>();

    private HermeusMiddleware middleware;

    private final String path;

    public HermeusRouterImpl(String path) {
        this.path = path;
    }

    void dispatch(@NotNull String[] path, @NotNull HermeusRequestImpl request, @NotNull HermeusResponse response) {
        // The priority is to router
        final HermeusRouterImpl subRouter = path.length > 0 ? (HermeusRouterImpl) this.subRouters.get(path[0]) : null;

        // If the router is null we look for a handler
        if (subRouter == null) {
            final Map<String, RegisteredHandler> handlers = this.handlers.get(request.method());

            if (handlers != null) {
                final RegisteredHandler handler = this.findHandler(request, path);

                if (handler != null) {
                    HermeusMiddleware middleware = handler.middleware();

                    if (middleware == null) {
                        middleware = this.middleware;
                    }

                    if (middleware != null) {
                        if (middleware.process(request, response)) {
                            handler.handler().handle(request, response);
                        }
                    } else {
                        handler.handler().handle(request, response);
                    }
                    return;
                }
            }

            response.text("Unknown endpoint!", HttpResponseStatus.NOT_FOUND);
            return;
        }

        subRouter.dispatch(Arrays.copyOfRange(path, 1, path.length), request, response);
    }

    private RegisteredHandler findHandler(@NotNull HermeusRequestImpl request, @NotNull String[] path) {
        final Map<String, RegisteredHandler> handlers = this.handlers.get(request.method());

        if (handlers != null) {
            final String joinedPath = String.join("", path);

            // Check for an exact match first
            RegisteredHandler handler = handlers.get(joinedPath);

            if (handler != null) {
                return handler;
            }

            // Check for dynamic path handlers
            for (Map.Entry<String, RegisteredHandler> entry : handlers.entrySet()) {
                final String handlerPath = entry.getKey();

                if (handlerPath.contains(":")) {
                    final List<HttpParameter> pathParameters = this.extractParameters(handlerPath, joinedPath);

                    if (pathParameters != null) {
                        request.addParameters(pathParameters);

                        return entry.getValue();
                    }
                }
            }
        }
        return null;
    }

    private List<HttpParameter> extractParameters(String handlerPath, String requestPath) {
        final String[] handlerPathParts = handlerPath.substring(1).split("/");
        final String[] requestPathParts = requestPath.substring(1).split("/");

        if (handlerPathParts.length != requestPathParts.length) {
            return null; // Number of path segments doesn't match
        }

        final List<HttpParameter> result = new ArrayList<>();

        for (int i = 0; i < handlerPathParts.length; i++) {
            final String handlerPathPart = handlerPathParts[i];
            final String requestPathPart = requestPathParts[i];

            if (handlerPathPart.startsWith(":")) {
                final String key = handlerPathPart.substring(1);

                result.add(new HttpParameterImpl(key, List.of(requestPathPart)));
            } else if (!handlerPathPart.equals(requestPathPart)) {
                return null; // Path segments don't match
            }
        }
        return result;
    }


    @Override
    public @NotNull HermeusRouter subRouter(@NotNull String path) {
        path = path.toLowerCase(Locale.ROOT);

        final HermeusRouter router = new HermeusRouterImpl(path);

        this.subRouters.put(path, router);

        return router;
    }

    @Override
    public void middleware(@NotNull HermeusMiddleware middleware) {
        this.middleware = middleware;
    }

    @Override
    public @NotNull RegisteredHandler get(@NotNull String path, @Nullable HermeusHandler handler) {
        return this.handler(HttpMethod.GET, path, handler);
    }

    @Override
    public @NotNull RegisteredHandler post(@NotNull String path, @Nullable HermeusHandler handler) {
        return this.handler(HttpMethod.POST, path, handler);
    }

    @Override
    public @NotNull RegisteredHandler put(@NotNull String path, @Nullable HermeusHandler handler) {
        return this.handler(HttpMethod.PUT, path, handler);
    }

    @Override
    public @NotNull RegisteredHandler patch(@NotNull String path, @Nullable HermeusHandler handler) {
        return this.handler(HttpMethod.PATCH, path, handler);
    }

    @Override
    public @NotNull RegisteredHandler delete(@NotNull String path, @Nullable HermeusHandler handler) {
        return this.handler(HttpMethod.DELETE, path, handler);
    }

    @Override
    public @NotNull RegisteredHandler handler(@NotNull HttpMethod method, @NotNull String path, @Nullable HermeusHandler handler) {
        if (!path.startsWith("/")) {
            throw new HermeusException("Invalid handler path! (not starting with '/')");
        }

        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        final RegisteredHandler registeredHandler = new RegisteredHandler(method, path, handler);

        this.handlers.merge(method, new HashMap<>(), (oldValue, newValue) -> oldValue).put(path, registeredHandler);

        return registeredHandler;
    }

    @Override
    public @NotNull String path() {
        return this.path;
    }

    private class RegisteredHandler implements HermeusRouter.RegisteredHandler {

        private final HttpMethod method;
        private final String path;
        private final HermeusHandler handler;
        private HermeusMiddleware middleware = HermeusRouterImpl.this.middleware;

        public RegisteredHandler(HttpMethod method, String path, HermeusHandler handler) {
            this.method = method;
            this.path = path;
            this.handler = handler;
        }

        @Override
        public @NotNull HttpMethod method() {
            return this.method;
        }

        @Override
        public @NotNull String path() {
            return this.path;
        }

        @Override
        public @NotNull HermeusHandler handler() {
            return this.handler;
        }

        @Override
        public @Nullable HermeusMiddleware middleware() {
            return this.middleware;
        }

        @Override
        public void middleware(HermeusMiddleware middleware) {
            this.middleware = middleware;
        }

    }

}
