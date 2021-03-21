package com.cyrillelamal.internety.Fillers;

import com.cyrillelamal.internety.SiteMap;

import java.net.URI;
import java.util.Queue;
import java.util.concurrent.Future;

public interface AsynchronousFillerInterface extends FillerInterface {
    /**
     * Start filling the sitemap.
     *
     * @return the fluent interface.
     */
    AsynchronousFillerInterface fill();

    /**
     * Block the execution until the filling is completed.
     */
    void await();

    /**
     * This method must be called when a new URI has been accepted by the sitemap, but no related tasks were launched.
     * <p>
     * This method enables to create distributed sitemaps, e.g. you can flush the sitemap into a file or await some tasks.
     * All the logic related to the distributed sitemaps stays for your realizations.
     * </p>
     *
     * @param uri     the accepted URI.
     * @param siteMap the site map that has accepted the URI.
     * @param tasks   the queue with the current tasks without the task related to the URI.
     */
    void onBeforeTaskCreated(final URI uri, final SiteMap siteMap, final Queue<Future<Void>> tasks);
}
