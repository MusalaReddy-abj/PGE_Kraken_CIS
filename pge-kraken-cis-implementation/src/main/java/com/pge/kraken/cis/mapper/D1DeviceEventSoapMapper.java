package com.pge.kraken.cis.mapper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

public class D1DeviceEventSoapMapper {

    private static final String SOAP_ENV_NAMESPACE = "http://schemas.xmlsoap.org/soap/envelope/";
    private static final String D1_NAMESPACE = "http://ouaf.oracle.com/webservices/d1/D1-DeviceEventSeeder";

    public static String toSoapEnvelope(String payload) throws Exception {
        if (payload == null || payload.isBlank()) {
            payload = "<d1:deviceEventId>unknown</d1:deviceEventId>";
        } else {
            String trimmed = payload.trim();
            if (isSoapEnvelope(trimmed)) {
                return trimmed;
            }
            if (isXml(trimmed)) {
                payload = normalizeDeviceEventXml(trimmed);
            } else {
                payload = "<d1:deviceEventId>" + escapeXml(trimmed) + "</d1:deviceEventId>";
            }
        }

        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<soapenv:Envelope xmlns:soapenv=\"" + SOAP_ENV_NAMESPACE + "\" "
                + "xmlns:d1=\"" + D1_NAMESPACE + "\">\n"
                + "  <soapenv:Header/>\n"
                + "  <soapenv:Body>\n"
                + payload + "\n"
                + "  </soapenv:Body>\n"
                + "</soapenv:Envelope>";
    }

    private static boolean isSoapEnvelope(String payload) {
        String normalized = payload.toLowerCase();
        return normalized.contains("<soapenv:envelope") || normalized.contains("<soap:envelope");
    }

    private static boolean isXml(String payload) {
        try {
            parseXml(payload);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static String normalizeDeviceEventXml(String xml) throws Exception {
        Document document = parseXml(xml);
        Element root = document.getDocumentElement();
        return serializeElement(root);
    }

    private static Document parseXml(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xml)));
    }

    private static String serializeElement(Element element) {
        String name = element.getLocalName() != null ? element.getLocalName() : element.getNodeName();
        StringBuilder builder = new StringBuilder();
        builder.append("<d1:").append(name).append(">");

        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            switch (child.getNodeType()) {
                case Node.ELEMENT_NODE:
                    builder.append(serializeElement((Element) child));
                    break;
                case Node.TEXT_NODE:
                    String text = child.getTextContent();
                    if (text != null && !text.trim().isEmpty()) {
                        builder.append(escapeXml(text.trim()));
                    }
                    break;
                default:
                    break;
            }
        }

        builder.append("</d1:").append(name).append(">");
        return builder.toString();
    }

    private static String escapeXml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
