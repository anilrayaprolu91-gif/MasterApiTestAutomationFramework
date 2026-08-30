package com.api.framework.services;

import com.api.framework.clients.AuthApiClient;
import com.api.framework.config.ConfigManager;
import com.api.framework.models.request.AuthRequest;
import com.api.framework.models.response.AuthResponse;
import io.restassured.response.Response;

public class AuthTokenService {


    private final AuthApiClient authApiClient;

    public AuthTokenService(
            AuthApiClient authApiClient) {

        this.authApiClient = authApiClient;
    }

    public String getToken() {

        AuthRequest request = AuthRequest.builder()
                .username(
                        ConfigManager.getInstance().getProperty(
                                "restfulbooker.username"))
                .password(
                        ConfigManager.getInstance().getProperty(
                                "restfulbooker.password"))
                .build();

        Response response =
                authApiClient.createToken(request);

        AuthResponse authResponse =
                response.as(AuthResponse.class);

        return authResponse.getToken();
    }


    public Response getTokenResponse() {

        AuthRequest request = AuthRequest.builder()
                .username(
                        ConfigManager.getInstance().getProperty(
                                "restfulbooker.username"))
                .password(
                        ConfigManager.getInstance().getProperty(
                                "restfulbooker.password"))
                .build();


        return authApiClient.createToken(request);
    }
}
