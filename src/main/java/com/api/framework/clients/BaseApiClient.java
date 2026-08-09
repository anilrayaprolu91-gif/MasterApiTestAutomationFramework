package com.api.framework.clients;

import com.api.framework.config.ConfigManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Abstract base for all API clients in this framework.
 *
 * <p>Centralises the construction of a RequestSpecification so that every
 * concrete client inherits the same baseline HTTP configuration.
 */
public abstract class BaseApiClient {

    protected final Logger logger = LogManager.getLogger(getClass());

    private final RequestSpecification baseSpec;

    /**
     * Constructs the shared RequestSpecification for a given base URI key.
     *
     * @param baseUriConfigKey the key in config.properties that holds the base URI
     */
    protected BaseApiClient(String baseUriConfigKey) {
        String baseUri = ConfigManager.getInstance().getProperty(baseUriConfigKey);
        int connectionTimeout = ConfigManager.getInstance().getIntProperty("connection.timeout.ms");

        baseSpec = new RequestSpecBuilder()
                .setBaseUri(baseUri)
                // Restful Booker performs content negotiation from the Accept header.
                // Keeping the default response type explicitly JSON prevents a 418
                // content-negotiation failure when a client does not override it.
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setRelaxedHTTPSValidation()
                .addFilter(new AllureRestAssured())
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        logger.debug("API client initialised — baseUri='{}', connectionTimeout={}ms", baseUri, connectionTimeout);
    }

    /**
     * Returns the baseline RequestSpecification.
     * Concrete clients should merge it with RestAssured.given(getBaseSpec()).
     */
    protected RequestSpecification getBaseSpec() {
        return baseSpec;
    }
}
