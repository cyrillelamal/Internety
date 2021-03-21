package com.cyrillelamal.internety.Serializers;

import com.cyrillelamal.internety.SiteMap;

public interface SerializerInterface {
    /**
     * Serialize the sitemap.
     *
     * @param map the site map to be serialized.
     * @return the print-ready text.
     */
    String serialize(final SiteMap map);
}
