package com.api.framework.base;

import com.api.framework.clients.AuthApiClient;
import com.api.framework.clients.BookingApiClient;
import com.api.framework.clients.UserApiClient;
import com.api.framework.config.ConfigManager;
import com.api.framework.config.PropertiesFileSource;
import com.api.framework.factory.ApiClientFactory;
import com.api.framework.infrastructure.FrameworkDependencies;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

/**
 * Root superclass for every test class in this framework.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Applies global REST Assured configuration (timeouts, object mapper, etc.).</li>
 *   <li>Ensures that all derived test classes benefit from a consistent, repeatable
 *       environment without duplicating setup code (DRY principle).</li>
 * </ul>
 *
 * <p>Note: The {@link io.qameta.allure.restassured.AllureRestAssured} filter is NOT
 * added at the global {@code RestAssured.filters()} level here. It is instead wired
 * inside each {@link com.api.framework.clients.BaseApiClient} subclass so that only
 * API client calls are captured — not internal framework operations such as health
 * checks or teardown calls. This keeps Allure reports clean and meaningful.
 */
public abstract class BaseTest {

    protected final Logger logger = LogManager.getLogger(getClass());


    protected FrameworkDependencies frameworkDependencies;

    protected BookingApiClient bookingApiClient;

    protected UserApiClient userApiClient;
    protected AuthApiClient authApiClient;

    /**
     * Suite-level setup that runs exactly once, before any test method in any class.
     *
     * <p>TestNG guarantees a single invocation even when running tests in parallel,
     * because {@code @BeforeSuite} is scoped to the entire suite, not to a thread.
     */
    @BeforeSuite(alwaysRun = true)
    public void configureSuite() {
        logger.info("========================================================");
        logger.info("  Master API Test Automation Framework — Suite Starting  ");
        logger.info("========================================================");

        int connectionTimeout = ConfigManager.getInstance().getIntProperty("connection.timeout.ms");
        int socketTimeout     = ConfigManager.getInstance().getIntProperty("socket.timeout.ms");

        // Apply global REST Assured timeouts.  Individual client specs may override these.
        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout",   connectionTimeout)
                        .setParam("http.socket.timeout",       socketTimeout)
                        .setParam("http.connection-manager.timeout", connectionTimeout));

        // Enable response URL logging globally (helps diagnose redirect issues in CI).
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        logger.info("REST Assured configured — connectionTimeout={}ms, socketTimeout={}ms",
                connectionTimeout, socketTimeout);
    }

    @BeforeClass
    public void initializeFramework() {

        frameworkDependencies =
                new FrameworkDependencies();

        ApiClientFactory clientFactory =
                frameworkDependencies.getApiClientFactory();

        bookingApiClient =
                clientFactory.createBookingApiClient();

        userApiClient =
                clientFactory.createUserApiClient();

        authApiClient =
                clientFactory.createAuthApiClient();
    }

}

