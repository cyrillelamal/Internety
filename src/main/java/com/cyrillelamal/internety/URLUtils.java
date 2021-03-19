package com.cyrillelamal.internety;

public class URLUtils {
    /**
     * Get the href without its anchor part.
     *
     * @param ref the href that may contain the anchor.
     * @return the passed ref without its anchor part.
     * If the passed ref does not contain any anchor, the passed ref is returned without any modifications.
     * If the passed ref starts with an anchor, an empty string is returned.
     */
    public static String refWithoutAnchor(final String ref) {
        final int idx = ref.indexOf('#');

        if (idx == 0) return "";

        return idx > 0
                ? ref.substring(0, idx - 1)
                : ref;
    }
}
