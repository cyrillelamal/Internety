package com.cyrillelamal.internety.Fillers;

import com.cyrillelamal.internety.SiteMap;
import com.cyrillelamal.internety.URLUtils;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ThreadPoolFiller implements AsynchronousFillerInterface {
    private final SiteMap siteMap;
    private final ExecutorService executor;
    private final TaskLifecycleHandlerInterface tlh;

    private final Queue<Future<Void>> tasks = new LinkedBlockingQueue<>();

    private final HttpClient client = HttpClient.newBuilder().build();

    /**
     * Create an asynchronous filler that uses futures and the executor service.
     *
     * @param siteMap  the sitemap to be filled.
     * @param nThreads the number of simultaneous tasks.
     */
    public ThreadPoolFiller(final SiteMap siteMap, final int nThreads) {
        this(siteMap, nThreads, null);
    }

    /**
     * Create an asynchronous filler that uses futures and the executor service.
     *
     * @param siteMap  the sitemap to be filled.
     * @param nThreads the number of simultaneous tasks.
     * @param tlh      the handler for tasks lifecycle events.
     */
    public ThreadPoolFiller(final SiteMap siteMap, final int nThreads, final TaskLifecycleHandlerInterface tlh) {
        this.siteMap = siteMap;

        this.executor = Executors.newFixedThreadPool(nThreads);

        this.tlh = tlh;
    }

    /**
     * @see FillerInterface#parseRefs
     */
    @Override
    public List<String> parseRefs(final String body) {
        return Jsoup.parse(body).select("a")
                .stream()
                .filter(a -> a.hasAttr("href"))
                .map(a -> a.attr("href").trim())
                .collect(Collectors.toList());
    }

    /**
     * @see AsynchronousFillerInterface#fill
     */
    @Override
    public AsynchronousFillerInterface fill() {
        final URI origin = this.getSiteMap().getStart();

        return this.fill(origin);
    }

    /**
     * Delegate actions to the lifecycle handler.
     *
     * @see AsynchronousFillerInterface#onBeforeTaskCreated
     */
    @Override
    public void onBeforeTaskCreated(final URI uri, final SiteMap siteMap, final Queue<Future<Void>> tasks) {
        final TaskLifecycleHandlerInterface tlh = this.getTaskLifecycleHandler();

        if (tlh != null) {
            tlh.onBeforeTaskCreated(uri, siteMap, tasks);
        }
    }

    /**
     * @see AsynchronousFillerInterface#await
     */
    @Override
    public void await() {
        final Queue<Future<Void>> tasks = this.getTasks();

        boolean empty = tasks.isEmpty();
        while (!empty) {
            final Future<Void> peek = tasks.remove();
            final boolean done = peek.isDone();
            if (!done) tasks.add(peek);
            empty = tasks.isEmpty();
        }

        this.getExecutor().shutdown();
    }

    /**
     * Get the target site map.
     *
     * @return the target site map.
     */
    public SiteMap getSiteMap() {
        return this.siteMap;
    }

    /**
     * Launch a new task using the provided URI.
     *
     * @param uri the target URI.
     * @return the fluent interface.
     */
    protected AsynchronousFillerInterface fill(final URI uri) {
        final SiteMap siteMap = this.getSiteMap();
        final Queue<Future<Void>> queue = this.getTasks();

        this.onBeforeTaskCreated(uri, siteMap, queue);

        final Callable<Void> task = this.newTask(uri);

        final Future<Void> f = this.getExecutor().submit(task);

        this.getTasks().add(f);

        return this;
    }

    /**
     * Create a new task that will visit the provided URI and launch, if it is need, other tasks.
     *
     * @param uri the target URI.
     * @return the task.
     */
    protected Callable<Void> newTask(final URI uri) {
        return () -> {
            final boolean accepted = this.getSiteMap().inscribe(uri);
            if (!accepted) return null;

            HttpRequest req = HttpRequest.newBuilder().uri(uri).build();
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            try {
                final String body = this.getClient().send(req, handler).body();
                final URI origin = this.getSiteMap().getStart();

                final List<String> refs = this.parseRefs(body);
                for (var href : refs) {
                    href = URLUtils.hrefWithoutAnchor(href);

                    var other = new URI(href);

                    final boolean sameHost = URLUtils.hasSameHost(origin, other);
                    if (!sameHost) continue;

                    // The current URI:
                    // 1. Belongs to the origin host;
                    // 2. Has no any anchor part.
                    other = origin.resolve(other);

                    this.fill(other); // The recursion.
                }
            } catch (IOException | InterruptedException | URISyntaxException ignored) {
            }

            return null;
        };
    }

    /**
     * Get the executor service.
     *
     * @return the executor service.
     */
    private ExecutorService getExecutor() {
        return this.executor;
    }

    /**
     * Returns the handler for tasks lifecycle events.
     *
     * @return the handler for tasks lifecycle events.
     */
    private TaskLifecycleHandlerInterface getTaskLifecycleHandler() {
        return this.tlh;
    }

    /**
     * Returns the queue with tasks.
     *
     * @return the queue with tasks.
     */
    private Queue<Future<Void>> getTasks() {
        return this.tasks;
    }

    /**
     * Returns the HTTP-client.
     *
     * @return the HTTP client used by all futures.
     */
    private HttpClient getClient() {
        return this.client;
    }
}
