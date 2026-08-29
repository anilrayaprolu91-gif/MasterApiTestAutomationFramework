package com.api.framework.clients;

import com.api.framework.http.*;
import com.api.framework.models.request.CreateUserRequest;
import io.qameta.allure.Step;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static io.restassured.RestAssured.given;

/**
 * API client for the <a href="https://jsonplaceholder.typicode.com/">JSONPlaceholder</a>
 * {@code /users} resource.
 *
 * <p>JSONPlaceholder is a free, stable, unauthenticated REST API that requires no
 * API key — ideal for CI/CD pipelines and portfolio demonstrations.
 */
public class UserApiClient {

    private static final String USERS_ENDPOINT = "/users";
    private static final Logger logger = LogManager.getLogger(UserApiClient.class);

//    public UserApiClient() {
//        this.restSpec =
//                new RestRequestSpecProvider("base.uri.jsonplaceholder");
//    }
private final ApiExecutor apiExecutor;

     public UserApiClient(ApiExecutor apiExecutor) {
         this.apiExecutor = apiExecutor;
     }

    /**
     * Retrieves all users (JSONPlaceholder returns all 10 users in a single array).
     *
     * @return the full HTTP response for assertion in the test layer
     */
    @Step("GET all users")
    public Response getUsers() {
        logger.info("Fetching all users");
//        return given(restSpec.get())
//                .when()
//                .get(USERS_ENDPOINT)
//                .then()
//                .extract().response();
//
        return apiExecutor.execute(ApiRequest.builder(Method.GET, USERS_ENDPOINT).build());
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
//        return given(restSpec.get())
//                .when()
//                .get(USERS_ENDPOINT + "/{userId}", userId)
//                .then()
//                .extract().response();
//
//
        return apiExecutor.execute(ApiRequest.builder(Method.GET, USERS_ENDPOINT + "/{userId}")
                .pathParam("userId", userId)
                .build());
    }
}
