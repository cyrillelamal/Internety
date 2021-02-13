package com.cyrillelamal.internety.Fillers;

import com.cyrillelamal.internety.SiteMap;
import org.jsoup.Jsoup;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

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
        var doc = Jsoup.parse(body);
        var tags = doc.select("a");

        List<String> refs = new ArrayList<>();

        for (var a : tags) if (a.hasAttr("href")) refs.add(a.attr("href").trim());

        return refs;
    }

    /**
     * @see AsynchronousFillerInterface#fill
     */
    public FutureFiller fill() {
        URI start = this.getMap().getStart();

        var f = this.launchFuture(start);

        this.getFutures().add(f);

        return this;
    }

    /**
     * @see AsynchronousFillerInterface#synchronize
     */
    public void synchronize() {
        boolean done = false;

        while (!done) {
            done = true;
            for (var f : this.getFutures()) if (!f.isDone()) done = false;
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

        var req = HttpRequest.newBuilder().uri(uri).build();

        return this.getClient().sendAsync(req, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(res -> {
                    for (String href : this.parseRefs(res)) {
                        try {
                            href = FutureFiller.getRefWithoutAnchor(href);

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

    /**
     * Get the href without its anchor part.
     *
     * @param ref the href that may contain the anchor.
     * @return the passed ref without its anchor part.
     * If the passed ref does not contain any anchor, the passed ref is returned without any modifications.
     * If the passed ref starts with an anchor, an empty string is returned.
     */
    protected static String getRefWithoutAnchor(final String ref) {
        var idx = ref.indexOf('#');

        if (idx == 0) return "";

        return idx > 0
                ? ref.substring(0, idx - 1)
                : ref;
    }
}
