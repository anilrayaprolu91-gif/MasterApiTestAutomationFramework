package com.api.framework.openapi;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class OpenApiAnalyzer {
    private final ApiRiskClassifier riskClassifier;

    public OpenApiAnalyzer(ApiRiskClassifier riskClassifier) {
        this.riskClassifier = riskClassifier;
    }

    public List<ApiEndpoint> analyze(OpenAPI openAPI) {
        if (openAPI == null || openAPI.getPaths() == null) {
            return List.of();
        }

        List<ApiEndpoint> endpoints = new ArrayList<>();
        openAPI.getPaths().forEach((path, pathItem) -> analyzePath(path, pathItem, endpoints));
        return List.copyOf(endpoints);
    }

    private void analyzePath(String path, PathItem pathItem, List<ApiEndpoint> endpoints) {
        if (pathItem == null) {
            return;
        }

        Map<String, Operation> operations = new LinkedHashMap<>();
        operations.put("GET", pathItem.getGet());
        operations.put("POST", pathItem.getPost());
        operations.put("PUT", pathItem.getPut());
        operations.put("PATCH", pathItem.getPatch());
        operations.put("DELETE", pathItem.getDelete());
        operations.put("HEAD", pathItem.getHead());
        operations.put("OPTIONS", pathItem.getOptions());

        operations.forEach((method, operation) -> {
            if (operation == null) {
                return;
            }

            List<ApiParameter> parameters = new ArrayList<>();
            if (pathItem.getParameters() != null) {
                pathItem.getParameters().forEach(parameter -> addParameter(parameters, parameter));
            }
            if (operation.getParameters() != null) {
                operation.getParameters().forEach(parameter -> addParameter(parameters, parameter));
            }

            Map<String, String> responseSchemas = new LinkedHashMap<>();
            if (operation.getResponses() != null) {
                operation.getResponses().forEach((status, response) -> {
                    String schemaName = schemaName(response);
                    if (schemaName != null) {
                        responseSchemas.put(status, schemaName);
                    }
                });
            }

            endpoints.add(new ApiEndpoint(
                    operation.getOperationId(),
                    method,
                    path,
                    operation.getSummary(),
                    operation.getRequestBody() != null,
                    riskClassifier.classify(method),
                    parameters,
                    responseSchemas));
        });
    }

    private void addParameter(List<ApiParameter> parameters, Parameter parameter) {
        if (parameter == null || parameter.getName() == null) {
            return;
        }
        parameters.removeIf(existing -> existing.name().equals(parameter.getName())
                && existing.location().equals(parameter.getIn()));
        parameters.add(new ApiParameter(
                parameter.getName(),
                parameter.getIn(),
                Boolean.TRUE.equals(parameter.getRequired()),
                parameter.getSchema() == null ? "unknown" : schemaType(parameter.getSchema())));
    }

    private String schemaName(ApiResponse response) {
        if (response == null || response.getContent() == null) {
            return null;
        }
        for (var mediaType : response.getContent().values()) {
            if (mediaType == null || mediaType.getSchema() == null) {
                continue;
            }
            return schemaType(mediaType.getSchema());
        }
        return null;
    }

    private String schemaType(Schema<?> schema) {
        if (schema.get$ref() != null) {
            String ref = schema.get$ref();
            int slash = ref.lastIndexOf('/');
            return slash >= 0 ? ref.substring(slash + 1) : ref;
        }
        if (schema.getType() != null) {
            return schema.getType();
        }
        return "unknown";
    }
}
