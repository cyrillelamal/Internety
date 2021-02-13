package com.cyrillelamal.internety.Fillers;

import java.util.List;

public interface FillerInterface {
    /**
     * Parse links from the provided text.
     *
     * @param body the text that may contain links.
     * @return the parsed links.
     */
    List<String> parseRefs(final String body);
}
