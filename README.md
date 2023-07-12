# Hermeus

Hermeus is a library based on Netty which allows you to create HTTP servers easily.<br>

## How to use it?

### Import the dependency
````groovy
repositories {
    maven {
        name = "Hermeus_maven"
        url = 'https://maven.pkg.github.com/AstFaster/Hermeus/'
    }
}

dependencies {
    implementation 'fr.astfaster:hermeus-api:1.0.0' // Imports only the API
    implementation 'fr.astfaster:hermeus-core:1.0.0' // Imports the implementation (needed to instantiate Hermeus)
}
````

### Instantiate Hermeus
To instantiate Hermeus you have to use the default implementation.
```java
final Hermeus hermeus = HermeusImpl.create();
```

### Create an HTTP server
Hermeus allows you to start multiple HTTP servers on one Java application.<br>
So first, create the server :
```java
// Create it with an address and a port...
final String address = "0.0.0.0";
final int port = 8080;
final HermeusServer server = hermeus.serverBuilder()
        .address(address, port)
        .build();

// or directly with an InetSocketAddress
final InetSocketAddress address = new InetSocketAddress("0.0.0.0", 8080);
final HermeusServer server = hermeus.serverBuilder()
        .address(address)
        .build();
```
Note: you can check whether a server is enabled or not with `HermeusServer#enabled()`

### Enable and disable an HTTP server
After creating an HTTP server you have to enable it to accept incoming requests.
```java
server.enable();
```
You can also disable it when you have to :
```java
server.disable();
```

### Handle requests
#### Routing
Every `HermeusServer` has a router mounted on the following path: "/". You can accces it with `HermeusServer#router()`.<br>
A router is used to route an incoming request to a handler.<br><br>

You can create sub-routers by using `HermeusRouter#subRouter(@NotNull String path)`.<br>
E.g. I want to create a "/v1/" sub-router:
```java
final HermeusServer server = ...;
final HermeusRouter router = server.router().subRouter("/v1");
```

#### Handlers
Now that you know routing works, you have to understand how HTTP requests can be processed.<br>
Every HTTP request has a "method": e.g. GET / POST / PUT / DELETE / PATCH / OPTIONS...<br>
So, to register a handler you have to know on which path you want to mount it to, and which method it will accept.<br>
E.g. I create a "/welcome" handler that returns "Welcome to Hermeus!" if the HTTP method is GET.
```java
final HermeusServer server = ...;
final HermeusRouter router = ...;

router.get("/welcome", (request, response) -> response.text("Welcome to Hermeus!"));
```
`HermeusRouter` accepts already a lot of HTTP method:
```java
router.post(...);
router.put(...);
router.patch(...);
router.delete(...);
```
But if you want to use another one, you can use `HermeusRouter#handler(@NotNull HttpMethod method, @NotNull String path, @Nullable HermeusHandler handler)`.<br><br>

To get the parameters or the body of the request, you can use these methods:
```java
request.containsParameter("myParameter") // Checks if a parameter was given in the URI
request.parameter("myParameter").value() // Returns the value of the "myParameter" parameter given in the URI

request.headers() // Returns the header given in the request

request.body() // Returns the body of the request (as a buffer of bytes [Netty ByteBuf])
request.jsonBody(...) // Returns the body of the request as a JSON
```

<br><br>
To send back a response to the client you have multiple possibilities:
```java
// -> Classic
final byte[] content = ...;

response.classic(content, "text/plain", HttpResponseStatus.OK) // Sends a direct bytes response

// -> Text
response.text("Welcome to Hermeus!", "text/plain" HttpResponseStatus.OK) // Sends a text message with a custom content-type and a custom status code
response.text("Welcome to Hermeus!", HttpResponseStatus.OK) // Sends a text message with a custom status code and "plain/text" content-type
response.text("Welcome to Hermeus!") // Sends a text message with OK status code and "plain/text" content-type

// -> HTML
final String html = "<html><head>Hermeus<title></title></head><body><p>Welcome to Hermeus!</p></body></html>";        

response.text(html, HttpResponseStatus.OK) // Sends an HTML page with a custom status code
response.text(html) // Sends an HTML page with OK status code

// -> JSON
final MyObject object = new MyObject("Test");

// Default serializer
response.json(object, HttpResponseStatus.OK) // Sends an object by converting it to JSON + custom status code
response.json(object) // Sends an object by converting it to JSON + OK status code

// Custom serializer
final Gson customSerializer = new GsonBuilder()
        .excludeFieldsWithoutExposeAnnotation()
        .registerTypeHierarchyAdapter(...)
        .create();

response.json(customSerializer, object, HttpResponseStatus.OK) // Sends an object by converting it to JSON + custom status code
response.json(customSerializer, object) // Sends an object by converting it to JSON + OK status code

```

#### Middleware
If you want to create a middleware that will be executed before a handler (e.g. for security checks [token, api-key...]) read this:
```java
final HermeusServer server = ...;
final HermeusRouter router = ...;
final HermeusMiddleware middleware = (request, response) -> request.containsParameter("password") && request.parameter("password").value().equals("p@ssw0rd");

router.get("/welcome", (request, response) -> response.text("Welcome to Hermeus!")).middleware(middleware);
```

Additionally, if you want to apply a middleware to every router's handlers, you can do:
```java
final HermeusServer server = ...;
final HermeusRouter router = ...;
final HermeusMiddleware middleware = (request, response) -> request.containsParameter("password") && request.parameter("password").value().equals("p@ssw0rd");

router.middleware(middleware);
```