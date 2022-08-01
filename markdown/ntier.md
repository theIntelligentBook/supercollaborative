
## n-Tier software

Talking about architecture in the abstract is all well and good, but we need a project we can develop and that involves choices.

Let's suppose we're developing an n-Tier application:

![Three layers](http://www.plantuml.com/plantuml/svg/TP2n2eCm48RtFCNXr4Ne8oXrTt3eK3gSoL52R0ovQvVITwynbO3Wzil_VvzS6XQCydfEAg1s-22ehZJ2PRBafOAFKW2t_7K7P71QgQyguuxOoHoE0RC3R3ySZ-rkt7eJPskE0Sn08GQARuhRiWrXiXh5pXvuRV8Hl9Ifxr0GK6jYNekkxLUVGS4HETttJycOVkGqrATpdHIcH5iYbhZOSjuagY5lbhV-0G00)

Somehow, the application server has to be able to receive requests from the clients. It needs to present an API over the network.

---

## Communication

For our purposes, let's assume our application will communicate over **HTTP**, using **JSON** as the data format.

This is a reasonably common format for systems running on the internet.


---

## HTTP: A very short guide

A lot of application servers communicate over HTTP *Hypertext transfer protocol*. The headers of this protocol are text, making it good for a quick introduction.

HTTP version 1.1 is a **call and response** API. The client sends requests, and the server responds

![Call and response](https://www.plantuml.com/plantuml/svg/oyXCILL8oyylISgluE9AJ2x9Br9mAielBqujuaf9B4bCIYnELGXEBIe3ya7IcMM99Qbm5L1QGTSEgiPAeIYri3Irk0Ic5guPH85Kwjf1T4FK5XVavnMdSgMaeYiVeaOZn121nEMGcfTIcfi30000)

[WebSockets](https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API) can be used as a web protocol that allows the client or the server to initiate messages at any time. (i.e. "full duplex")

---

### An HTTP request

```http
GET /encrypt?pw=hellothere HTTP/1.1
Host: localhost:9000
Connection: keep-alive
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8
User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.132 Safari/537.36
Accept-Encoding: gzip, deflate, sdch
Accept-Language: en-US,en;q=0.8
```

The first line of the protocol specifies three things:

* A **method** (e.g. `GET`)
* A **path** (`/encrypt?pw=hellothere`)
* The **version** of HTTP the request was made with

The `Host` header indicates what server we're sending the request to. Here, `localhost:9000`

---

### HTTP methods

The HTTP protocol was designed as a "RESTful" protocol - "REpresentational State Transfer".

The term "RESTful" comes from Roy Fielding's PhD thesis, and describes diffferent actions on a resource:

* `GET` - we could fetch it from a location
* `PUT` - we could put a resource at a location
* `DELETE` - we could delete it
* `HEAD` - we could get header information about it, without fetching the data itself
* `POST` - we could send it some information to process

Some methods have different properties.

* **idempotent**. If we `GET` the resource twice, we should get the same result. If we `DELETE` it twice,
  it's just as gone. Idempotent methods should (theoretically) be cacheable and produce the same result if called
  once or multiple times.

* **safe**. `GET`ting a resource doesn't change data (other than logging). Neither does `HEAD`.

---

### Request bodies and MIME types

Some requests have a body (a payload of data) after the header.

If so, there is a `Content-Type` header specifying what sort of data is being sent

```http
POST /path HTTP/1.1
Host: example.com
accept: */*
content-type: text/plain;charset=UTF-8
content-length: 15
```

In this case, `text/plain;charset=UTF-8` indicates ordinary text in the UTF-8 character set.

---

### Responses

HTTP responses also come back with text headers

```http
HTTP/1.1 200 OK
content-type: application/json
content-length: 86
```

In the first line, we have

* The HTTP version
* A **status code** and **status text**. These fall into ranges:
  - 100-199 are information
  - 200-299 are success (e.g. `200 OK`)
  - 300-399 are redirection (e.g. `303 See Other`)
  - 400-499 are client errors (e.g. `404 Not Found`, `403 Forbidden`, `407 Proxy Authentication Required`)
  - 500-599 are server errors (e.g. `500 Internal Server Error`)

There may be a response body, in which case we'll see a `Content-Type` header indicating what sort of data follows.

---

### JSON

As we're dealing with an API rather than writing web pages, we probably want to send data over HTTP.

Often, that data will be represented in JSON: **JavaScript Object Notation**.

A JSON response comes in one of two forms:

* A JSON object, which is a dictionary of keys to values, e.g.  
  ```js
  { 
    "command": "loadClient",
    "clientName": "MuddleClient",
    "arguments": [ 1, 2, 3 ]
  }
  ```

* A JSON array (an array of data), e.g.  
  ```js
  [
    { "name": "Algernon" },
    { "name": "Charlie" }
  ]
  ```

---

### Writing a server API

Writing our server API, then involves

* Defining **handlers** that can respond to requests with particular
  - **actions** (e.g. `GET`) 
  - on particular **routes** (e.g. `/gameServers`)

Many application servers provide a "fluent API" for writing these. For instance, in Vertx, this could be 

```java
router.get("/ping").handler((ctx) -> {
            ctx.response().end("pong");
});
```

* If a `GET` request is received on the `/ping` route, then respond to it with `"pong"`

---

### Path parameters

Sometimes, we might want to extract a parameter from the path. e.g. `gameServers/Muddle` refers to the "Muddle" game server.

There's typically an API provided for extracting these parameters. e.g. In the handler below, we extract the name of a 
game server so we can look it up from our code

```java
router.get("/games/:gameServer").respond((ctx) -> {
    String serverName = ctx.pathParam("gameServer");
    GameServer gs = Main.gameRegistry.getGameServer(serverName);
    GameMetadata[] games = gs.getGamesInProgress();

    /** Vertx/Jackson should turn this into a JSON list, because we're just outputing a simple List<record> */
    return Future.succeededFuture(Arrays.asList(games));
});
```

---

### Serialization and Deserialization

If we write a server in JavaScript, JSON is reasonably natural to use in the code. (It's JavaScript object notation)

If we're writing in another language, e.g. Java or Scala, however, we'll need some help producing and referring to JSON data.

We have a couple of choices:
* Parse JSON into a data structure that specifically represents JSON objects. e.g. `JsonObject`, or
* Try to convert to and from classes we write.

For simple cases, e.g. Java `record`s, a JSON library might be able to convert this automatically.

For more complex cases, we'd either need to write *serialization* and *deserialization* code, or just work with the `JsonObject` types our json library provides. e.g. 

```java
JsonObject json = new JsonObject().put("command", "loadClient").put("clientName", "muddleClient");
```

---

## Parallelism

From the client side, HTTP 1.1 looks like it's "call and response":

![Call and response](https://www.plantuml.com/plantuml/svg/oyXCILL8oyylISgluE9AJ2x9Br9mAielBqujuaf9B4bCIYnELGXEBIe3ya7IcMM99Qbm5L1QGTSEgiPAeIYri3Irk0Ic5guPH85Kwjf1T4FK5XVavnMdSgMaeYiVeaOZn121nEMGcfTIcfi30000)

From the server, side, though, there may be any number of calls coming in at once. The server has to have a means of:

* responding to each request 
* while still being available to receive more requests (without taking too long)

---

### Event loops

A lot of servers work via some form of event loop (or actor-like model):

* Some number of threads are waiting to serve requests
* When an event comes in
  - if it can be handled quickly, it just handles it
  - it it'll take a while, it moves the work onto a background thread until it's complete

The rule of thumb is **don't block the event loop**. If the event loop is blocked for a long time, 
it can't handle other requests that are coming in and the server will seem slow and unresponsive to users.

Different servers have different numbers of event loops. e.g.:

* Node.js runs a single event loop per process
* Vertx runs multiple event loops, typically twice the number of cores your processor has

---

### Asynchronous code - Futures

If we're going to move work to a background thread, we need a way of knowing it is complete.

A `Future<T>` is a computation that will finish at some point in the future.

We typically write code describing what we want to do *when* the computation finishes, in a functional style.

e.g.

* `map` can turn a `Future<A>` into a `Future<B>`
  ```java
  Future<String> stringFuture = doComplexTask();
  stringFuture.map((s) -> s.length()); // Future<Int>
  ``

* `onSuccess` runs code if the `Future` completes successfully
  ```java
  stringFuture.onSuccess((s) -> System.out.println(s));
  ```

* `onFailure` runs code if the `Future` fails in some way
  ```java
  stringFuture.onFailure((ex) -> logger.error("It failed with this exception: {}", ex));
  ```

---

### Blocking vs non-blocking

**Blocking** code is code that can block the current thread. **Non-blocking** code is code that won't.

Just wrapping code in a `Future` doesn't make it non-blocking. `Future` is something that will complete in the future.
It doesn't say what thread it's running on. Someone has to have written some code to put the work on a background thread.
I could write code that runs in the current thread but returns `Future<T>`.

However, *non-blocking* methods will often return a `Future<T>` (or its native Java equivalent, `CompletionStage<T>`)

---

### Typical way of working with `Future<T>`

The typical way of working

* You call a method whose return type is `Future<T>`. Hopefully, whoever wrote that method has made the task run on a worker thread.

* That method schedules the work somewhere and immediately returns an incomplete `Future<T>`. 

* You call `map` or `onSuccess` on the returned `Future<T>`. 

Again, your call returns *immediately*. It hasn't *done the work*, it's just *scheduled the work*. *When* the `Future` completes, your handler will be called.

That lets event loops cycle very quickly, because they're putting all the hard/long-running work on background threads - scheduling the work rather than doing the work.

---

### Event loops in the client

Almost all UI clients also have issues around events and paralellism:

* There is usually **one** thread that renders the UI and calls event handlers (e.g. click handlers)
* If this is blocked for a long time by a long-running task, the UI will appear to *hang* or *freeze*

In the UI, there's typically two actions we need to take:

* Move long-running code off the UI thread, onto a background thread
* Move code that modifies live UI components (e.g. button states) onto the UI thread.

