package com.api.framework.clients;

import io.qameta.allure.Step;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.qameta.allure.restassured.AllureRestAssured;
import com.api.framework.config.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static io.restassured.RestAssured.given;

/**
 * Dedicated API client for SOAP/XML web-services.
 *
 * <p>Unlike REST clients, SOAP calls share a single endpoint URL and are
 * distinguished by the {@code SOAPAction} header and XML body template.
 * This client targets the public
 * <a href="https://www.dataaccess.com/webservicesserver/numberconversion.wso">
 * DataAccess NumberConversion WSDL</a>.
 */
public class SoapApiClient {

    private static final Logger logger = LogManager.getLogger(SoapApiClient.class);

    /** Template for a SOAP 1.1 NumberToWords envelope. */
    private static final String NUMBER_TO_WORDS_ENVELOPE =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                    + "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                    + "  <soap:Body>"
                    + "    <NumberToWords xmlns=\"http://www.dataaccess.com/webservicesserver/\">"
                    + "      <ubiNum>%d</ubiNum>"
                    + "    </NumberToWords>"
                    + "  </soap:Body>"
                    + "</soap:Envelope>";

    private final RequestSpecification soapSpec;

    /**
     * Constructs a SOAP client wired to {@code base.uri.soap} from config.properties.
     */
    public SoapApiClient() {
        String baseUri = ConfigManager.getInstance().getProperty("base.uri.soap");

        soapSpec = new RequestSpecBuilder()
                .setBaseUri(baseUri)
                // SOAP 1.1 mandates "text/xml" as the Content-Type; "application/xml" causes HTTP 415.
                .setContentType("text/xml; charset=utf-8")
                .setRelaxedHTTPSValidation()
                .addFilter(new AllureRestAssured())
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        logger.debug("SOAP client initialised — baseUri='{}'", baseUri);
    }

    /**
     * Calls the {@code NumberToWords} SOAP operation.
     *
     * @param number the unsigned integer to convert to a word representation
     * @return the full HTTP response containing the SOAP envelope
     */
    @Step("SOAP NumberToWords — convert {number} to words")
    public Response convertNumberToWords(long number) {
        String soapBody = String.format(NUMBER_TO_WORDS_ENVELOPE, number);
        logger.info("Invoking NumberToWords SOAP operation with number={}", number);

        return given(soapSpec)
                .header("SOAPAction", "\"http://www.dataaccess.com/webservicesserver/NumberToWords\"")
                .body(soapBody)
                .when()
                .post("/NumberConversion.wso")
                .then()
                .extract().response();
    }
}

