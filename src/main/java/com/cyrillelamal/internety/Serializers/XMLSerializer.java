package com.cyrillelamal.internety.Serializers;

import com.cyrillelamal.internety.SiteMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
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

            addUrlsetChildren(document, map.getUris());

            return XMLSerializer.xmlToString(document);
        } catch (ParserConfigurationException | TransformerException ignored) {
            return "";
        }
    }

    /**
     * Translate the XML document to string.
     *
     * @param document the document to be translated.
     * @return the contents of the document as a string.
     * @throws TransformerException thrown when the document cannot be translated.
     */
    protected static String xmlToString(Document document) throws TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));

        return writer.getBuffer().toString();
    }

    /**
     * Create a new empty XML document.
     *
     * @return an empty XML document.
     * @throws ParserConfigurationException thrown when the document configuration contains errors.
     */
    protected static Document buildEmptyDocument() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        return builder.newDocument();
    }

    /**
     * Fill the document with URIs.
     *
     * @param document the target document.
     * @param uris     a set of URIs to be listed in the document.
     */
    protected static void addUrlsetChildren(Document document, Set<URI> uris) {
        Element urlset = document.createElement("urlset");
        document.appendChild(urlset);
        urlset.setAttribute("xmlns", "http://www.sitemaps.org/schemas/sitemap/0.9");
        urlset.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        urlset.setAttribute("xsi:schemaLocation", "http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd");

        for (var uri : uris) {
            Element url = document.createElement("url");
            urlset.appendChild(url);

            Element loc = document.createElement("loc");
            loc.appendChild(document.createTextNode(uri.toString()));

            url.appendChild(loc);
        }
    }
}
