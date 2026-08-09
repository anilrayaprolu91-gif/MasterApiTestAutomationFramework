package com.api.framework.http;

import io.restassured.specification.RequestSpecification;



public interface RequestSpecProvider {

    RequestSpecification get();
}