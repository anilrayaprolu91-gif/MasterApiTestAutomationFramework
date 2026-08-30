package com.api.framework.clients;

import com.api.framework.http.ApiExecutor;
import com.api.framework.http.ApiRequest;
import com.api.framework.http.RequestSpecProvider;
import com.api.framework.http.RestRequestSpecProvider;
import com.api.framework.models.request.CreatePostRequest;
import io.qameta.allure.Step;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static io.restassured.RestAssured.given;

/**
 * API client for the <a href="https://jsonplaceholder.typicode.com/">JSONPlaceholder</a>
 * {@code /posts} resource.
 *
 * <p>JSONPlaceholder is a free, stable, unauthenticated REST API widely used
 * for testing and prototyping.
 */
public class PostApiClient  {

    private static final String POSTS_ENDPOINT = "/posts";
    private static final Logger logger = LogManager.getLogger(PostApiClient.class);

//    public PostApiClient() {
//        this.restSpec =
//                new RestRequestSpecProvider("base.uri.jsonplaceholder");
//    }



    private final ApiExecutor apiExecutor;

    public PostApiClient(ApiExecutor apiExecutor) {
        this.apiExecutor = apiExecutor;
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
//        return given(restSpec.get())
//                .body(request)
//                .when()
//                .post(POSTS_ENDPOINT)
//                .then()
//                .extract().response();
        return apiExecutor.execute(
                ApiRequest.builder(Method.POST, POSTS_ENDPOINT)
                        .body(request)
                        .build()
                );

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
//        return given(restSpec.get())
//                .when()
//                .get(POSTS_ENDPOINT + "/{postId}", postId)
//                .then()
//                .extract().response();

        return apiExecutor.execute(
                ApiRequest.builder(Method.GET, POSTS_ENDPOINT + "/{postId}")
                        .pathParam("postId", postId) // Key as a String, value as your variable
                        .build()
        );
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
//        return given(restSpec.get())
//                .when()
//                .delete(POSTS_ENDPOINT + "/{postId}", postId)
//                .then()
//                .extract().response();
//

        return apiExecutor.execute(
                ApiRequest.builder(Method.GET, POSTS_ENDPOINT + "/{postId}")
                        .pathParam("postId", postId) // Key as a String, value as your variable
                        .build()
        );
    }
}

