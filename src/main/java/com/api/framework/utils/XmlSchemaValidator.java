package com.api.framework.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for validating XML strings against an XSD schema.
 *
 * <p>Instances are stateless — all methods are static. The class uses the JDK's
 * built-in {@link javax.xml.validation} API so no extra dependencies are required.
 *
 * <p>Usage examples:
 * <pre>{@code
 *   // Validate a complete XML document:
 *   XmlSchemaValidator.validate(responseBody, "schemas/my-schema.xsd");
 *
 *   // Validate the inner content of a SOAP Body:
 *   String bodyContent = XmlSchemaValidator.extractSoapBodyContent(soapEnvelopeXml);
 *   XmlSchemaValidator.validate(bodyContent, "schemas/my-service-response.xsd");
 * }</pre>
 */
public final class XmlSchemaValidator {

    private static final Logger logger = LogManager.getLogger(XmlSchemaValidator.class);

    /**
     * Pattern that matches the content between the SOAP Body open and close tags,
     * handling both prefixed ({@code soap:Body}) and unprefixed ({@code Body}) forms.
     */
    private static final Pattern SOAP_BODY_CONTENT_PATTERN =
            Pattern.compile(
                    "<(?:\\w+:)?Body[^>]*>\\s*(.*?)\\s*</(?:\\w+:)?Body>",
                    Pattern.DOTALL | Pattern.CASE_INSENSITIVE
            );

    // Utility class — no instances allowed.
    private XmlSchemaValidator() {
        throw new UnsupportedOperationException("XmlSchemaValidator is a utility class.");
    }

    /**
     * Validates the given XML string against an XSD file on the classpath.
     *
     * @param xml              the XML string to validate
     * @param xsdClasspathPath path relative to the classpath root
     *                         (e.g. {@code "schemas/number-to-words-response.xsd"})
     * @throws AssertionError if the XML does not conform to the schema
     */
    public static void validate(String xml, String xsdClasspathPath) {
        logger.debug("Validating XML against XSD: {}", xsdClasspathPath);

        try (InputStream xsdStream = XmlSchemaValidator.class.getClassLoader()
                .getResourceAsStream(xsdClasspathPath)) {

            if (xsdStream == null) {
                throw new IllegalArgumentException(
                        "XSD file not found on classpath: '" + xsdClasspathPath + "'. "
                                + "Verify the file exists in src/test/resources/."
                );
            }

            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            // Disable external entity loading as a security hardening measure.
            factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

            Schema schema = factory.newSchema(new StreamSource(xsdStream));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new StringReader(xml)));

            logger.debug("XML validation passed for schema: {}", xsdClasspathPath);

        } catch (SAXException e) {
            String message = "XML structure does not conform to XSD '" + xsdClasspathPath
                    + "': " + e.getMessage();
            logger.error(message);
            throw new AssertionError(message, e);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "I/O error while reading XSD resource: " + xsdClasspathPath, e);
        }
    }

    /**
     * Extracts the first child element from a SOAP 1.1 / 1.2 {@code Body} element.
     *
     * <p>This allows schema validation to focus on the service-specific response element
     * rather than the generic SOAP envelope wrapper.  The extracted fragment retains its
     * original namespace declarations so it remains a self-contained, valid XML document.
     *
     * @param soapEnvelopeXml the complete SOAP Envelope XML string
     * @return the trimmed XML fragment residing directly inside {@code <soap:Body>}
     * @throws IllegalArgumentException if no SOAP Body content can be located
     */
    public static String extractSoapBodyContent(String soapEnvelopeXml) {
        Matcher matcher = SOAP_BODY_CONTENT_PATTERN.matcher(soapEnvelopeXml);

        if (!matcher.find()) {
            throw new IllegalArgumentException(
                    "Cannot locate SOAP Body content in the provided XML. "
                            + "Verify the response is a well-formed SOAP 1.1/1.2 envelope."
            );
        }

        String bodyContent = matcher.group(1).trim();
        logger.debug("Extracted SOAP Body content ({} chars)", bodyContent.length());
        return bodyContent;
    }
}
