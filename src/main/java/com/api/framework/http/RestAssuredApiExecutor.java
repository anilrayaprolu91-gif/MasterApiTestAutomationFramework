package com.api.framework.http;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;


public class RestAssuredApiExecutor implements ApiExecutor {

    private final RequestSpecProvider requestSpecProvider;

    public RestAssuredApiExecutor(
            RequestSpecProvider requestSpecProvider) {

        this.requestSpecProvider = requestSpecProvider;
    }

    @Override
    public Response execute(ApiRequest request) {

        RequestSpecification specification =
                given(requestSpecProvider.get());

        request.getHeaders()
                .forEach(specification::header);

        request.getQueryParams()
                .forEach(specification::queryParam);

        request.getPathParams()
                .forEach(specification::pathParam);

        if (request.getBody() != null) {
            specification.body(request.getBody());
        }

        return specification
                .when()
                .request(
                        request.getMethod(),
                        request.getEndpoint()
                )
                .then()
                .extract()
                .response();
    }
}