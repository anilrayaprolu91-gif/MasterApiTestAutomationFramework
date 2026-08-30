package com.api.framework.tests;

import com.api.framework.base.BaseTest;
import com.api.framework.clients.PostApiClient;
import com.api.framework.clients.UserApiClient;
import com.api.framework.models.request.CreatePostRequest;
import com.api.framework.models.response.CreatePostResponse;
import com.api.framework.models.response.JsonPlaceholderUser;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Functional and schema-level tests against the
 * <a href="https://jsonplaceholder.typicode.com/">JSONPlaceholder</a> REST API.
 *
 * <p>JSONPlaceholder is a free, stable, zero-auth public API — making it ideal
 * for CI/CD pipelines that need reliable network access without secret management.
 *
 * <p>Contract assertions (HTTP status, field values) use AssertJ for readable
 * failure messages. Structural correctness is verified against JSON Schema files
 * in {@code src/test/resources/schemas/}.
 */
@Epic("JSONPlaceholder REST API")
@Feature("Users and Posts Resources")
@Owner("API Automation Team")
public class JsonValidationTest extends BaseTest {


    // =========================================================================
    //  Test: List All Users — GET /users
    // =========================================================================

    @Test(description = "GET /users should return HTTP 200 and match JSON schema for user array")
    @Story("List Users")
    @Severity(SeverityLevel.CRITICAL)
    @Description("""
            Verifies that the user list endpoint:
            1) Returns HTTP 200 OK.
            2) Response body (array) matches the declared JSON Schema.
            3) Each user object has a positive id and non-blank email.
            """)
    public void listUsers_shouldReturn200AndMatchSchema() {
        // ── When ─────────────────────────────────────────────────────────────
        Response response = userApiClient.getUsers();

        // ── Then — HTTP contract ──────────────────────────────────────────────
        assertThat(response.getStatusCode())
                .as("Expected HTTP 200 from GET /users")
                .isEqualTo(HttpStatus.SC_OK);

        // ── Then — JSON Schema validation ─────────────────────────────────────
        File schemaFile = new File("src/test/resources/schemas/list-users-response-schema.json");
        response.then().assertThat().body(matchesJsonSchema(schemaFile));

        // ── Then — Domain assertions ──────────────────────────────────────────
        List<JsonPlaceholderUser> users = response.jsonPath().getList("", JsonPlaceholderUser.class);

        assertThat(users)
                .as("User list must not be empty")
                .isNotEmpty();

        users.forEach(user -> {
            assertThat(user.getId()).as("User id must be positive").isPositive();
            assertThat(user.getName()).as("User name must not be blank").isNotBlank();
            assertThat(user.getEmail()).as("User email must not be blank").isNotBlank();
        });

        logger.info("listUsers_shouldReturn200AndMatchSchema passed — {} users returned", users.size());
    }

    // =========================================================================
    //  Test: Get Single User — GET /users/1
    // =========================================================================

    @Test(description = "GET /users/1 should return HTTP 200 with correct user fields")
    @Story("Get Single User")
    @Severity(SeverityLevel.NORMAL)
    @Description("""
            Verifies that fetching a known user by ID:
            1) Returns HTTP 200 OK.
            2) User ID in the body matches the path parameter.
            3) Key string fields are non-blank.
            """)
    public void getUserById_shouldReturn200WithCorrectUser() {
        // ── Given ────────────────────────────────────────────────────────────
        int userId = 1;

        // ── When ─────────────────────────────────────────────────────────────
        Response response = userApiClient.getUserById(userId);

        // ── Then ─────────────────────────────────────────────────────────────
        assertThat(response.getStatusCode())
                .as("Expected HTTP 200 from GET /users/1")
                .isEqualTo(HttpStatus.SC_OK);

        JsonPlaceholderUser user = response.as(JsonPlaceholderUser.class);

        assertThat(user.getId())
                .as("User ID in response must equal the requested ID")
                .isEqualTo(userId);

        assertThat(user.getName()).as("name must not be blank").isNotBlank();
        assertThat(user.getUsername()).as("username must not be blank").isNotBlank();
        assertThat(user.getEmail()).as("email must not be blank").isNotBlank();

        logger.info("getUserById_shouldReturn200WithCorrectUser passed — user='{}'", user.getName());
    }

    // =========================================================================
    //  Test: Get Non-Existent User — GET /users/9999
    // =========================================================================

    @Test(description = "GET /users/9999 should return HTTP 404 for a non-existent user")
    @Story("Get Single User")
    @Severity(SeverityLevel.MINOR)
    @Description("Verifies that requesting a non-existent user ID returns HTTP 404 Not Found.")
    public void getUserById_nonExistent_shouldReturn404() {
        // ── When ─────────────────────────────────────────────────────────────
        Response response = userApiClient.getUserById(9999);

        // ── Then ─────────────────────────────────────────────────────────────
        assertThat(response.getStatusCode())
                .as("Expected HTTP 404 for a non-existent user ID")
                .isEqualTo(HttpStatus.SC_NOT_FOUND);

        logger.info("getUserById_nonExistent_shouldReturn404 passed");
    }

    // =========================================================================
    //  Test: Create Post — POST /posts
    // =========================================================================

    @Test(description = "POST /posts should create a resource and return HTTP 201 with schema-valid JSON")
    @Story("Create Post")
    @Severity(SeverityLevel.BLOCKER)
    @Description("""
            Verifies that the post creation endpoint:
            1) Returns HTTP 201 Created.
            2) Response body matches the declared JSON Schema (structural validation).
            3) The echoed 'title' and 'body' match the values sent in the request.
            4) A server-generated 'id' is present and positive.
            """)
    public void createPost_shouldReturn201AndMatchSchema() {
        // ── Given ────────────────────────────────────────────────────────────
        CreatePostRequest requestBody = CreatePostRequest.builder()
                .title("Exploring REST Assured JSON Schema Validation")
                .body("This post demonstrates contract testing using JSON Schema in Java.")
                .userId(1)
                .build();

        // ── When ─────────────────────────────────────────────────────────────
        Response response = postApiClient.createPost(requestBody);

        // ── Then — HTTP contract ──────────────────────────────────────────────
        assertThat(response.getStatusCode())
                .as("Expected HTTP 201 Created from POST /posts")
                .isEqualTo(HttpStatus.SC_CREATED);

        // ── Then — JSON Schema validation ─────────────────────────────────────
        File schemaFile = new File("src/test/resources/schemas/create-post-response-schema.json");
        response.then().assertThat().body(matchesJsonSchema(schemaFile));

        // ── Then — Domain assertions ──────────────────────────────────────────
        CreatePostResponse body = response.as(CreatePostResponse.class);

        assertThat(body.getTitle())
                .as("Echoed 'title' must match the request payload")
                .isEqualTo(requestBody.getTitle());

        assertThat(body.getBody())
                .as("Echoed 'body' must match the request payload")
                .isEqualTo(requestBody.getBody());

        assertThat(body.getId())
                .as("Server must generate a positive 'id' for the new post")
                .isPositive();

        logger.info("createPost_shouldReturn201AndMatchSchema passed — postId={}", body.getId());
    }

    // =========================================================================
    //  Test: Delete Post — DELETE /posts/1
    // =========================================================================

    @Test(description = "DELETE /posts/1 should return HTTP 200 (JSONPlaceholder simulates deletion)")
    @Story("Delete Post")
    @Severity(SeverityLevel.NORMAL)
    @Description("""
            JSONPlaceholder simulates all write operations; DELETE always returns
            HTTP 200 with an empty JSON object body '{}' rather than 204.
            """)
    public void deletePost_shouldReturn200() {
        // ── When ─────────────────────────────────────────────────────────────
        Response response = postApiClient.deletePost(1);

        // ── Then ─────────────────────────────────────────────────────────────
        assertThat(response.getStatusCode())
                .as("Expected HTTP 200 from DELETE /posts/1 (simulated)")
                .isEqualTo(HttpStatus.SC_OK);

        logger.info("deletePost_shouldReturn200 passed");
    }
}
