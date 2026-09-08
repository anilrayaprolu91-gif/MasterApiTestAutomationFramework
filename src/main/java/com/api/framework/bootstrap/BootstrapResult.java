package com.api.framework.bootstrap;

import com.api.framework.openapi.ApiEndpoint;

import java.util.List;

public record BootstrapResult(
        String service,
        List<ApiEndpoint> endpoints,
        List<ApiEndpoint> smokeCandidates) {

    public BootstrapResult {
        endpoints = endpoints == null ? List.of() : List.copyOf(endpoints);
        smokeCandidates = smokeCandidates == null ? List.of() : List.copyOf(smokeCandidates);
    }
}
