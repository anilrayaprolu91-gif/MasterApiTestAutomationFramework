package com.api.framework.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

import java.nio.file.Files;
import java.nio.file.Path;

public final class OpenApiReader {
    public OpenAPI read(Path specification) {
        if (specification == null) {
            throw new IllegalArgumentException("OpenAPI specification must not be null");
        }
        if (!Files.isRegularFile(specification)) {
            throw new IllegalArgumentException("OpenAPI specification does not exist: " + specification);
        }

        SwaggerParseResult result = new OpenAPIV3Parser().readLocation(
                specification.toAbsolutePath().toString(), null, null);

        if (result.getMessages() != null && !result.getMessages().isEmpty()) {
            throw new IllegalArgumentException(
                    "Invalid OpenAPI specification: " + String.join("; ", result.getMessages()));
        }
        if (result.getOpenAPI() == null) {
            throw new IllegalArgumentException("Unable to parse OpenAPI specification: " + specification);
        }
        return result.getOpenAPI();
    }
}
