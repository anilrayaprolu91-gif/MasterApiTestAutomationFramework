package com.api.framework.smoke;

import com.api.framework.http.ApiExecutor;
import com.api.framework.http.ApiRequest;
import com.api.framework.openapi.ApiEndpoint;
import io.restassured.http.Method;
import io.restassured.response.Response;

public final class ApiSmokeExecutor {
    private final ApiExecutor apiExecutor;

    public ApiSmokeExecutor(ApiExecutor apiExecutor) {
        this.apiExecutor = apiExecutor;
    }

    public SmokeExecutionResult execute(ApiEndpoint endpoint, String baseUri) {
        if (endpoint == null) {
            throw new IllegalArgumentException("endpoint must not be null");
        }
        if (endpoint.risk() != com.api.framework.openapi.ApiRisk.SAFE) {
            throw new IllegalArgumentException("Only SAFE endpoints can be executed by the default smoke executor: "
                    + endpoint.operationId());
        }
        if (endpoint.parameters().stream().anyMatch(p -> p.required() && "path".equalsIgnoreCase(p.location()))) {
            throw new IllegalArgumentException("Required path parameters need explicit smoke data: "
                    + endpoint.operationId());
        }

        String endpointUrl = join(baseUri, endpoint.path());
        ApiRequest request = ApiRequest.builder(
                        Method.valueOf(endpoint.method().toUpperCase()), endpointUrl)
                .header("Accept", "application/json")
                .build();

        Response response = apiExecutor.execute(request);
        boolean passed = response.statusCode() >= 200 && response.statusCode() < 300;
        return new SmokeExecutionResult(
                endpoint.operationId(),
                response.statusCode(),
                passed,
                response.asString());
    }

    private String join(String baseUri, String path) {
        String base = baseUri == null ? "" : baseUri.trim();
        String normalizedBase = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
        String normalizedPath = path == null || path.isBlank() ? "/" : path.startsWith("/") ? path : "/" + path;
        return normalizedBase + normalizedPath;
    }
}
