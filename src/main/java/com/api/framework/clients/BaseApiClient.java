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
 * <p>Centralises the construction of a {@link RequestSpecification} so that
 * every concrete client inherits a consistent baseline: base URI, default
 * content-type, timeout constants, and the Allure logging filter.
 *
 * <p>Subclasses call {@link #getBaseSpec()} to obtain a pre-configured spec
 * and then layer their own endpoint-specific settings on top via
 * {@code RestAssured.given(spec)}.
 */
public abstract class BaseApiClient {

    protected final Logger logger = LogManager.getLogger(getClass());

    private final RequestSpecification baseSpec;

    /**
     * Constructs the shared {@link RequestSpecification} for a given base URI key.
     *
     * @param baseUriConfigKey the key in {@code config.properties} that holds the base URI
     */
    protected BaseApiClient(String baseUriConfigKey) {
        String baseUri = ConfigManager.getInstance().getProperty(baseUriConfigKey);
        int connectionTimeout = ConfigManager.getInstance().getIntProperty("connection.timeout.ms");

        baseSpec = new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .setContentType(ContentType.JSON)
                .addHeader("User-Agent", "Mozilla/5.0")
                .setRelaxedHTTPSValidation()               // Trust all SSL certs — fine for test environments.
                .addFilter(new AllureRestAssured())         // Captures full req/resp into Allure attachments.
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        logger.debug("API client initialised — baseUri='{}', connectionTimeout={}ms", baseUri, connectionTimeout);
    }

    /**
     * Returns the immutable baseline {@link RequestSpecification}.
     * Concrete clients must NEVER mutate this object; they should merge it via
     * {@code RestAssured.given(getBaseSpec())}.
     */
    protected RequestSpecification getBaseSpec() {
        return baseSpec;
    }
}

