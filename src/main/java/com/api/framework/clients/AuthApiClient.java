package com.api.framework.clients;

import com.api.framework.config.ConfigManager;
import com.api.framework.models.request.AuthRequest;
import com.api.framework.models.response.AuthResponse;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

/**
 * API client for the Restful Booker authentication endpoint.
 */
public class AuthApiClient extends BaseApiClient {

    private static final String AUTH_ENDPOINT = "/auth";

    public AuthApiClient() {
        super("base.uri.restfulbooker");
    }

    /**
     * Creates an authentication token using the configured demo credentials.
     *
     * @return raw HTTP response containing the token payload
     */
    @Step("POST authenticate against Restful Booker with configured credentials")
    public Response createToken() {
        AuthRequest request = AuthRequest.builder()
                .username(ConfigManager.getInstance().getProperty("restfulbooker.username"))
                .password(ConfigManager.getInstance().getProperty("restfulbooker.password"))
                .build();

        logger.info("Generating Restful Booker authentication token");
        return createToken(request);
    }

    /**
     * Creates an authentication token using the supplied credentials.
     *
     * @param request authentication request payload
     * @return raw HTTP response containing the token payload or failure reason
     */
    @Step("POST authenticate against Restful Booker for user '{request.username}'")
    public Response createToken(AuthRequest request) {
        logger.info("Requesting authentication token for Restful Booker user='{}'", request.getUsername());
        return given(getBaseSpec())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(request)
                .when()
                .post(AUTH_ENDPOINT)
                .then()
                .extract().response();
    }

    /**
     * Convenience wrapper that returns only the token string.
     *
     * @return authentication token
     */
    public String createTokenValue() {
        Response response = createToken();
        AuthResponse authResponse = response.as(AuthResponse.class);
        return authResponse.getToken();
    }
}

