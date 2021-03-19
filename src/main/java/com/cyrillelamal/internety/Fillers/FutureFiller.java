package com.cyrillelamal.internety.Fillers;

import com.cyrillelamal.internety.SiteMap;
import com.cyrillelamal.internety.URLUtils;
import org.jsoup.Jsoup;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class FutureFiller implements AsynchronousFillerInterface {
    private final SiteMap map;

    private final HttpClient client = HttpClient.newBuilder().build();

    private final Queue<CompletableFuture<Void>> futures = new ConcurrentLinkedQueue<>();

    /**
     * Create an asynchronous filler that uses completable futures.
     *
     * @param map the sitemap to be filled.
     */
    public FutureFiller(final SiteMap map) {
        this.map = map;
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
    public FutureFiller fill() {
        URI start = this.getMap().getStart();

        CompletableFuture<Void> f = this.launchFuture(start); // An active not completed future

        this.getFutures().add(f);

        return this;
    }

    /**
     * @see AsynchronousFillerInterface#synchronize
     */
    public void synchronize() {
        Queue<CompletableFuture<Void>> futures = this.getFutures();

        while (!futures.isEmpty()) {
            CompletableFuture<Void> peek = futures.remove();
            if (peek != null && !peek.isDone()) futures.add(peek);
        }
    }

    /**
     * Get the target map.
     *
     * @return the target map.
     */
    public SiteMap getMap() {
        return this.map;
    }

    /**
     * Start a future for the URI.
     *
     * @param uri the URI that the futures will try to visit.
     * @return the newly created not done future.
     */
    protected CompletableFuture<Void> launchFuture(final URI uri) {
        this.getMap().inscribe(uri); // visited

        HttpRequest req = HttpRequest.newBuilder().uri(uri).build();

        return this.getClient().sendAsync(req, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(res -> {
                    for (String href : this.parseRefs(res)) {
                        try {
                            href = URLUtils.refWithoutAnchor(href);

                            var v = new URI(href);

                            if (!this.getMap().sameHost(v)) continue;

                            v = uri.resolve(href);

                            // Ignore the visited pages.
                            if (this.getMap().inscribe(v)) this.getFutures().add(launchFuture(v));
                        } catch (URISyntaxException ignored) {
                        }
                    }
                });
    }

    /**
     * Encapsulation for the tasks.
     *
     * @return the list of generated futures.
     */
    protected Queue<CompletableFuture<Void>> getFutures() {
        return this.futures;
    }

    /**
     * Encapsulation for the HTTP-client.
     *
     * @return the HTTP client used by all futures.
     */
    private HttpClient getClient() {
        return this.client;
    }
}
