package com.api.framework.http;

import io.restassured.response.Response;

public interface ApiExecutor {

    Response execute(ApiRequest request);
}