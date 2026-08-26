package com.api.framework.clients;

import com.api.framework.http.RequestSpecProvider;
import com.api.framework.models.request.AuthRequest;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class AuthApiClient {

    RequestSpecProvider requestSpecProvider;

    public Response createToken(AuthRequest request) {

        return given(requestSpecProvider.get())
                .body(request)
                .when()
                .post("/auth")
                .then()
                .extract()
                .response();
    }
}