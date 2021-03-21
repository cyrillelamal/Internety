package com.cyrillelamal.internety.Serializers;

import com.cyrillelamal.internety.SiteMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.net.URI;
import java.util.Set;

public class XMLSerializer implements SerializerInterface {
    @Override
    public String serialize(SiteMap map) {
        try {
            Document document = XMLSerializer.buildEmptyDocument();

            XMLSerializer.addUrlsetChildren(document, map.getUris());

            return XMLSerializer.xmlToString(document);
        } catch (ParserConfigurationException | TransformerException ignored) {
            return "";
        }
    }

    /**
     * Create a new empty XML document.
     *
     * @return an empty XML document.
     * @throws ParserConfigurationException thrown when the document configuration contains errors.
     */
    protected static Document buildEmptyDocument() throws ParserConfigurationException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    }

    /**
     * Fill the document with URIs.
     *
     * @param document the target document.
     * @param uris     a set of URIs to be listed in the document.
     */
    protected static void addUrlsetChildren(final Document document, final Set<URI> uris) {
        Element urlset = document.createElement("urlset");
        urlset.setAttribute("xmlns", "http://www.sitemaps.org/schemas/sitemap/0.9");
        urlset.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        urlset.setAttribute("xsi:schemaLocation", "http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd");
        document.appendChild(urlset);

        for (var uri : uris) {
            final Element url = document.createElement("url");
            urlset.appendChild(url);

            final Element loc = document.createElement("loc");
            final String txt = uri.toString();
            final Text val = document.createTextNode(txt);
            loc.appendChild(val);

            url.appendChild(loc);
        }
    }

    /**
     * Translate the XML document to string.
     *
     * @param document the document to be translated.
     * @return the contents of the document as a string.
     * @throws TransformerException thrown when the document cannot be translated.
     */
    protected static String xmlToString(final Document document) throws TransformerException {
        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        t.setOutputProperty(OutputKeys.INDENT, "no");

        var ds = new DOMSource(document);
        var w = new StringWriter();
        var sr = new StreamResult(w);

        t.transform(ds, sr);

        return w.getBuffer().toString();
    }
}
