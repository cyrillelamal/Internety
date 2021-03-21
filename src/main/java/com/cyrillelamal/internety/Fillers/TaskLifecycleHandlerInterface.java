package com.cyrillelamal.internety.Fillers;

import com.cyrillelamal.internety.SiteMap;

import java.net.URI;
import java.util.Queue;
import java.util.concurrent.Future;

public interface TaskLifecycleHandlerInterface {
    /**
     * The strategy pattern for different lifecycle handlers.
     *
     * @see AsynchronousFillerInterface#onBeforeTaskCreated
     */
    void onBeforeTaskCreated(final URI uri, final SiteMap siteMap, final Queue<Future<Void>> tasks);
}
