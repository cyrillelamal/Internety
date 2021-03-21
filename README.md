# Internety

Скачать интернеты бесплатно без регистрации и СМС.

A sitemap builder.

## How to...

See an [example of usage](./src/examples/Main.java).

### fill a sitemap?

0. Create the sitemap.

```java
SiteMap map = new SiteMap(new URI("https://host.com/foo/start"));
```

1. Create a filler.

```java
AsynchronousFillerInterface filler = new ThreadPoolFiller(map, 4); // This implementation uses Futures
```

There are no reasons to use a more generic `FillerInterface`, but you can build your implementations for this one.

2. Do fill.

```java
filler.fill();
```

The thread will finish before the futures are completed. So you have to join them.

```java
filler.await(); // or filler.fill().await();
```

3. Get the result, e.g. print it somewhere.

Use implementations of the `SerializerInterface`.

```java
SerializerInterface serializer = new TxtSerializer();
```

There are:

* [Txt serializer](./src/main/java/com/cyrillelamal/internety/Serializers/TxtSerializer.java);
* [XML serializer](./src/main/java/com/cyrillelamal/internety/Serializers/XMLSerializer.java);

It uses the strategy pattern against site maps.

```java
Sting result = serializer.serialize(map);
```

### add your serializer?

Implement the `SerializerInterface` and use it.

### add your filler?

It is highly recommended implementing the `AsynchronousFillerInterface`, e.g. using threads (even only one). Or you can
implement the more generic `FillerInterface`.

### create distributed or heavy sitemap?

At the moment the `AsynchronousFillerInterface` proposes the `onBeforeTaskCreated`
callback which is implemented in the `ThreadPoolFiller` via the `TaskLifecycleHandlerInterface` interface.

More simply, it means that you can pass an implementation of the `TaskLifecycleHandlerInterface` interface to the
constructor of `ThreadPoolFiller`. This implementation will handle inscribed URIs before launch any task and handle
other URIs.
