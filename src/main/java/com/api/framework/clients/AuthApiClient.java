package com.api.framework.clients;

import com.api.framework.http.ApiExecutor;
import com.api.framework.http.ApiRequest;
import com.api.framework.http.RequestSpecProvider;
import com.api.framework.models.request.AuthRequest;
import io.restassured.http.Method;
import io.restassured.response.Response;

import java.util.Objects;

import static io.restassured.RestAssured.given;

public class AuthApiClient {



    private final ApiExecutor apiExecutor;

    public AuthApiClient(ApiExecutor apiExecutor) {
        this.apiExecutor = Objects.requireNonNull(
                apiExecutor,
                "apiExecutor must not be null"
        );
    }

    public Response createToken(AuthRequest request) {

//        return given(requestSpecProvider.get())
//                .body(request)
//                .when()
//                .post("/auth")
//                .then()
//                .extract()
//                .response();



      return apiExecutor.execute(
                ApiRequest.builder(Method.POST, "/auth")
                        .body(request)
                        .build()
        );
    }
}