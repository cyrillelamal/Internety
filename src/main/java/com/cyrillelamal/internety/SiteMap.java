package com.cyrillelamal.internety;

import com.cyrillelamal.internety.Serializers.SerializerInterface;

import java.net.URI;
import java.util.HashSet;

public class SiteMap {
    private final URI start;

    private final HashSet<URI> uris = new HashSet<>();

    /**
     * Create a new fillable sitemap.
     *
     * @param start the entry point URI.
     */
    public SiteMap(final URI start) {
        this.start = start;
    }

    /**
     * Inscribe the URI in the sitemap.
     *
     * @param u the inscribed URI.
     * @return true, if the URI is successfully inscribed.
     */
    public synchronized boolean inscribe(final URI u) {
        return this.getUris().add(u);
    }

    /**
     * Check if the URI has the same host as the entry point URI.
     *
     * @param u the checked URI.
     * @return true, if the provided URI is a relative URI or it has the same host.
     */
    public boolean sameHost(final URI u) {
        return u.getHost() == null || u.getHost().equals(this.getHost());
    }

    /**
     * Get the resulting sitemap ready to be loaded or printed somewhere.
     *
     * @param serializer a serializer with a specific format.
     * @return the text representation of the sitemap.
     */
    public synchronized String serialize(final SerializerInterface serializer) {
        return serializer.serialize(this);
    }

    /**
     * Get the entry point URI.
     *
     * @return the entry point URI
     */
    public URI getStart() {
        return this.start;
    }

    /**
     * Get the host of the entry point URI.
     *
     * @return the host of the entry point URI.
     */
    public String getHost() {
        return this.getStart().getHost();
    }

    /**
     * Get the internal set of URIs.
     *
     * @return the internal set of URIs.
     */
    public HashSet<URI> getUris() {
        return this.uris;
    }
}
