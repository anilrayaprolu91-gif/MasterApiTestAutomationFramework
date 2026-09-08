package com.api.framework.openapi;

public final class DefaultApiRiskClassifier implements ApiRiskClassifier {
    @Override
    public ApiRisk classify(String method) {
        if (method == null || method.isBlank()) {
            return ApiRisk.UNKNOWN;
        }
        return switch (method.toUpperCase()) {
            case "GET", "HEAD", "OPTIONS" -> ApiRisk.SAFE;
            case "POST", "PUT", "PATCH" -> ApiRisk.CONTROLLED_WRITE;
            case "DELETE" -> ApiRisk.DESTRUCTIVE;
            default -> ApiRisk.UNKNOWN;
        };
    }
}
