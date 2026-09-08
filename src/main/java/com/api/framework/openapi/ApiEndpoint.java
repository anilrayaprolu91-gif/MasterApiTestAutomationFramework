package com.api.framework.openapi;

import java.util.List;
import java.util.Map;

public record ApiEndpoint(
        String operationId,
        String method,
        String path,
        String summary,
        boolean hasRequestBody,
        ApiRisk risk,
        List<ApiParameter> parameters,
        Map<String, String> responseSchemas) {

    public ApiEndpoint {
        parameters = parameters == null ? List.of() : List.copyOf(parameters);
        responseSchemas = responseSchemas == null ? Map.of() : Map.copyOf(responseSchemas);
    }
}
