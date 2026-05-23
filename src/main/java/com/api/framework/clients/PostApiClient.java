package com.api.framework.clients;

import com.api.framework.models.request.CreatePostRequest;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

/**
 * API client for the <a href="https://jsonplaceholder.typicode.com/">JSONPlaceholder</a>
 * {@code /posts} resource.
 *
 * <p>JSONPlaceholder is a free, stable, unauthenticated REST API widely used
 * for testing and prototyping.
 */
public class PostApiClient extends BaseApiClient {

    private static final String POSTS_ENDPOINT = "/posts";

    public PostApiClient() {
        super("base.uri.jsonplaceholder");
    }

    /**
     * Creates a new post resource.
     *
     * @param request the serialisable request body POJO
     * @return HTTP 201 Created response with the echoed body + server-generated id
     */
    @Step("POST create post — title='{request.title}', userId={request.userId}")
    public Response createPost(CreatePostRequest request) {
        logger.info("Creating post — title='{}', userId={}", request.getTitle(), request.getUserId());
        return given(getBaseSpec())
                .body(request)
                .when()
                .post(POSTS_ENDPOINT)
                .then()
                .extract().response();
    }

    /**
     * Retrieves a single post by its numeric ID.
     *
     * @param postId the post identifier
     * @return the full HTTP response
     */
    @Step("GET post by id={postId}")
    public Response getPostById(int postId) {
        logger.info("Fetching post id={}", postId);
        return given(getBaseSpec())
                .when()
                .get(POSTS_ENDPOINT + "/{postId}", postId)
                .then()
                .extract().response();
    }

    /**
     * Deletes a post by its numeric ID.
     * JSONPlaceholder simulates deletion and always returns HTTP 200 with {@code {}}.
     *
     * @param postId the post identifier
     * @return the full HTTP response
     */
    @Step("DELETE post id={postId}")
    public Response deletePost(int postId) {
        logger.info("Deleting post id={}", postId);
        return given(getBaseSpec())
                .when()
                .delete(POSTS_ENDPOINT + "/{postId}", postId)
                .then()
                .extract().response();
    }
}

