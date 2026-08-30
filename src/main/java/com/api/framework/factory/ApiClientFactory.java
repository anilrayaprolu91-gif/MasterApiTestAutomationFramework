package com.api.framework.factory;

import com.api.framework.clients.*;
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
                dependencies.getJsonPlaceHolderExecutor()
        );
    }

    public AuthApiClient createAuthApiClient() {

        return new AuthApiClient(
                dependencies.getRestfulBookerExecutor()
        );
    }

    public SoapApiClient createSoapApiClient() {

        return new SoapApiClient(
                dependencies.getSoapExecutor()
        );
    }

    public PostApiClient createPostApiClient() {

        return new PostApiClient(
                dependencies.getJsonPlaceHolderExecutor()
        );
    }
}