package com.api.framework.infrastructure;

import com.api.framework.config.ConfigManager;
import com.api.framework.factory.ApiClientFactory;
import com.api.framework.http.*;
import com.api.framework.services.AuthTokenService;

public  class FrameworkDependencies {

    private final ApiExecutor restfulBookerExecutor;
    private final ApiExecutor soapExecutor;

    private final ApiExecutor jsonPlaceHolderExecutor;
    private final AuthTokenService authTokenService;

    private final ApiClientFactory apiClientFactory;

    public ApiClientFactory getApiClientFactory() {
        return apiClientFactory;
    }

    public ApiExecutor getJsonPlaceHolderExecutor() {
        return jsonPlaceHolderExecutor;
    }



    public ApiExecutor getRestfulBookerExecutor() {
        return restfulBookerExecutor;
    }




    public AuthTokenService getAuthTokenService() {
        return authTokenService;
    }

    public FrameworkDependencies() {

        RequestSpecProvider restfulBookerSpec =
                new RestRequestSpecProvider(
                        "base.uri.restfulbooker"
                );

        RequestSpecProvider jsonPlaceHolderSpec =
                new RestRequestSpecProvider(
                        "base.uri.jsonplaceholder"
                );


        RequestSpecProvider soapSpec = new SoapRequestSpecProvider("base.uri.soap");

        restfulBookerExecutor =
                new RestAssuredApiExecutor(restfulBookerSpec);

       jsonPlaceHolderExecutor =
                new RestAssuredApiExecutor(jsonPlaceHolderSpec);



        soapExecutor =
                new RestAssuredApiExecutor(soapSpec);

        apiClientFactory =
                new ApiClientFactory(this);

        this.authTokenService =
                new AuthTokenService(
                        apiClientFactory.createAuthApiClient()
                );
    }

    public ApiExecutor getSoapExecutor() {
        return soapExecutor;
    }
}