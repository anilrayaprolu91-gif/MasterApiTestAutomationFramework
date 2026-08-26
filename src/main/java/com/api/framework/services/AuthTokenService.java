package com.api.framework.services;

import com.api.framework.clients.AuthApiClient;
import com.api.framework.config.ConfigManager;
import com.api.framework.models.request.AuthRequest;
import com.api.framework.models.response.AuthResponse;
import io.restassured.response.Response;

public class AuthTokenService {


    private final AuthApiClient authApiClient;
    private final ConfigManager configManager;

    public AuthTokenService(
            AuthApiClient authApiClient,
            ConfigManager configManager) {

        this.authApiClient = authApiClient;
        this.configManager = configManager;
    }

    public String getToken() {

        AuthRequest request = AuthRequest.builder()
                .username(
                        configManager.getProperty(
                                "restfulbooker.username"))
                .password(
                        configManager.getProperty(
                                "restfulbooker.password"))
                .build();

        Response response =
                authApiClient.createToken(request);

        AuthResponse authResponse =
                response.as(AuthResponse.class);

        return authResponse.getToken();
    }
}
