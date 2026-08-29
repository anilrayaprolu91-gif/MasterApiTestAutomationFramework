package com.api.framework.factory;

import com.api.framework.clients.AuthApiClient;
import com.api.framework.clients.BookingApiClient;
import com.api.framework.clients.SoapApiClient;
import com.api.framework.clients.UserApiClient;
import com.api.framework.http.ApiExecutor;
import com.api.framework.http.RestAssuredApiExecutor;
import com.api.framework.infrastructure.FrameworkDependencies;

public class ApiClientFactory {

    private final FrameworkDependencies dependencies;

    public ApiClientFactory(
            FrameworkDependencies dependencies) {
        this.dependencies = dependencies;
    }

    public BookingApiClient createBookingApiClient() {

        return new BookingApiClient(
                dependencies.getRestfulBookerExecutor()
        );
    }

    public UserApiClient createUserApiClient() {

        return new UserApiClient(
                dependencies.getRestfulBookerExecutor()
        );
    }

    public AuthApiClient createAuthApiClient() {

        return new AuthApiClient(
                dependencies.getRestfulBookerExecutor()
        );
    }
}