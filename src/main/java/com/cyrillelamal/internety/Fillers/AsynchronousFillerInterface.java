package com.cyrillelamal.internety.Fillers;

public interface AsynchronousFillerInterface extends FillerInterface {
    /**
     * Fill the sitemap.
     *
     * @return the fluent interface.
     */
    AsynchronousFillerInterface fill();

    /**
     * Block the execution until the filling is completed.
     */
    void synchronize();
}
