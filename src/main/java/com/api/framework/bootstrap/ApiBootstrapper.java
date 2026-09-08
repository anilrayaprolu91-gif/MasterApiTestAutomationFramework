package com.api.framework.bootstrap;

import com.api.framework.openapi.ApiEndpoint;
import com.api.framework.openapi.OpenApiAnalyzer;
import com.api.framework.openapi.OpenApiReader;
import com.api.framework.smoke.SmokePolicy;
import io.swagger.v3.oas.models.OpenAPI;

import java.util.List;

public final class ApiBootstrapper {
    private final OpenApiReader reader;
    private final OpenApiAnalyzer analyzer;
    private final SmokePolicy smokePolicy;

    public ApiBootstrapper(OpenApiReader reader,
                           OpenApiAnalyzer analyzer,
                           SmokePolicy smokePolicy) {
        this.reader = reader;
        this.analyzer = analyzer;
        this.smokePolicy = smokePolicy;
    }

    public BootstrapResult bootstrap(BootstrapConfiguration configuration) {
        OpenAPI openAPI = reader.read(configuration.openApiSpecification());
        List<ApiEndpoint> endpoints = analyzer.analyze(openAPI);
        List<ApiEndpoint> smokeCandidates = endpoints.stream()
                .filter(smokePolicy::shouldRun)
                .toList();
        return new BootstrapResult(configuration.serviceName(), endpoints, smokeCandidates);
    }
}
