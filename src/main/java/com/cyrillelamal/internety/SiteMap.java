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
     * Get the text representation of the sitemap.
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
    public String getStartHost() {
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
