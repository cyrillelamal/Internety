package com.cyrillelamal.internety.Serializers;

import com.cyrillelamal.internety.SiteMap;

import java.net.URI;

public class TxtSerializer implements SerializerInterface {
    public static final String DELIMITER = "\n";

    /**
     * Serialize to the .txt format.
     *
     * @see SerializerInterface#serialize
     */
    @Override
    public String serialize(final SiteMap map) {
        var sb = new StringBuilder();

        for (URI u : map.getUris()) {
            sb.append(u.toString());
            sb.append(TxtSerializer.DELIMITER);
        }

        return sb.toString();
    }
}
