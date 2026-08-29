package com.api.framework.http;

import com.api.framework.config.ConfigManager;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import io.qameta.allure.restassured.AllureRestAssured;
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
public class SoapRequestSpecProvider implements RequestSpecProvider {

    private static final Logger logger = LogManager.getLogger(SoapRequestSpecProvider.class);


    private final RequestSpecification soapSpec;

    /**
     * Constructs a SOAP client wired to {@code base.uri.soap} from config.properties.
     */
    public SoapRequestSpecProvider(String baseUri) {
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


    @Override
    public RequestSpecification get() {
        return soapSpec;
    }
}

