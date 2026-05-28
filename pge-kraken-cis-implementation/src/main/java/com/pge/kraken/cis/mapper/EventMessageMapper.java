package com.pge.kraken.cis.mapper;

import com.pge.kraken.cis.models.event.D1DeviceEventSeeder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class EventMessageMapper {

    private static final String DEFAULT_EXTERNAL_SENDER_ID = "CM-TRILLIANT-HES";
    private static final String NAMESPACE_NS8 = "http://iec.ch/TC57/2009/EndDeviceEvents#";

    public static NodeList extractEventNodes(String xmlContent) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document doc = builder.parse(new InputSource(new StringReader(xmlContent)));

        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();

        // Define namespace prefix mapping
        xpath.setNamespaceContext(new javax.xml.namespace.NamespaceContext() {
            @Override
            public String getNamespaceURI(String prefix) {
                if ("ns8".equals(prefix)) {
                    return NAMESPACE_NS8;
                }
                return javax.xml.XMLConstants.NULL_NS_URI;
            }

            @Override
            public String getPrefix(String namespaceURI) {
                if (NAMESPACE_NS8.equals(namespaceURI)) {
                    return "ns8";
                }
                return null;
            }

            @Override
            public java.util.Iterator getPrefixes(String namespaceURI) {
                return java.util.Collections.emptyIterator();
            }
        });

        // Extract and return EndDeviceEvents
        return (NodeList) xpath.evaluate("//ns8:EndDeviceEvent", doc, XPathConstants.NODESET);
    }

    public static D1DeviceEventSeeder mapEventElement(Element eventElement) throws Exception {
        String category = getElementValue(eventElement, "ns8:category");
        String createdDateTime = getElementValue(eventElement, "ns8:createdDateTime");
        String mRID = getElementValue(eventElement, "ns8:Assets/ns8:mRID");

        if (mRID != null && !mRID.isEmpty() && category != null && !category.isEmpty() && 
            createdDateTime != null && !createdDateTime.isEmpty()) {
            D1DeviceEventSeeder event = new D1DeviceEventSeeder();
            event.setExternalSenderId(DEFAULT_EXTERNAL_SENDER_ID);
            event.setDeviceIdentifierNumber(mRID);
            event.setExternalEventName(category);
            event.setEventDateTime(createdDateTime);
            return event;
        }
        return null;
    }

    private static String getElementValue(Element element, String path) throws Exception {
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();

        xpath.setNamespaceContext(new javax.xml.namespace.NamespaceContext() {
            @Override
            public String getNamespaceURI(String prefix) {
                if ("ns8".equals(prefix)) {
                    return NAMESPACE_NS8;
                }
                return javax.xml.XMLConstants.NULL_NS_URI;
            }

            @Override
            public String getPrefix(String namespaceURI) {
                if (NAMESPACE_NS8.equals(namespaceURI)) {
                    return "ns8";
                }
                return null;
            }

            @Override
            public java.util.Iterator getPrefixes(String namespaceURI) {
                return java.util.Collections.emptyIterator();
            }
        });

        String result = (String) xpath.evaluate(path, element, XPathConstants.STRING);
        return result != null ? result.trim() : null;
    }
}
