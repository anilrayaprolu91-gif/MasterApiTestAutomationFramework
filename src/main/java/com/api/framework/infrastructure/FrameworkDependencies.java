package com.api.framework.infrastructure;

import com.api.framework.factory.ApiClientFactory;
import com.api.framework.http.*;

public class FrameworkDependencies {

    private final ApiExecutor restfulBookerExecutor;
    private final ApiExecutor bankingExecutor;
    private final ApiExecutor customerExecutor;

    private final ApiExecutor customerSoapExecutor;

    private final ApiClientFactory apiClientFactory;

    public ApiClientFactory getApiClientFactory() {
        return apiClientFactory;
    }

    public ApiExecutor getCustomerSoapExecutor() {
        return customerSoapExecutor;
    }

    public ApiExecutor getCustomerExecutor() {
        return customerExecutor;
    }

    public ApiExecutor getRestfulBookerExecutor() {
        return restfulBookerExecutor;
    }

    public ApiExecutor getBankingExecutor() {
        return bankingExecutor;
    }

    public FrameworkDependencies() {

        RequestSpecProvider restfulBookerSpec =
                new RestRequestSpecProvider(
                        "base.uri.restfulbooker"
                );

        RequestSpecProvider bankingSpec =
                new RestRequestSpecProvider(
                        "base.uri.banking"
                );

        RequestSpecProvider customerSpec =
                new RestRequestSpecProvider(
                        "base.uri.customer"
                );

        RequestSpecProvider customerSoapSpec =
                new SoapRequestSpecProvider(
                        "base.uri.customer.soap"
                );

        restfulBookerExecutor =
                new RestAssuredApiExecutor(restfulBookerSpec);

        bankingExecutor =
                new RestAssuredApiExecutor(bankingSpec);

        customerExecutor =
                new RestAssuredApiExecutor(customerSpec);

        customerSoapExecutor =
                new RestAssuredApiExecutor(customerSoapSpec);

        apiClientFactory =
                new ApiClientFactory(this);
    }
}