package com.cyrillelamal.internety.Serializers;

import com.cyrillelamal.internety.SiteMap;

public class TxtSerializer implements SerializerInterface {
    /**
     * Serialize to the .txt format.
     *
     * @see SerializerInterface#serialize
     */
    @Override
    public String serialize(final SiteMap map) {
        var sb = new StringBuilder();

        for (var u : map.getUris()) {
            sb.append(u.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
