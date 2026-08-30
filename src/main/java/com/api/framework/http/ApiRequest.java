package com.api.framework.http;

import io.restassured.http.Method;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ApiRequest {

    private final Method method;
    private final String endpoint;
    private final Map<String, String> headers;
    private final Map<String, Object> queryParams;
    private final Map<String, Object> pathParams;
    private final Object body;

    private ApiRequest(Builder builder) {
        this.method = builder.method;
        this.endpoint = builder.endpoint;
        this.headers = Collections.unmodifiableMap(
                new LinkedHashMap<>(builder.headers)
        );
        this.queryParams = Collections.unmodifiableMap(
                new LinkedHashMap<>(builder.queryParams)
        );
        this.pathParams = Collections.unmodifiableMap(
                new LinkedHashMap<>(builder.pathParams)
        );
        this.body = builder.body;
    }

    public Method getMethod() {
        return method;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, Object> getQueryParams() {
        return queryParams;
    }

    public Map<String, Object> getPathParams() {
        return pathParams;
    }

    public Object getBody() {
        return body;
    }

    public static Builder builder(Method method, String endpoint) {
        return new Builder(method, endpoint);
    }

    public static class Builder {

        private final Method method;
        private final String endpoint;

        private final Map<String, String> headers = new LinkedHashMap<>();
        private final Map<String, Object> queryParams = new LinkedHashMap<>();
        private final Map<String, Object> pathParams = new LinkedHashMap<>();

        private Object body;

        private Builder(Method method, String endpoint) {
            this.method = method;
            this.endpoint = endpoint;
        }

        public Builder header(String name, String value) {
            headers.put(name, value);
            return this;
        }

        public Builder queryParam(String name, Object value) {
            queryParams.put(name, value);
            return this;
        }

        public Builder pathParam(String name, Object value) {
            pathParams.put(name, value);
            return this;
        }

        public Builder body(Object body) {
            this.body = body;
            return this;
        }

        public ApiRequest build() {
            return new ApiRequest(this);
        }
    }
}