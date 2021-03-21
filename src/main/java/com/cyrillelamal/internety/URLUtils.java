package com.cyrillelamal.internety;

import java.net.URI;

public class URLUtils {
    /**
     * Remove the anchor part.
     *
     * @param href the href that may contain an anchor part.
     * @return the href without its anchor part.
     * If the passed href does not contain any anchor, the passed ref is returned without any modifications.
     * If the passed href starts with an anchor, an empty string is returned.
     */
    public static String hrefWithoutAnchor(final String href) {
        final int idx = href.indexOf('#');

        return idx == 0 ? "" : idx > 0
                ? href.substring(0, idx - 1)
                : href;
    }

    /**
     * Check if the URIs have the same host.
     *
     * @param origin the original URI against which is compared the other one.
     * @param other  the other URI. It may be a relative URI.
     * @return true, if the provided URIs belong to the same host or the other URI is a relative one.
     */
    public static boolean hasSameHost(final URI origin, final URI other) {
        return other.getHost() == null || other.getHost().equals(origin.getHost());
    }
}
