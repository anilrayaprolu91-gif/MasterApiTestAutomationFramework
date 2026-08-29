package com.api.framework.factory;

import com.api.framework.clients.AuthApiClient;
import com.api.framework.clients.BookingApiClient;
import com.api.framework.clients.SoapApiClient;
import com.api.framework.clients.UserApiClient;
import com.api.framework.config.ConfigManager;
import com.api.framework.http.ApiExecutor;
import com.api.framework.http.RestRequestSpecProvider;
import com.api.framework.http.SoapRequestSpecProvider;

public class RequestSpecFactory {


    public RequestSpecFactory() {

    }



    public RestRequestSpecProvider createRestRequestSpec(String baseUri) {
        return new RestRequestSpecProvider(baseUri);
            }

    public SoapRequestSpecProvider createSoapSpec(String baseUri) {
        return new SoapRequestSpecProvider(baseUri);
    }

}