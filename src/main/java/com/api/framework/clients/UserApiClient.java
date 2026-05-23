package com.api.framework.clients;

import com.api.framework.models.request.CreateUserRequest;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

/**
 * API client for the <a href="https://jsonplaceholder.typicode.com/">JSONPlaceholder</a>
 * {@code /users} resource.
 *
 * <p>JSONPlaceholder is a free, stable, unauthenticated REST API that requires no
 * API key — ideal for CI/CD pipelines and portfolio demonstrations.
 */
public class UserApiClient extends BaseApiClient {

    private static final String USERS_ENDPOINT = "/users";

    public UserApiClient() {
        super("base.uri.jsonplaceholder");
    }

    /**
     * Retrieves all users (JSONPlaceholder returns all 10 users in a single array).
     *
     * @return the full HTTP response for assertion in the test layer
     */
    @Step("GET all users")
    public Response getUsers() {
        logger.info("Fetching all users");
        return given(getBaseSpec())
                .when()
                .get(USERS_ENDPOINT)
                .then()
                .extract().response();
    }

    /**
     * Retrieves a single user by their numeric ID.
     *
     * @param userId numeric user identifier (1–10 for known users)
     * @return the full HTTP response
     */
    @Step("GET user by id={userId}")
    public Response getUserById(int userId) {
        logger.info("Fetching user id={}", userId);
        return given(getBaseSpec())
                .when()
                .get(USERS_ENDPOINT + "/{userId}", userId)
                .then()
                .extract().response();
    }
}
