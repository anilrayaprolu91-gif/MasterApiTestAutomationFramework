package com.api.framework.http;

import com.api.framework.config.ConfigManager;
import com.api.framework.config.PropertiesFileSource;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class RestRequestSpecProvider implements RequestSpecProvider{

    private final RequestSpecification requestSpecification;

    public RestRequestSpecProvider (String baseUriConfigKey) {

        String baseUri =
                ConfigManager.getInstance()
                        .getProperty(baseUriConfigKey);

        requestSpecification = new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .setContentType(ContentType.JSON)
                .addHeader("User-Agent", "Mozilla/5.0")
                .setRelaxedHTTPSValidation()
                .addFilter(new AllureRestAssured())
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @Override
    public RequestSpecification get() {
        return requestSpecification;
    }
}