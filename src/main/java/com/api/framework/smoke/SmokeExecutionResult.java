package com.api.framework.smoke;

public record SmokeExecutionResult(
        String operationId,
        int statusCode,
        boolean passed,
        String responseBody) {
}
